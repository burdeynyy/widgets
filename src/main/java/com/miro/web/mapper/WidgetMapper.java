package com.miro.web.mapper;

import com.miro.entity.Widget;
import com.miro.web.dto.WidgetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for {@link Widget}.
 */
@Mapper
public interface WidgetMapper {
    WidgetDto toDto(Widget entity);

    @Mapping(target = "modificationDate", ignore = true)
    Widget fromDto(WidgetDto dto);
}
