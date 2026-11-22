package com.nativeverificationappbe.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Executes before Spring Security and Controllers
public class MdcLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_REQUEST_ID = "requestId";
    private static final String MDC_SESSION_ID = "sessionId";
    private static final String MDC_USER_ID = "userId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Generate or extract Request ID
            String requestId = request.getHeader(REQUEST_ID_HEADER);
            if (requestId == null || requestId.isBlank()) {
                requestId = UUID.randomUUID().toString().substring(0, 8); // 8-char unique ID
            }
            MDC.put(MDC_REQUEST_ID, requestId);

            // Return Request ID in response headers for client tracing
            response.setHeader(REQUEST_ID_HEADER, requestId);

            // 2. Extract Session ID and User ID if an active session exists
            HttpSession session = request.getSession(false);
            if (session != null) {
                MDC.put(MDC_SESSION_ID, session.getId());

                Object userId = session.getAttribute("USER_ID");
                MDC.put(MDC_USER_ID, userId != null ? userId.toString() : "ANONYMOUS");
            } else {
                MDC.put(MDC_SESSION_ID, "NONE");
                MDC.put(MDC_USER_ID, "ANONYMOUS");
            }

            // Continue handling the request
            filterChain.doFilter(request, response);

        } finally {
            // Crucial: Clear MDC to prevent thread pool pollution in Tomcat
            MDC.clear();
        }
    }
}
