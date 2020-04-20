package com.miro.web.mapper;

import com.miro.repository.params.SearchParams;
import com.miro.web.dto.SearchParamsDto;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

import static com.miro.util.ValidationUtils.allAreNull;

/**
 * Mapper for {@link SearchParamsDto}.
 */
@Mapper
public interface SearchParamsMapper {

    // spring always creates object for request params so we have to check if the object has only null values
    default SearchParams fromDtoWithValuesCheck(SearchParamsDto dto) {

        Object[] fieldsValues = {
                dto.getLowerLeftX(),
                dto.getLowerLeftY(),
                dto.getUpperRightX(),
                dto.getUpperRightY()
        };

        if (allAreNull(fieldsValues)) {
            return null;
        }
        return fromDto(dto);

    }

    SearchParams fromDto(SearchParamsDto dto);

}
