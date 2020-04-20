package com.miro.service.throttling;

import com.miro.config.properties.ApplicationProperties;
import com.miro.exception.EntityNotFoundException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link RateLimiterService}.
 *
 * Service registers and stores data required for throttling implementation.
 */
@Service
@RequiredArgsConstructor
public class RateLimiterServiceImpl implements RateLimiterService {

    private static final Duration DURATION = Duration.ofMinutes(1);

    private final ApplicationProperties applicationProperties;
    final private Map<HandlerMethod, LocalBucket> buckets = new HashMap<>();
    final private Map<String, HandlerMethod> handlers = new HashMap<>();

    void registerApi(HandlerMethod handlerMethod) {
        Integer defaultRateLimit = applicationProperties.getDefaultRateLimit();

        Refill refill = Refill.intervally(defaultRateLimit, DURATION);
        Bandwidth limit = Bandwidth.classic(defaultRateLimit, refill);
        LocalBucket bucket = Bucket4j.builder().addLimit(limit).build();

        buckets.put(handlerMethod, bucket);
        handlers.put(handlerMethod.getBeanType().getSimpleName() + "-" + handlerMethod.getMethod().getName(), handlerMethod);

    }

    @Override
    public List<ApiRateLimitInfo> getConfigsForAllApis() {
        List<ApiRateLimitInfo> result = new ArrayList<>();
        for (Map.Entry<String, HandlerMethod> entry : handlers.entrySet()) {
            LocalBucket bucket = buckets.get(entry.getValue());
            ApiRateLimitInfo apiRateLimitInfo = new ApiRateLimitInfo();
            apiRateLimitInfo.setApiName(entry.getKey());
            apiRateLimitInfo.setAvailableRequests(bucket.getAvailableTokens());
            apiRateLimitInfo.setLimit(bucket.getConfiguration().getBandwidths()[0].getCapacity());
            apiRateLimitInfo.setNextResetTme(
                    LocalDateTime.now().plusNanos(bucket.estimateAbilityToConsume(1).getNanosToWaitForRefill())
            );
            result.add(apiRateLimitInfo);

        }
        return result;
    }

    @Override
    public ApiRateLimitInfo getApiRateLimitInfo(String apiName) {
        LocalBucket bucket = Optional.of(buckets.get(handlers.get(apiName)))
                .orElseThrow(() -> new EntityNotFoundException(apiName, ApiRateLimitInfo.class));

        ApiRateLimitInfo apiRateLimitInfo = new ApiRateLimitInfo();
        apiRateLimitInfo.setApiName(apiName);
        apiRateLimitInfo.setAvailableRequests(bucket.getAvailableTokens());
        apiRateLimitInfo.setLimit(bucket.getConfiguration().getBandwidths()[0].getCapacity());
        apiRateLimitInfo.setNextResetTme(
                LocalDateTime.now().plusNanos(bucket.estimateAbilityToConsume(1).getNanosToWaitForRefill())
        );
        return apiRateLimitInfo;
    }

    @Override
    public ApiRateLimitInfo updateRateLimit(String apiName, long value) {
        LocalBucket bucket = Optional.of(buckets.get(handlers.get(apiName)))
                .orElseThrow(() -> new EntityNotFoundException(apiName, ApiRateLimitInfo.class));
        Refill refill = Refill.intervally(value, DURATION);
        Bandwidth limit = Bandwidth.classic(value, refill);

        bucket.replaceConfiguration(new BucketConfiguration(Collections.singletonList(limit)));

        return getApiRateLimitInfo(apiName);
    }

    @Override
    public LocalBucket getBucket(HandlerMethod handlerMethod) {
        return buckets.get(handlerMethod);
    }
}
