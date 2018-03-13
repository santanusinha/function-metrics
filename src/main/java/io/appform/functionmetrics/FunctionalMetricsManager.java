package io.appform.functionmetrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global metrics manager that needs to be initialized at start
 */
public class FunctionalMetricsManager {
    private static final Logger log = LoggerFactory.getLogger(FunctionalMetricsManager.class.getSimpleName());

    private static MetricRegistry registry;
    private static String prefix;

    private FunctionalMetricsManager() {}

    public static void initialize(String packageName, MetricRegistry registry) {
        log.info("Functional Metric prefix: {}", packageName);
        FunctionalMetricsManager.registry = registry;
        FunctionalMetricsManager.prefix = packageName;
    }

    public static Timer timer(final TimerDomain domain, final FunctionInvocation invocation) {
        Preconditions.checkNotNull(registry, "Please call FunctionalMetricsManager.initialize() to setup metrics collection");
        return registry.timer(
                String.format("%s.%s.%s.%s",
                              prefix,
                              invocation.getClassName(),
                              invocation.getMethodName(),
                              domain.getValue()));
    }
}
