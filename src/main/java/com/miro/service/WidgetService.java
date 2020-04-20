package com.miro.service;

import com.miro.entity.Widget;
import com.miro.repository.params.SearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service to work with widgets.
 */

public interface WidgetService {

    Widget create(Widget widget);

    Widget update(Widget source);

    Optional<Widget> findById(Integer id);

    void delete(Integer id);

    Page<Widget> list(Pageable pageable, SearchParams params);

}
