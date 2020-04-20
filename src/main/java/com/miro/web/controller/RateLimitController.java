package com.miro.web.controller;

import com.miro.service.throttling.RateLimiterService;
import com.miro.web.dto.ApiRateLimitInfoDto;
import com.miro.web.dto.RateLimitDto;
import com.miro.web.mapper.ApiRateLimitInfoMapper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.miro.config.RestConfiguration.BASE_PREFIX;

/**
 * REST API for Rate Limits.
 */
@RestController
@RequestMapping(BASE_PREFIX+ "/rateLimits")
@RequiredArgsConstructor
public class RateLimitController {

    private final RateLimiterService rateLimiterService;
    private final ApiRateLimitInfoMapper apiRateLimitInfoMapper;

    @GetMapping
    public List<ApiRateLimitInfoDto> list() {
        return rateLimiterService.getConfigsForAllApis()
                .stream()
                .map(apiRateLimitInfoMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{apiName}")
    public ApiRateLimitInfoDto findByApiName(@PathVariable("apiName") String apiName) {
        return apiRateLimitInfoMapper.toDto(rateLimiterService.getApiRateLimitInfo(apiName));
    }

    @ApiOperation(
            value = "Updates rate limit for API by its name",
            notes = "New value comes into effect from the next time window"
    )
    @PutMapping("/{apiName}")
    public ApiRateLimitInfoDto update(@Valid @RequestBody RateLimitDto rateLimitDto,
                                      @PathVariable("apiName") String apiName) {
        return apiRateLimitInfoMapper.toDto(rateLimiterService.updateRateLimit(apiName, rateLimitDto.getValue()));
    }

}
