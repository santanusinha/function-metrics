package io.appform.functionmetrics;

import com.codahale.metrics.Timer;
import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Streams;
import javafx.util.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.appform.functionmetrics.FunctionMetricConstants.*;

/**
 * This aspect ensures that only methods annotated with {@link MonitoredFunction} are measured.
 */
@Aspect
@SuppressWarnings("unused")
public class FunctionTimerAspect {
    private static final Logger log = LoggerFactory.getLogger(FunctionTimerAspect.class.getSimpleName());

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
        final MethodSignature methodSignature = MethodSignature.class.cast(callSignature);
        MonitoredFunction monitoredFunction = methodSignature.getMethod().getAnnotation(MonitoredFunction.class);
        final String className = Strings.isNullOrEmpty(monitoredFunction.className())
                                    ? callSignature.getDeclaringType().getSimpleName()
                                    : monitoredFunction.className();
        final String methodName = Strings.isNullOrEmpty(monitoredFunction.method())
                                    ? callSignature.getName()
                                    : monitoredFunction.method();
        final String[] parameterNames = methodSignature.getParameterNames();
        final Options options = FunctionMetricsManager.getOptions();

        String parameterString = "";
        if (options != null && options.isEnableParameterCapture()) {
            List<String> paramValues = Streams.zip(Arrays.stream(methodSignature.getMethod().getParameters()), Arrays.stream(joinPoint.getArgs()), Pair::new)
                    .map(pair -> {
                        MetricTerm metricTerm = pair.getKey().getAnnotation(MetricTerm.class);
                        if (metricTerm == null) {
                            return null;
                        }
                        Object paramValue = pair.getValue();
                        String paramValueStr = convertToString(pair.getValue()).trim();
                        boolean matches = VALID_PARAM_VALUE_PATTERN.matcher(paramValueStr).matches();
                        String sanitizedParamValue = matches ? options.getCaseFormatConverter().convert(paramValueStr) : "";
                        return new Pair<>(metricTerm.order(), sanitizedParamValue);
                    })
                    .filter(Objects::nonNull) // filter parameters that are not metric terms
                    .sorted(Comparator.comparingInt(Pair::getKey)) // sort metric terms by order attribute
                    .map(Pair::getValue) // extract parameter value
                    .collect(Collectors.toList());

            // if and only if after all transformations none of the parameter values are null or empty will we add the parameter string to the metric name
            if (paramValues
                    .stream()
                    .noneMatch(Strings::isNullOrEmpty)) {
                parameterString = Joiner.on(METRIC_DELIMITER).join(paramValues);
            }
        }

        log.trace("Called for class: {} method: {} parameterString: {}", className, methodName, parameterString);
        final FunctionInvocation invocation = new FunctionInvocation(className, methodName, parameterString);
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            Object response = joinPoint.proceed();
            stopwatch.stop();
            FunctionMetricsManager.timer(TimerDomain.SUCCESS, invocation)
                    .ifPresent(timer -> updateTimer(timer, stopwatch));
            return response;
        }
        catch(Throwable t) {
            stopwatch.stop();
            FunctionMetricsManager.timer(TimerDomain.FAILURE, invocation)
                    .ifPresent(timer -> updateTimer(timer, stopwatch));
            throw t;
        }
        finally {
            FunctionMetricsManager.timer(TimerDomain.ALL, invocation)
                    .ifPresent(timer -> updateTimer(timer, stopwatch));
        }
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
        } else if (obj instanceof Enum) {
            return ((Enum) obj).name();
        }
        return "";
    }

}
