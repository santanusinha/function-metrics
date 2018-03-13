package io.appform.functionmetrics;

/**
 *
 */
public class MyClass {
    @MonitoredFunction
    public void myFunction(int val) {
        if(val % 2 == 0) {
            throw new RuntimeException("Test exception");
        }
    }

    public void nonTimedFunction() {
        System.out.println("Blah Blah");
    }
}
