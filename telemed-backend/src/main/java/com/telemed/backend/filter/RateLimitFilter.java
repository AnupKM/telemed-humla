package com.telemed.backend.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket(int capacity, int refillTokens, Duration duration) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(capacity,
                        Refill.greedy(refillTokens, duration)))
                .build();
    }

    private Bucket resolveBucket(String key, int capacity, int refillTokens, Duration duration) {
        return cache.computeIfAbsent(key, k -> createNewBucket(capacity, refillTokens, duration));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip = request.getRemoteAddr();

        Bucket bucket;

        if (path.contains("/api/auth/login")) {
            bucket = resolveBucket(ip + ":login", 5, 5, Duration.ofMinutes(1));
        } else if (path.contains("/api/auth/refresh")) {
            bucket = resolveBucket(ip + ":refresh", 10, 10, Duration.ofMinutes(1));
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.getWriter().write("Too many requests");
        }
    }
}