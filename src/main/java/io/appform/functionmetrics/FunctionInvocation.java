package io.appform.functionmetrics;

/**
 *
 */
public class FunctionInvocation {
    private final String className;
    private final String methodName;

    public FunctionInvocation(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }
}
