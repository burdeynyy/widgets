package com.miro.repository;

import com.miro.entity.Widget;
import com.miro.repository.params.SearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Repository to work with widgets data.
 */
public interface WidgetRepository {

    Widget create(Widget widget);

    Optional<Widget> findById(Integer id);

    Widget update(Widget source);

    void delete(Integer id);

    Page<Widget> list(Pageable pageable, SearchParams params);

}
