package com.miro.web.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

/**
 * Rate limit value dto.
 */
@Getter
@Setter
public class RateLimitDto {

    @Min(0)
    private long value;

}
