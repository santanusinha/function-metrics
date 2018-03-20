package io.appform.functionmetrics;

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

    public void pubFunction(int i) {
        myFunction(i);
    }

    public void nonTimedFunction() {
        System.out.println("Blah Blah");
    }
}
