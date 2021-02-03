package io.appform.functionmetrics;

import com.google.common.base.Strings;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;

/**
 * Created by kanika.khetawat on 03/02/21
 */
public class TraceManager {

    private Span span;
    private Scope scope;

    void startSpan(final String methodName,
                   final String className,
                   final String parameterString) {
        Span parentSpan = GlobalTracer.get().activeSpan();
        if (parentSpan == null) {
            return;
        }
        span = GlobalTracer.get().buildSpan("method")
                .asChildOf(parentSpan)
                .withTag("method.class", className)
                .withTag("method.name", methodName)
                .start();
        if (!Strings.isNullOrEmpty(parameterString)) {
            span.setTag("method.parameters", parameterString);
        }
        scope = GlobalTracer.get().activateSpan(span);
    }

    void addSuccessTagToSpan() {
        if (span == null) {
            return;
        }
        addStatusTag("SUCCESS");
    }


    void addErrorTagToSpan() {
        if (span == null) {
            return;
        }
        addStatusTag("FAILURE");
    }

    void closeSpan() {
        if (scope != null) {
            scope.close();
        }
        if (span != null) {
            span.finish();
        }
    }

    private void addStatusTag(final String status) {
        span.setTag("method.status", status);
    }
}
