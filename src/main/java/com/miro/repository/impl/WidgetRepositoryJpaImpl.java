package com.miro.repository.impl;

import com.miro.entity.Widget;
import com.miro.exception.EntityNotFoundException;
import com.miro.repository.WidgetJpaRepository;
import com.miro.repository.WidgetRepository;
import com.miro.repository.params.SearchParams;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Repository that works with sql-based data storage through Spring Data JPA repository.
 */
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "app.storage",
        havingValue = "in-memory")
public class WidgetRepositoryJpaImpl implements WidgetRepository {
    private final WidgetJpaRepository widgetJpaRepository;

    @Transactional
    @Override
    public Widget create(Widget widget) {
        handleZIndex(widget);
        return widgetJpaRepository.save(widget);
    }

    @Override
    public Optional<Widget> findById(Integer id) {
        return widgetJpaRepository.findById(id);
    }

    @Transactional
    @Override
    public Widget update(Widget source) {
        Widget target = widgetJpaRepository.findById(source.getId())
                .orElseThrow(() -> new EntityNotFoundException(source.getId(), Widget.class));

        target.setX(source.getX());
        target.setY(source.getY());
        target.setWidth(source.getWidth());
        target.setHeight(source.getHeight());

        if (!target.getZ().equals(source.getZ())) {
            handleZIndex(source);
            target.setZ(source.getZ());
        }

        return target;
    }

    @Override
    public void delete(Integer id) {
        Widget target = widgetJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, Widget.class));
        widgetJpaRepository.delete(target);
    }

    @Transactional
    @Override
    public Page<Widget> list(Pageable pageable, SearchParams params) {
        if (params != null) {
            return widgetJpaRepository.findByRectangle(
                    params.getLowerLeftX(), params.getLowerLeftY(), params.getUpperRightX(), params.getUpperRightY(),
                    pageable
            );
        }
        return widgetJpaRepository.findAll(pageable);
    }

    private void handleZIndex(Widget widget) {
        if (widget.getZ() != null) {
            if (widgetJpaRepository.existsByZ(widget.getZ())) {
                widgetJpaRepository.shiftByZIndex(widget.getZ());
            }
        } else {
            widget.setZ(widgetJpaRepository.getNextZIndex());
        }

    }
}
