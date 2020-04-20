package com.miro.web.controller;

import com.miro.entity.Widget;
import com.miro.exception.EntityNotFoundException;
import com.miro.exception.ValidationException;
import com.miro.service.WidgetService;
import com.miro.util.ValidationUtils;
import com.miro.web.dto.SearchParamsDto;
import com.miro.web.dto.WidgetDto;
import com.miro.web.mapper.SearchParamsMapper;
import com.miro.web.mapper.WidgetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Objects;

import static com.miro.config.RestConfiguration.BASE_PREFIX;
import static com.miro.util.ValidationUtils.*;

/**
 * REST API for Widgets.
 */
@RestController
@RequestMapping(BASE_PREFIX + "/widgets")
@RequiredArgsConstructor
public class WidgetController {

    private final WidgetService widgetService;
    private final WidgetMapper widgetMapper;
    private final SearchParamsMapper searchParamsMapper;

    @GetMapping
    public Page<WidgetDto> list(
            @PageableDefault(sort = "z", direction = Sort.Direction.ASC) Pageable pageable,
            SearchParamsDto params) {
        validateParams(params);
        return widgetService.list(pageable, searchParamsMapper.fromDtoWithValuesCheck(params)).map(widgetMapper::toDto);
    }

    @GetMapping("/{id}")
    public WidgetDto findById(@PathVariable("id") Integer id) {
        Widget widget = widgetService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, Widget.class));
        return widgetMapper.toDto(widget);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WidgetDto create(@Valid @RequestBody WidgetDto widgetDto) {
        if (widgetDto.getId() != null) {
            throw new ValidationException("Entity should not contain filled id to create instance");
        }
        Widget widget = widgetService.create(widgetMapper.fromDto(widgetDto));
        return widgetMapper.toDto(widget);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Integer id) {
        widgetService.delete(id);
    }

    @PutMapping("/{id}")
    public WidgetDto update(@Valid @RequestBody WidgetDto widgetDto,
                            @PathVariable("id") Long id) {
        widgetDto.setId(id);
        Widget updated = widgetService.update(widgetMapper.fromDto(widgetDto));
        return widgetMapper.toDto(updated);
    }

    private void validateParams(SearchParamsDto params) {
        Object[] fieldsValues = {
                params.getLowerLeftX(),
                params.getLowerLeftY(),
                params.getUpperRightX(),
                params.getUpperRightY()
        };

        boolean valuesAreNull = allAreNull(fieldsValues);
        boolean valuesAreNotNull = allAreNotNull(fieldsValues);

        if (!valuesAreNull && !valuesAreNotNull) {
            throw new ValidationException("Search params (x, y, width, height) all should be either empty " +
                    "or filled with values");
        }
    }


}
