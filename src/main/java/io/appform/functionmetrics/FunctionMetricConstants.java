package io.appform.functionmetrics;

import java.util.regex.Pattern;

public interface FunctionMetricConstants {
    String METRIC_DELIMITER = ".";
    Pattern NON_ALPHABET_CHARS_PATTERN = Pattern.compile("[^a-zA-Z]");
}
