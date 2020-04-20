package com.miro.repository.impl;

import com.miro.TestConstants;
import com.miro.entity.Widget;
import com.miro.repository.params.SearchParams;
import com.miro.util.TestData;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.miro.TestEntityFactory.*;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link WidgetRepositoryNativeImpl}.
 */
public class WidgetRepositoryNativeImplTest {

    private WidgetRepositoryNativeImpl repository = new WidgetRepositoryNativeImpl();

    @Test
    public void testCreateWidgetWithZNonExistingIndex() {
        Widget widget = createDefaultWidget();


        Widget created = repository.create(widget);


        assertSame(widget, created);
        assertNotNull(created.getId());
        assertNotNull(created.getModificationDate());
        assertTrue(repository.findById(created.getId()).isPresent());
        Assert.assertEquals(TestConstants.Z_INDEX, created.getZ());
    }


    @Test
    public void testCreateWidgetWithExistingIndex() {

        int testZInd = 10;
        Widget first = repository.create(createDefaultWidget());
        Widget second = repository.create(createWidgetWithZSpecified(testZInd));


        Widget createdWidgetWithTheSameZIndex = repository.create(createDefaultWidget());


        List<Widget> widgets = repository.findAll();
        assertEquals(createdWidgetWithTheSameZIndex, widgets.get(0));
        assertEquals(TestConstants.Z_INDEX, widgets.get(0).getZ());
        assertEquals(first, widgets.get(1));
        assertEquals(TestConstants.Z_INDEX + 1, widgets.get(1).getZ().intValue());
        assertEquals(second, widgets.get(2));
        assertEquals(testZInd + 1, widgets.get(2).getZ().intValue());

    }


    @Test
    public void testCreateWidgetWithNullIndex() {

        Integer testZInd = 10;
        Widget first = repository.create(createDefaultWidget());
        Widget second = repository.create(createWidgetWithZSpecified(testZInd));

        Widget widgetWithNullIndex = createDefaultWidget();
        widgetWithNullIndex.setZ(null);


        Widget createdWidgetWithNullIndex = repository.create(widgetWithNullIndex);


        List<Widget> widgets = repository.findAll();
        assertEquals(first, widgets.get(0));
        assertEquals(TestConstants.Z_INDEX, widgets.get(0).getZ());

        assertEquals(second, widgets.get(1));
        assertEquals(testZInd, widgets.get(1).getZ());

        assertEquals(createdWidgetWithNullIndex, widgets.get(2));
        assertEquals(testZInd + 1, widgets.get(2).getZ().intValue());

    }

    @Test
    public void testDeleteWidget() {
        Widget widget = repository.create(createDefaultWidget());
        assertEquals(1, repository.findAll().size());


        repository.delete(widget.getId());


        assertEquals(0, repository.findAll().size());
        assertFalse(repository.findById(widget.getId()).isPresent());
    }

    @Test
    public void testUpdateWidgetWithTheSameZIndex() {
        Widget widget = repository.create(createDefaultWidget());
        Widget changed = createChangedDefaultWidget(widget.getId());


        Widget updated = repository.update(changed);


        assertSame(widget, updated);
        assertEquals(changed.getZ(), updated.getZ());
        assertEquals(changed.getX(), updated.getX());
        assertEquals(changed.getY(), updated.getY());
        assertEquals(changed.getWidth(), updated.getWidth());
        assertEquals(changed.getHeight(), updated.getHeight());

    }

    /**
     * Checks that updated widgets gets desired z-index and widgets with the same and greater zIndex only are shifted.
     */
    @Test
    public void testUpdateWidgetWithDifferentExistingZIndex() {

        Integer z1 = 1;
        Integer z2 = 2;
        Integer z3 = 3;
        Integer z4 = 4;
        Integer z5 = 5;
        Integer z6 = 6;
        Integer z7 = 7;

        Widget widget = repository.create(createWidgetWithZSpecified(z1));
        Widget widget2 = repository.create(createWidgetWithZSpecified(z2));
        Widget widget3 = repository.create(createWidgetWithZSpecified(z3));
        Widget widget4 = repository.create(createWidgetWithZSpecified(z4));
        Widget widget5 = repository.create(createWidgetWithZSpecified(z5));
        Widget widget6 = repository.create(createWidgetWithZSpecified(z6));

        Widget changed = createDefaultWidgetWithIdAndZSpecified(widget2.getId(), z4);


        repository.update(changed);


        List<Widget> widgets = repository.findAll();
        assertEquals(widget, widgets.get(0));
        assertEquals(z1, widgets.get(0).getZ());

        assertEquals(widget3, widgets.get(1));
        assertEquals(z3, widgets.get(1).getZ());

        assertEquals(widget2, widgets.get(2));
        assertEquals(z4, widgets.get(2).getZ());

        assertEquals(widget4, widgets.get(3));
        assertEquals(z5, widgets.get(3).getZ());

        assertEquals(widget5, widgets.get(4));
        assertEquals(z6, widgets.get(4).getZ());

        assertEquals(widget6, widgets.get(5));
        assertEquals(z7, widgets.get(5).getZ());
    }


    @Test
    public void testFindById() {
        Widget widget = repository.create(createDefaultWidget());


        Optional<Widget> optional = repository.findById(widget.getId());


        assertTrue(optional.isPresent());
        assertEquals(widget.getId(), optional.get().getId());
    }

    @Test
    public void testPagedListOnly() {

        long total = 10;
        int pageSize = 2;
        for (int i = 0; i < total; i++) {
            repository.create(createDefaultWidget());
        }


        PageRequest pageRequest = PageRequest.of(2, pageSize);
        Page<Widget> list = repository.list(pageRequest, null);


        assertEquals(list.getTotalElements(), total);
        assertEquals(list.getTotalPages(), total / pageSize);
        assertEquals(list.getNumberOfElements(), pageSize);
    }

    @Test
    public void testSearchesOnly() {

        List<Widget> widgets = TestData.getTestData().stream().map(repository::create).collect(toList());
        PageRequest pageRequest = PageRequest.of(0, 9999);


        //search area on the picture - 7, 8, 11
        List<Widget> results = repository.list(pageRequest, new SearchParams(-1, -5, 5, 5))
                .getContent();
        assertEquals(3, results.size());
        assertTrue(results.containsAll(Arrays.asList(widgets.get(6), widgets.get(7), widgets.get(10))));


        //"bounding box" for all widgets on the picture
        results = repository.list(pageRequest, new SearchParams(-11, -11, 11, 11)).getContent();
        assertEquals(14, results.size());


        //widgets that are above x-axis on the picture - 1, 2, 3, 4, 5, 7
        results = repository.list(pageRequest, new SearchParams(-11, 0, 11, 11)).getContent();
        assertEquals(6, results.size());
        assertTrue(results.containsAll(Arrays.asList(
                widgets.get(0),
                widgets.get(1),
                widgets.get(2),
                widgets.get(3),
                widgets.get(4),
                widgets.get(6))
        ));

    }

    @Test
    public void testWithSearchesAndPagination() {

        List<Widget> widgets = TestData.getTestData().stream().map(repository::create).collect(toList());
        PageRequest pageRequest = PageRequest.of(1, 2);


        List<Widget> results = repository.list(pageRequest, new SearchParams(-1, -5, 5, 5))
                .getContent();
        assertEquals(1, results.size());
        assertTrue(results.contains(widgets.get(10)));


        results = repository.list(pageRequest, new SearchParams(-11, -11, 11, 11)).getContent();
        assertEquals(2, results.size());
        assertTrue(results.containsAll(Arrays.asList(widgets.get(2), (widgets.get(3)))));


        results = repository.list(pageRequest, new SearchParams(-11, 0, 11, 11)).getContent();
        assertEquals(2, results.size());
        assertTrue(results.containsAll(Arrays.asList(
                widgets.get(2),
                widgets.get(3))
        ));

    }

}
