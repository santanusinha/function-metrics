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

import com.codahale.metrics.LockFreeExponentiallyDecayingReservoir;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SlidingTimeWindowArrayReservoir;
import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Global metrics manager that needs to be initialized at start
 */
public class FunctionMetricsManager {
    private static final Logger log = LoggerFactory.getLogger(FunctionMetricsManager.class.getName());
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static Options options = new Options();
    private static MetricRegistry registry;
    private static String prefix;

    private FunctionMetricsManager() {}

    public static void initialize(final String packageName, final MetricRegistry registry) {
        initialize(packageName, registry, options);
    }

    public static void initialize(final String packageName,
                                  final MetricRegistry registry,
                                  final Options options) {
        if (initialized.get()) {
            log.warn("Function metrics already initialized");
            return;
        }
        synchronized (FunctionMetricsManager.class) {
            if (initialized.get()) {
                return;
            }
            log.info("Function Metrics prefix: {}", packageName);
            FunctionMetricsManager.registry = registry;
            FunctionMetricsManager.prefix = packageName;
            FunctionMetricsManager.options = options;
            if (options.isEnableParameterCapture() && options.isDisableCacheOptimisation()) {
                log.warn("Enabling caching for method annotations because enableParameterCapture flag is set to true");
                options.setDisableCacheOptimisation(false);
            }
        }
        initialized.set(true);
    }

    public static Optional<Timer> timer(final TimerDomain domain, final FunctionInvocation invocation) {
        if(!initialized.get()) {
            log.warn("Please call FunctionMetricsManager.initialize() to setup metrics collection. No metrics will be pushed.");
            return Optional.empty();
        }
        final String metricName = options.isEnableParameterCapture() && !Strings.isNullOrEmpty(invocation.getParameterString())
                ? prefix + "." + invocation.getClassName() + "." + invocation.getMethodName() + "." + invocation.getParameterString() + "." + domain.getValue()
                : prefix + "." + invocation.getClassName() + "." + invocation.getMethodName() + "." + domain.getValue();
        return Optional.of(registry.timer(metricName, () -> {
            switch (options.getTimerType()) {
                case DECAYING:
                    return new Timer(LockFreeExponentiallyDecayingReservoir.builder().build());
                case SLIDING:
                default:
                    // The correct behaviour is to throw an IllegalStateException here. However, it is not advisable to
                    // fail actual method calls, so it is better to use the default timer type
                    return new Timer(new SlidingTimeWindowArrayReservoir(60, TimeUnit.SECONDS));
            }
        }));
    }

    public static Options getOptions() {
        return options;
    }
}
