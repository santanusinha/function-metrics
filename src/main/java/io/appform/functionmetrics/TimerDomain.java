package io.appform.functionmetrics;

/**
 *
 */
public enum TimerDomain {
    SUCCESS("success"),
    FAILURE("failure"),
    ALL("all");

    private final String value;

    TimerDomain(String value) {

        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
