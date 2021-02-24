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
     * Override the global tracing enabled flag.
     * @return True if tracing is disabled, otherwise false as the default value.
     */
    boolean disableTracing() default false;
}
