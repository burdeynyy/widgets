package com.miro.service.throttling;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Rate limit info for an API.
 */
@Getter
@Setter
public class ApiRateLimitInfo {

    private String apiName;

    private long limit;

    private long availableRequests;

    private LocalDateTime nextResetTme;
}
