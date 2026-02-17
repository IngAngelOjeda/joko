package io.github.jokoframework.myproject.web.context;

public class RequestContext {

    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

    private RequestContext() {}

    public static void setCorrelationId(String correlationId) {
        CORRELATION_ID.set(correlationId);
    }

    public static String getCorrelationId() {
        return CORRELATION_ID.get();
    }

    public static void clear() {
        CORRELATION_ID.remove();
    }

}
