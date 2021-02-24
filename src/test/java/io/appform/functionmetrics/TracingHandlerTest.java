package io.appform.functionmetrics;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopScopeManager;
import io.opentracing.noop.NoopSpan;
import io.opentracing.util.GlobalTracer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases related to TracingHandler
 */
public class TracingHandlerTest {

    @Test
    public void testGetTracer() {
        Tracer tracer = TracingHandler.getTracer();
        Assert.assertNotNull(tracer);
    }

    @Test
    public void testStartSpan() {
        Assert.assertNull(TracingHandler.startSpan(null, null, "test", null));
        Span span = TracingHandler.startSpan(GlobalTracer.get(), null, null, "test");
        Assert.assertNotNull(span);
        Assert.assertTrue(span instanceof NoopSpan);
    }

    @Test
    public void testStartScope() {
        Assert.assertNull(TracingHandler.startScope(null, NoopSpan.INSTANCE));
        Assert.assertNull(TracingHandler.startScope(GlobalTracer.get(), null));
        Scope scope = TracingHandler.startScope(GlobalTracer.get(), NoopSpan.INSTANCE);
        Assert.assertNotNull(scope);
        Assert.assertTrue(scope instanceof NoopScopeManager.NoopScope);
    }

}
