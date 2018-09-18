package io.appform.functionmetrics;

import java.util.regex.Pattern;

public interface FunctionMetricConstants {
    String METRIC_DELIMITER = ".";
    Pattern VALID_PARAM_VALUE_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z_0-9]*$");
}
