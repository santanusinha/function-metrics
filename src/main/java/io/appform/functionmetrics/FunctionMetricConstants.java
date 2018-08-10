package io.appform.functionmetrics;

import java.util.regex.Pattern;

public interface FunctionMetricConstants {
    String METRIC_DELIMITER = ".";
    Pattern ALLOWED_PARAM_VALUE_CHARS = Pattern.compile("[^_a-zA-Z]");
}
