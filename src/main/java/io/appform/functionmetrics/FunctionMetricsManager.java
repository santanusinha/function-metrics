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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SlidingTimeWindowArrayReservoir;
import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Global metrics manager that needs to be initialized at start
 */
@Slf4j
public class FunctionMetricsManager {
    private static MetricRegistry registry;
    private static String prefix;
    private static Options options;

    public static Optional<Options> getOptions() {
        return Optional.ofNullable(options);
    }

    private FunctionMetricsManager() {}

    public static void initialize(final String packageName, final MetricRegistry registry) {
        initialize(packageName, registry, new Options());
    }

    public static void initialize(final String packageName, final MetricRegistry registry, final Options options) {
        log.info("Functional Metric prefix: {}", packageName);
        FunctionMetricsManager.registry = registry;
        FunctionMetricsManager.prefix = packageName;
        FunctionMetricsManager.options = options;
    }

    public static Optional<Timer> timer(final TimerDomain domain, final FunctionInvocation invocation) {
        if(null == registry) {
            log.warn("Please call FunctionalMetricsManager.initialize() to setup metrics collection. No metrics will be pushed.");
            return Optional.empty();
        }
        MetricRegistry.MetricSupplier<Timer> metricSupplier = () -> new Timer(new SlidingTimeWindowArrayReservoir(60, TimeUnit.SECONDS));
        if (options.isEnableParameterCapture() && !Strings.isNullOrEmpty(invocation.getParameterString())) {
            return Optional.of(registry.timer(
                    String.format("%s.%s.%s.%s.%s",
                            prefix,
                            invocation.getClassName(),
                            invocation.getMethodName(),
                            invocation.getParameterString(),
                            domain.getValue()),
                    metricSupplier));
        }
        else {
            return Optional.of(registry.timer(
                    String.format("%s.%s.%s.%s",
                            prefix,
                            invocation.getClassName(),
                            invocation.getMethodName(),
                            domain.getValue()),
                    metricSupplier));
        }
    }
}
