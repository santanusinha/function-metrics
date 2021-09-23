/*
 * Copyright (c) 2019 Santanu Sinha <santanu.sinha@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.appform.functionmetrics;

import com.codahale.metrics.Timer;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.appform.functionmetrics.FunctionMetricConstants.METRIC_DELIMITER;
import static io.appform.functionmetrics.FunctionMetricConstants.VALID_PARAM_VALUE_PATTERN;
import static io.appform.functionmetrics.FunctionMetricsManager.timer;

/**
 * This aspect ensures that only methods annotated with {@link MonitoredFunction} are measured.
 */
@Aspect
@SuppressWarnings("unused")
public class FunctionTimerAspect {
    private static final Logger log = LoggerFactory.getLogger(FunctionTimerAspect.class.getSimpleName());

    private final Map<String, MethodData> paramCache = new ConcurrentHashMap<>();

    @Pointcut("@annotation(io.appform.functionmetrics.MonitoredFunction)")
    public void monitoredFunctionCalled() {
        //Empty as required
    }

    @Pointcut("execution(* *(..))")
    public void anyFunctionCalled() {
        //Empty as required
    }

    @Around("monitoredFunctionCalled() && anyFunctionCalled()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        final Signature callSignature = joinPoint.getSignature();

        final MethodData methodData = cacheDisabled()
                                      ? createMethodData(callSignature)
                                      : paramCache.computeIfAbsent(callSignature.toLongString(),
                                                                   key -> createMethodData(callSignature));
        final FunctionInvocation invocation = createFunctionInvocation(methodData, joinPoint, callSignature);

        final Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            final Object response = joinPoint.proceed();
            stopwatch.stop();
            timer(TimerDomain.SUCCESS, invocation).ifPresent(timer -> updateTimer(timer, stopwatch));
            return response;
        }
        catch (Throwable t) {
            stopwatch.stop();
            timer(TimerDomain.FAILURE, invocation).ifPresent(timer -> updateTimer(timer, stopwatch));
            throw t;
        }
        finally {
            timer(TimerDomain.ALL, invocation).ifPresent(timer -> updateTimer(timer, stopwatch));
        }
    }

    private boolean cacheDisabled() {
        return FunctionMetricsManager.getOptions().map(Options::isDisableCacheOptimisation).orElse(false);
    }

    private MethodData createMethodData(Signature callSignature) {
        final MethodSignature methodSignature = (MethodSignature) callSignature;
        final MonitoredFunction monitoredFunction = methodSignature.getMethod().getAnnotation(MonitoredFunction.class);
        final String className = Strings.isNullOrEmpty(monitoredFunction.className())
                                 ? callSignature.getDeclaringType().getSimpleName()
                                 : monitoredFunction.className();
        final String methodName = Strings.isNullOrEmpty(monitoredFunction.method())
                                  ? callSignature.getName()
                                  : monitoredFunction.method();
        return new MethodData(className, methodName);
    }

    private FunctionInvocation createFunctionInvocation(
            final MethodData methodData, final ProceedingJoinPoint joinPoint, final Signature callSignature) {
        final MethodSignature methodSignature = (MethodSignature) callSignature;
        final Options options = FunctionMetricsManager.getOptions().orElse(null);

        final String className = methodData.getClassName();
        final String methodName = methodData.getMethodName();
        final String parameterString = createParamString(className, methodName, joinPoint, methodSignature, options)
                .orElse("");

        log.trace("Called for class: {} method: {} parameterString: {}",
                  className, methodName, parameterString);
        return new FunctionInvocation(className, methodName, parameterString);
    }

    private Optional<String> createParamString(
            String className,
            String methodName,
            ProceedingJoinPoint joinPoint,
            MethodSignature methodSignature,
            Options options) {
        if (options != null && options.isEnableParameterCapture()) {
            if (methodSignature.getMethod().getParameterCount() != joinPoint.getArgs().length) {
                log.warn(
                        "Unusual scenario - number of parameters in method signature doesn't match with args supplied in " +
                                "runtime, so skipping parameter capture altogether in metric name for this invocation " +
                                "[class = {}, method = {}]",
                        className,
                        methodName);
                return Optional.empty();
            }
        }
        else {
            return Optional.empty();
        }
        final List<String> paramValues = IntStream.range(0, methodSignature.getMethod().getParameterCount())
                .mapToObj(i -> {
                    final MetricTerm metricTerm = methodSignature.getMethod()
                            .getParameters()[i].getAnnotation(MetricTerm.class);
                    if (metricTerm == null) {
                        return null;
                    }
                    final String paramValueStr = convertToString(joinPoint.getArgs()[i]).trim();
                    final String sanitizedParamValue = VALID_PARAM_VALUE_PATTERN.matcher(paramValueStr).matches()
                                                       ? options.getCaseFormatConverter().convert(paramValueStr)
                                                       : "";
                    return new Pair<>(metricTerm.order(), sanitizedParamValue);
                })
                .filter(Objects::nonNull) // filter parameters that are not metric terms
                .sorted(Comparator.comparingInt(Pair::getKey)) // sort metric terms by order attribute
                .map(Pair::getValue) // extract parameter value
                .collect(Collectors.toList());
        // if and only if after all transformations none of the parameter values are null or
        // empty will we add the parameter string to the metric name
        if (paramValues
                .stream()
                .noneMatch(Strings::isNullOrEmpty)) {
            return Optional.of(Joiner.on(METRIC_DELIMITER).join(paramValues));
        }
        return Optional.empty();
    }

    private void updateTimer(Timer timer, Stopwatch stopwatch) {
        timer.update(stopwatch.elapsed(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
    }

    private String convertToString(Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        else if (obj instanceof Enum) {
            return ((Enum<?>) obj).name();
        }
        return "";
    }

}
