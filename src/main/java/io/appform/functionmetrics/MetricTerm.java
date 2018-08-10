package io.appform.functionmetrics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add this annotation to formal parameters of any method annotated with {@link MonitoredFunction} in order to use the parameter
 * values for generating the metric name that is instrumented
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface MetricTerm {
}
