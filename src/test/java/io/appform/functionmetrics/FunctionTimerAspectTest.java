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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import com.google.common.base.CaseFormat;
import com.google.common.base.Stopwatch;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
public class FunctionTimerAspectTest {

    private final static MetricRegistry registry = SharedMetricRegistries.getOrCreate("test-metrics");

    @BeforeClass
    public static void setup() {
        FunctionMetricsManager.initialize(
                "phonepe.test",
                registry,
                new Options.OptionsBuilder()
                        .enableParameterCapture(true)
                        .caseFormatConverter(CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL))
                        .build());
    }

    @Test
    public void testMetricsCollection() throws Exception {
        final MyClass myClass = new MyClass();
        myClass.pubFunction(1);
        myClass.nonTimedFunction();
        myClass.pubFunction(3);

        final FunctionInvocation myFunctionInvocation
                = new FunctionInvocation("MyClass", "myFunction", "");
        final FunctionInvocation nonTimedFunctionInvocation
                = new FunctionInvocation("MyClass", "nonTimedFunction", "");
        final Timer failureTimer
                = FunctionMetricsManager.timer(TimerDomain.FAILURE, myFunctionInvocation).orElse(null);
        Assert.assertEquals(0, failureTimer.getCount());
        final Timer successTimer
                = FunctionMetricsManager.timer(TimerDomain.SUCCESS, myFunctionInvocation).orElse(null);
        Assert.assertEquals(2, successTimer.getCount());
        final Timer allTimer = FunctionMetricsManager.timer(TimerDomain.ALL, myFunctionInvocation).orElse(null);
        Assert.assertEquals(2, allTimer.getCount());
        Assert.assertEquals(0,
                            FunctionMetricsManager.timer(TimerDomain.ALL, nonTimedFunctionInvocation).orElse(null).getCount());

        try {
            myClass.pubFunction(2);
        } catch (Exception e) {
            Assert.assertEquals(1, failureTimer.getCount());
            Assert.assertEquals(2, successTimer.getCount());
            Assert.assertEquals(3, allTimer.getCount());
        }
    }


    @Test
    public void testMetricsCollectionCustomName() throws Exception {
        final MyClass myClass = new MyClass();
        myClass.myFunction(2,3);

        final FunctionInvocation invocation
                = new FunctionInvocation("MyClass", "myOverloadedFunction", "");
        final Timer failureTimer
                = FunctionMetricsManager.timer(TimerDomain.FAILURE, invocation).orElse(null);
        Assert.assertNotNull(failureTimer);
        Assert.assertEquals(0, failureTimer.getCount());
        final Timer successTimer
                = FunctionMetricsManager.timer(TimerDomain.SUCCESS, invocation).orElse(null);
        Assert.assertNotNull(successTimer);
        Assert.assertEquals(1, successTimer.getCount());
    }

    @Test
    public void testMetricsCollectionParameterValid() throws Exception {
        final MyClass myClass = new MyClass();
        myClass.parameterValidFunction("a","John_Cartier047");

        final FunctionInvocation invocation
                = new FunctionInvocation("MyClass", "parameterValidFunction", "a.johnCartier047");
        final Timer failureTimer
                = FunctionMetricsManager.timer(TimerDomain.FAILURE, invocation).orElse(null);
        Assert.assertNotNull(failureTimer);
        Assert.assertEquals(0, failureTimer.getCount());
        final Timer successTimer
                = FunctionMetricsManager.timer(TimerDomain.SUCCESS, invocation).orElse(null);
        Assert.assertNotNull(successTimer);
        Assert.assertEquals(1, successTimer.getCount());
    }

    @Test
    public void testMetricsCollectionParameterValid_NoArgs() throws Exception {
        final MyClass myClass = new MyClass();
        myClass.parameterValidFunction();

        final FunctionInvocation invocation
                = new FunctionInvocation("MyClass", "parameterValidNoArgsFunction", "");
        final Timer failureTimer
                = FunctionMetricsManager.timer(TimerDomain.FAILURE, invocation).orElse(null);
        Assert.assertNotNull(failureTimer);
        Assert.assertEquals(0, failureTimer.getCount());
        final Timer successTimer
                = FunctionMetricsManager.timer(TimerDomain.SUCCESS, invocation).orElse(null);
        Assert.assertNotNull(successTimer);
        Assert.assertEquals(1, successTimer.getCount());
    }

    @Test
    public void testMetricsCollectionParameterInvalid() throws Exception {
        final MyClass myClass = new MyClass();
        myClass.parameterInvalidFunction("a",5);

        final FunctionInvocation invocation
                = new FunctionInvocation("MyClass", "parameterInvalidFunction",  "");
        final Timer failureTimer
                = FunctionMetricsManager.timer(TimerDomain.FAILURE, invocation).orElse(null);
        Assert.assertNotNull(failureTimer);
        Assert.assertEquals(0, failureTimer.getCount());
        final Timer successTimer
                = FunctionMetricsManager.timer(TimerDomain.SUCCESS, invocation).orElse(null);
        Assert.assertNotNull(successTimer);
        Assert.assertEquals(1, successTimer.getCount());
    }

    @Test
    public void testMetricsCollectionParameterInvalid_VarArgs() throws Exception {
        final MyClass myClass = new MyClass();
        myClass.parameterInvalidFunction("a", "b", "c", "d");

        final FunctionInvocation invocation
                = new FunctionInvocation("MyClass", "parameterInvalidVarArgsFunction",  "");
        final Timer failureTimer
                = FunctionMetricsManager.timer(TimerDomain.FAILURE, invocation).orElse(null);
        Assert.assertNotNull(failureTimer);
        Assert.assertEquals(0, failureTimer.getCount());
        final Timer successTimer
                = FunctionMetricsManager.timer(TimerDomain.SUCCESS, invocation).orElse(null);
        Assert.assertNotNull(successTimer);
        Assert.assertEquals(1, successTimer.getCount());
    }

    @Test
    public void testOverloadCaching() throws Exception {
        final MyClass myClass = new MyClass();
        IntStream.range(0,10).forEach(i -> myClass.overLoadF(1,2));
        IntStream.range(0,10).forEach(i -> myClass.overLoadF(1,"XX"));

        final FunctionInvocation invocation
                = new FunctionInvocation("MyClass", "overLoadF", "");
        final Timer failureTimer
                = FunctionMetricsManager.timer(TimerDomain.FAILURE, invocation).orElse(null);
        Assert.assertNotNull(failureTimer);
        Assert.assertEquals(0, failureTimer.getCount());
        final Timer successTimer
                = FunctionMetricsManager.timer(TimerDomain.SUCCESS, invocation).orElse(null);
        Assert.assertNotNull(successTimer);
        Assert.assertEquals(20, successTimer.getCount());
        //This is 20 because params are not identified individually
    }

    @Test
    public void testCachingMT() {
        final double avgTime = runMTTest();
        System.out.println("Time taken for MT test: " + avgTime);
        FunctionMetricsManager.initialize(
                "phonepe.test",
                registry,
                new Options.OptionsBuilder()
                        .enableParameterCapture(true)
                        .caseFormatConverter(CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL))
                        .disableCacheOptimisation()
                        .build());
        final double avgTimeNoCache = runMTTest();
        System.out.println("Time taken for MT test without cache: " + avgTimeNoCache);
        FunctionMetricsManager.initialize(
                "phonepe.test",
                registry,
                new Options.OptionsBuilder()
                        .enableParameterCapture(true)
                        .caseFormatConverter(CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL))
                        .disableCacheOptimisation(false)
                        .build());
        Assert.assertTrue(avgTimeNoCache > avgTime);
    }

    private double runMTTest() {
        final int numThreads = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        final MyClass myClass = new MyClass();
        final List<Future<Long>> futures = IntStream.range(0, numThreads)
                .mapToObj(i -> executorService.submit(() -> {
                    final Stopwatch stopwatch = Stopwatch.createStarted();
                    IntStream.range(0, 1000_000).forEach(j -> myClass.pubFunction(1));
                    final long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
                    System.out.println("Done test: " + i + " elapsed: " + elapsed);
                    return elapsed;
                }))
                .collect(Collectors.toList());
        final ExecutorCompletionService<Long> cs = new ExecutorCompletionService<>(executorService);
        final long total = futures.stream()
                .mapToLong(f -> {
                    try {
                        return f.get();
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return 0;
                    }
                    catch (ExecutionException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .sum();
        return ((double) total) / numThreads;
    }
}