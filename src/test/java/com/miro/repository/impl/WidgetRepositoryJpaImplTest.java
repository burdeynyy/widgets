package com.miro.repository.impl;

import com.miro.entity.Widget;
import com.miro.repository.WidgetJpaRepository;
import com.miro.repository.params.SearchParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Optional;

import static com.miro.TestConstants.Z_INDEX_CHANGED;
import static com.miro.TestEntityFactory.createChangedDefaultWidget;
import static com.miro.TestEntityFactory.createDefaultWidget;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for @{@link WidgetRepositoryJpaImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class WidgetRepositoryJpaImplTest {

    @InjectMocks
    private WidgetRepositoryJpaImpl widgetRepository;

    @Mock
    private WidgetJpaRepository jpaRepository;

    @Test
    public void testCreateWidgetWithZNonExistingIndex() {
        Integer newId = 1;
        Widget widget = createDefaultWidget();
        when(jpaRepository.save(widget))
                .thenAnswer(invocationOnMock -> {
                    final Widget argument = invocationOnMock.getArgument(0);
                    argument.setId(newId);
                    return argument;
                });


        Widget created = widgetRepository.create(widget);


        verify(jpaRepository).save(widget);
        verify(jpaRepository).existsByZ(widget.getZ());
        verify(jpaRepository, never()).shiftByZIndex(widget.getZ());

        assertSame(widget, created);
        assertNotNull(created.getId());
    }

    @Test
    public void testCreateWidgetWithExistingIndex() {
        Integer newId = 1;
        Widget widget = createDefaultWidget();
        when(jpaRepository.existsByZ(widget.getZ())).thenReturn(Boolean.TRUE);
        when(jpaRepository.save(widget))
                .thenAnswer(invocationOnMock -> {
                    final Widget argument = invocationOnMock.getArgument(0);
                    argument.setId(newId);
                    return argument;
                });


        Widget created = widgetRepository.create(widget);


        verify(jpaRepository).save(widget);
        verify(jpaRepository).existsByZ(widget.getZ());
        verify(jpaRepository).shiftByZIndex(widget.getZ());

        assertSame(widget, created);
        assertNotNull(created.getId());
    }

    @Test
    public void testCreateWidgetWithNullIndex() {
        Integer newId = 1;
        Widget widget = createDefaultWidget();
        widget.setZ(null);
        when(jpaRepository.save(widget))
                .thenAnswer(invocationOnMock -> {
                    final Widget argument = invocationOnMock.getArgument(0);
                    argument.setId(newId);
                    return argument;
                });


        Widget created = widgetRepository.create(widget);


        verify(jpaRepository).save(widget);
        verify(jpaRepository, never()).existsByZ(widget.getZ());
        verify(jpaRepository, never()).shiftByZIndex(widget.getZ());

        assertSame(widget, created);
        assertNotNull(created.getId());
    }

    @Test
    public void testDeleteWidget() {
        Widget widget = createDefaultWidget();
        when(jpaRepository.findById(widget.getId())).thenReturn(Optional.of(widget));


        widgetRepository.delete(widget.getId());


        verify(jpaRepository).delete(widget);
    }

    @Test
    public void testUpdateWidgetWithTheSameZIndex() {
        Widget widget = createDefaultWidget();
        Widget changed = createChangedDefaultWidget(widget.getId());

        when(jpaRepository.findById(widget.getId())).thenReturn(Optional.of(widget));


        Widget updated = widgetRepository.update(changed);


        assertSame(widget, updated);
        assertEquals(changed.getZ(), updated.getZ());
        assertEquals(changed.getX(), updated.getX());
        assertEquals(changed.getY(), updated.getY());
        assertEquals(changed.getWidth(), updated.getWidth());
        assertEquals(changed.getHeight(), updated.getHeight());

    }

    @Test
    public void testUpdateWidgetWithDifferentExistingZIndex() {
        Widget widget = createDefaultWidget();
        widget.setId(1);
        Widget changed = createChangedDefaultWidget(widget.getId());

        when(jpaRepository.existsByZ(changed.getZ())).thenReturn(Boolean.TRUE);
        when(jpaRepository.findById(changed.getId())).thenReturn(Optional.of(widget));


        Widget updated = widgetRepository.update(changed);


        verify(jpaRepository).existsByZ(Z_INDEX_CHANGED);
        verify(jpaRepository).shiftByZIndex(Z_INDEX_CHANGED);

        assertEquals(changed.getZ(), updated.getZ());
        assertEquals(changed.getX(), updated.getX());
        assertEquals(changed.getY(), updated.getY());
        assertEquals(changed.getWidth(), updated.getWidth());
        assertEquals(changed.getHeight(), updated.getHeight());

    }


    @Test
    public void testFindById() {
        Widget widget = createDefaultWidget();
        widget.setId(123);

        when(jpaRepository.findById(widget.getId())).thenReturn(Optional.of(widget));


        Optional<Widget> optional = widgetRepository.findById(widget.getId());


        verify(jpaRepository).findById(widget.getId());

        assertTrue(optional.isPresent());
        assertSame(widget, optional.get());
    }



    @Test
    public void testPagedListOnly() {
        Pageable pageable = PageRequest.of(2, 2);

        ArrayList<Widget> result = new ArrayList<>();
        PageImpl<Widget> pagedResult = new PageImpl<>(result);

        when(jpaRepository.findAll(pageable)).thenReturn(pagedResult);


        Page<Widget> list = widgetRepository.list(pageable, null);


        verify(jpaRepository).findAll(pageable);
        assertSame(list, pagedResult);

    }

    @Test
    public void testWithSearchesAndPagination() {
        Pageable pageable = PageRequest.of(2, 2);

        ArrayList<Widget> result = new ArrayList<>();
        PageImpl<Widget> pagedResult = new PageImpl<>(result);

        SearchParams searchParams = new SearchParams(-1, -5, 5, 5);

        when(jpaRepository.findByRectangle(searchParams.getLowerLeftX(), searchParams.getLowerLeftY(), searchParams.getUpperRightX(), searchParams.getUpperRightY(), pageable))
                .thenReturn(pagedResult);


        Page<Widget> list = widgetRepository.list(pageable,  searchParams);


        verify(jpaRepository).findByRectangle(searchParams.getLowerLeftX(), searchParams.getLowerLeftY(), searchParams.getUpperRightX(), searchParams.getUpperRightY(), pageable);
        assertSame(list, pagedResult);

    }



}
