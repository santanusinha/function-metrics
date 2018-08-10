package io.appform.functionmetrics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MonitoredFunction {
    /**
     * Override the classname being pushed into metrics.
     * @return Class name if provided otherwise the actual class name is used.
     */
    String className() default "";

    /**
     * Override the method name being passed. Otherwise the actual method name is used.
     * @return Method name if provided, otherwise actual method name is used.
     */
    String method() default "";
}
