package io.appform.functionmetrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class FunctionTimerAspectTest {

    @Test
    public void testMetricsCollection() throws Exception {
        final MetricRegistry registry = SharedMetricRegistries.getOrCreate("test-metrics");
        FunctionalMetricsManager.initialize("phonepe.test", registry);
        final MyClass myClass = new MyClass();
        myClass.myFunction(1);
        myClass.nonTimedFunction();
        myClass.myFunction(3);

        final FunctionInvocation myFunctionInvocation
                = new FunctionInvocation("MyClass", "myFunction");
        final FunctionInvocation nonTimedFunctionInvocation
                = new FunctionInvocation("MyClass", "nonTimedFunction");
        Assert.assertEquals(0, FunctionalMetricsManager.timer(TimerDomain.FAILURE, myFunctionInvocation).getCount());
        Assert.assertEquals(2, FunctionalMetricsManager.timer(TimerDomain.SUCCESS, myFunctionInvocation).getCount());
        Assert.assertEquals(2, FunctionalMetricsManager.timer(TimerDomain.ALL, myFunctionInvocation).getCount());
        Assert.assertEquals(0, FunctionalMetricsManager.timer(TimerDomain.ALL, nonTimedFunctionInvocation).getCount());

        try {
            myClass.myFunction(2);
        } catch (Exception e) {
            Assert.assertEquals(1, FunctionalMetricsManager.timer(TimerDomain.FAILURE, myFunctionInvocation).getCount());
            Assert.assertEquals(2, FunctionalMetricsManager.timer(TimerDomain.SUCCESS, myFunctionInvocation).getCount());
            Assert.assertEquals(3, FunctionalMetricsManager.timer(TimerDomain.ALL, myFunctionInvocation).getCount());
        }
    }

}