package io.appform.functionmetrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class FunctionTimerAspectTest {

    @Test
    public void testMetricsCollection() throws Exception {
        final MetricRegistry registry = SharedMetricRegistries.getOrCreate("test-metrics");
        FunctionMetricsManager.initialize("phonepe.test", registry);
        final MyClass myClass = new MyClass();
        myClass.myFunction(1);
        myClass.nonTimedFunction();
        myClass.myFunction(3);

        final FunctionInvocation myFunctionInvocation
                = new FunctionInvocation("MyClass", "myFunction");
        final FunctionInvocation nonTimedFunctionInvocation
                = new FunctionInvocation("MyClass", "nonTimedFunction");
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
            myClass.myFunction(2);
        } catch (Exception e) {
            Assert.assertEquals(1, failureTimer.getCount());
            Assert.assertEquals(2, successTimer.getCount());
            Assert.assertEquals(3, allTimer.getCount());
        }
    }

}