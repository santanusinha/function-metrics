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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add this annotation to formal parameters of any method annotated with {@link MonitoredFunction} in order to use the parameter
 * values for generating the metric name that is instrumented.
 * Let's suppose we have a function foobar which is annotated with {@link MonitoredFunction}
 *
 * {@code
 *  package io.appform.functionmetrics;
 *
 *  public class Foo {
 *
 *      \\@MonitoredFunction
 *      public void bar(int a, \\@MetricTerm String b, long c) {
 *          \\ do some computation
 *          return 42;
 *      }
 *  }
 * }
 * Imagine the bar function was invoked as follows: bar(10, "Blah-b1@h_ ", 131513).
 * Then, the metric name will be {prefix}.io.appform.functionmetrics.Foo.bar.blahbh.$domain.
 * Here, $domain is an enum that takes a value in SET {SUCCESS, FAILURE, ALL}.
 * Note how the parameter value is trimmed, lower-cased and stripped of non-alphabetic characters.
 * If there were multiple parameters annotated with {@link MetricTerm} in the function bar, then their string values are simply joined together by a dot.
 * The order of formal parameters in the method signature is maintained in generating the concatenated metric name component for parameter values.
 * NOTE: Formal parameters annotated with this annotation must be of type {@code String or @code Enum}, otherwise the string value of parameter is ignored
 * Also, formal parameter values must match this regexp ^[a-zA-Z_][a-zA-Z_0-9]*. See {@link FunctionMetricConstants}.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface MetricTerm {
    // set order to control the order in which parameter string values are concatenated to form the metric name
    // the parameter values are sorted in ascending order of this param to derive the metric name
    // setting explicit order helps to maintain consistent metric names across formal parameter sequence changes in method signature
    int order() default 0;
}
