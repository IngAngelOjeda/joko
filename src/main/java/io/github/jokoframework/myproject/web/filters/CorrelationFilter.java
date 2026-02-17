package io.github.jokoframework.myproject.web.filters;

import io.github.jokoframework.myproject.web.context.RequestContext;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId = request.getHeader(HEADER);

        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        try {
            RequestContext.setCorrelationId(correlationId);

            MDC.put(MDC_KEY, correlationId);

            response.setHeader(HEADER, correlationId);

            filterChain.doFilter(request, response);

        } finally {
            MDC.remove(MDC_KEY);
            RequestContext.clear();
        }
    }
}
