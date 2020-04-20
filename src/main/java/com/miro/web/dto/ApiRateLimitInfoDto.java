package com.miro.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Rate limit config dto.
 */
@Getter
@Setter
public class ApiRateLimitInfoDto {

    private String apiName;

    private long limit;

    private long availableRequests;

    private LocalDateTime nextResetTme;

}
