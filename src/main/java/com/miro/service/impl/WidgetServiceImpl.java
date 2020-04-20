package com.miro.service.impl;

import com.miro.entity.Widget;
import com.miro.repository.WidgetRepository;
import com.miro.repository.params.SearchParams;
import com.miro.service.WidgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * {@inheritDoc}.
 */
@RequiredArgsConstructor
@Service
public class WidgetServiceImpl implements WidgetService {

    private final WidgetRepository widgetRepository;

    @Override
    public Widget create(Widget widget) {
        return widgetRepository.create(widget);
    }

    @Override
    public Widget update(Widget source) {
        return widgetRepository.update(source);
    }

    @Override
    public Optional<Widget> findById(Integer id) {
        return widgetRepository.findById(id);
    }

    @Override
    public void delete(Integer id) {
        widgetRepository.delete(id);
    }

    @Override
    public Page<Widget> list(Pageable pageable, SearchParams params) {
        return widgetRepository.list(pageable, params);
    }
}
