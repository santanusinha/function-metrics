package io.appform.functionmetrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import com.google.common.base.CaseFormat;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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

}