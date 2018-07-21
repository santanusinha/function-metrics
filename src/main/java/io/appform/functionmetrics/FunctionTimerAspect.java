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

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        final Parameter[] parameters = methodSignature.getMethod().getParameters();
        String parameterString = "";
        if (parameterNames != null && parameterNames.length > 0) {
            List<String> dynamicPrefixComponents = IntStream.range(0, parameterNames.length)
                    .mapToObj(i -> {
                        String parameterName = parameterNames[i];
                        Object[] args = joinPoint.getArgs();
                        if (args != null && i < args.length) {
                            Object arg = args[i];
                            if (arg instanceof String) {
                                return String.class.cast(arg);
                            } else {
                                return "";
                            }
                        }
                        return "";
                    })
                    .collect(Collectors.toList());
            if (dynamicPrefixComponents
                    .stream()
                    .noneMatch(Strings::isNullOrEmpty)) {
                parameterString = Joiner.on(".").join(dynamicPrefixComponents);
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

}
