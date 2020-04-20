package com.miro.web.mapper;

import com.miro.service.throttling.ApiRateLimitInfo;
import com.miro.web.dto.ApiRateLimitInfoDto;
import org.mapstruct.Mapper;

/**
 * Mapper for {@link ApiRateLimitInfo}.
 */
@Mapper
public interface ApiRateLimitInfoMapper {
    ApiRateLimitInfoDto toDto(ApiRateLimitInfo entity);

}
