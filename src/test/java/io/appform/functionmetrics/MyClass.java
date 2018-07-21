package io.appform.functionmetrics;

import com.google.common.base.Joiner;

import java.util.Objects;

/**
 *
 */
public class MyClass {
    @MonitoredFunction
    private void myFunction(int val) {
        if(val % 2 == 0) {
            throw new RuntimeException("Test exception");
        }
    }

    @MonitoredFunction(method = "myOverloadedFunction")
    public void myFunction(int x, int y) {
        System.out.println("Val: " + Objects.toString(x + y));
    }

    @MonitoredFunction(args = {"x", "y"})
    public void parameterValidFunction(String x, String y) {
        System.out.println(String.format("x = %s, y = %s", x, y));
    }

    @MonitoredFunction(args = {"x", "y"})
    public void parameterInvalidFunction(String x, int y) {
        System.out.println(String.format("x = %s, y = %d", x, y));
    }

    @MonitoredFunction(method = "parameterInvalidVarArgsFunction", args = {"x", "y"})
    public void parameterInvalidFunction(String x, String... y) {
        System.out.println(String.format("x = %s, y = [%s]", x, Joiner.on(",").join(y)));
    }

    @MonitoredFunction(method = "parameterValidNoArgsFunction")
    public void parameterValidFunction() {
        System.out.println("No args");
    }

    public void pubFunction(int i) {
        myFunction(i);
    }

    public void nonTimedFunction() {
        System.out.println("Blah Blah");
    }
}
