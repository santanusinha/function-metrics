package io.appform.functionmetrics;

/**
 *
 */
public class FunctionInvocation {
    private final String className;
    private final String methodName;
    private final String parameterString;

    public FunctionInvocation(String className, String methodName, String parameterString) {
        this.className = className;
        this.methodName = methodName;
        this.parameterString = parameterString;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getParameterString() {
        return parameterString;
    }
}
