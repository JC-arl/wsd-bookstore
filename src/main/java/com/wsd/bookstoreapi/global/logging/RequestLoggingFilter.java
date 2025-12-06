package com.wsd.bookstoreapi.global.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        long startTime = System.currentTimeMillis();
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();

        try {
            chain.doFilter(request, response);
        } finally {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            int status = httpResponse.getStatus();
            long elapsed = System.currentTimeMillis() - startTime;

            log.info("[REQUEST] {} {} -> status={} elapsed={}ms",
                    method, uri, status, elapsed);
        }
    }
}
