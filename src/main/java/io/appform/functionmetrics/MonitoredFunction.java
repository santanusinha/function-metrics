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

    /**
     * Override the method parameter names used for computing metric name.
     * The values represented by the parameter names are added in specified order to metric name after className (dot separated).
     * Parameters must be non-null and non-empty and must strictly be of type String.
     * For example,
     * {@code
     *     package com.foo.bar;
     *
     *     public class Foobar {
     *          \\@MonitoredFunction(args = {})
     *          public void foobar(String param1, String param2) {
     *              return 42;
     *          }
     *     })
     * }
     * The fully qualified name of the metric will be com.foo.bar.Foobar.foobar.$param1.$param2.$domain.
     * Here $param1, $param2 refer to the string value of param1 and param2 respectively.
     * $domain is one of {SUCCESS, FAILURE, ALL}.
     * Note: If any actual parameter in function call is either empty or null or of a different type other than String,
     * $param1.$param2 will effectively be empty so actual name of metric becomes com.foo.bar.Foobar.foobar.$domain.
     * @return Method parameter names whose values are used for generating metric name if provided, otherwise empty array
     */
    String[] args() default {};
}
