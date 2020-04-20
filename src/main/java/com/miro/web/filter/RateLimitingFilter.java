package com.miro.web.filter;

import com.miro.service.throttling.RateLimiterService;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.local.LocalBucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.miro.config.RestConfiguration.BASE_PREFIX;

/**
 * Filter to check that APIs rate limits are not exceeded.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitingFilter implements Filter {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final RateLimiterService rateLimiterService;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (req.getRequestURI().startsWith(BASE_PREFIX)) {
            try {
                HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(req);
                if (handlerExecutionChain != null) {
                    HandlerMethod handler = (HandlerMethod) handlerExecutionChain.getHandler();
                    LocalBucket bucket = rateLimiterService.getBucket(handler.getResolvedFromHandlerMethod());
                    ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
                    addRateLimitHeaders(res,
                            Long.toString(bucket.getConfiguration().getBandwidths()[0].getCapacity()),
                            Long.toString(bucket.getAvailableTokens()),
                            LocalDateTime.now().plusNanos(probe.getNanosToWaitForRefill()).toString()
                    );
                    if (!probe.isConsumed()) {
                        res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429
                        res.setContentType("text/plain");
                        res.getWriter().append("Too many requests");
                        return;
                    }
                }
            } catch (Exception e) {
                // org.springframework.web.servlet.handler.AbstractHandlerMapping.getHandler declares very
                // general checked exception...
                log.debug("Throttling filter exception", e);
            }
        }

        chain.doFilter(req, res);
    }

    private void addRateLimitHeaders(HttpServletResponse response,
                                     String limit,
                                     String remaining,
                                     String resetDateTime) {
        response.addHeader("X-Rate-Limit-Limit", limit);
        response.addHeader("X-Rate-Limit-Remaining ", remaining);
        response.addHeader("X-Rate-Limit-Reset", resetDateTime);
    }

}