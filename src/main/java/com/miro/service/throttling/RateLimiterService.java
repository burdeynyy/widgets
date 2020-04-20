package com.miro.service.throttling;

import io.github.bucket4j.local.LocalBucket;
import org.springframework.web.method.HandlerMethod;

import java.util.List;

/**
 * Service to work with APIs rate limits.
 */
public interface RateLimiterService {
    List<ApiRateLimitInfo> getConfigsForAllApis();

    ApiRateLimitInfo getApiRateLimitInfo(String apiName);

    ApiRateLimitInfo updateRateLimit(String apiName, long value);

    LocalBucket getBucket(HandlerMethod handlerMethod);
}
