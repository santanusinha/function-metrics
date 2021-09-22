/*
 * Copyright (c) 2019 Santanu Sinha <santanu.sinha@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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

    @MonitoredFunction()
    public void parameterValidFunction(@MetricTerm String x, @MetricTerm String y) {
        System.out.println(String.format("x = %s, y = %s", x, y));
    }

    @MonitoredFunction()
    public void parameterInvalidFunction(@MetricTerm String x, @MetricTerm int y) {
        System.out.println(String.format("x = %s, y = %d", x, y));
    }

    @MonitoredFunction()
    public void parameterInvalidFunction(@MetricTerm int x, @MetricTerm int y) {
        System.out.println(String.format("x = %d, y = %d", x, y));
    }

    @MonitoredFunction(method = "parameterInvalidVarArgsFunction")
    public void parameterInvalidFunction(@MetricTerm String x, @MetricTerm String... y) {
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

    @MonitoredFunction
    public void overLoadF(int x, int y) {
        System.out.println(String.format("X = %d Y = %d", x,y));
    }

    @MonitoredFunction
    public void overLoadF(int x, String y) {
        System.out.println(String.format("X = %d Y = %s", x, y));
    }

    void perfFunction() {}
}
