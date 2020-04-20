package com.miro.repository.impl;

import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;
import com.miro.entity.Widget;
import com.miro.exception.EntityNotFoundException;
import com.miro.repository.WidgetRepository;
import com.miro.repository.params.SearchParams;
import gnu.trove.TIntProcedure;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.miro.util.RectangleUtils.getRectangle;

/**
 * Native(except complication №2 part) java implementation of data storage for widgets .
 *
 * Note: For organizing spatial index - external R-tree is used {@link SpatialIndex}.
 */
@Repository
@ConditionalOnProperty(
        value = "app.storage",
        havingValue = "native",
        matchIfMissing = true)
public class WidgetRepositoryNativeImpl implements WidgetRepository {

    private final TreeMap<Integer, Widget> uniqueZIndex = new TreeMap<>();
    private final SpatialIndex spatialIndex = new RTree();
    private final Map<Integer, Widget> storage = new HashMap<>();
    private final AtomicInteger sequence = new AtomicInteger();
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public WidgetRepositoryNativeImpl() {
        spatialIndex.init(null);
    }

    @Override
    public Widget create(Widget widget) {
        if (widget == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        try {
            lock.writeLock().lock();

            updateUniqueIndex(widget);

            widget.setId(sequence.incrementAndGet());
            widget.setModificationDate(LocalDateTime.now());

            updateSpatialIndex(widget);

            storage.put(widget.getId(), widget);
            return widget;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void updateSpatialIndex(Widget widget) {
        Rectangle rectangle = getRectangle(widget);
        spatialIndex.add(rectangle, widget.getId());
    }


    private void updateUniqueIndex(Widget widget) {
        Integer z = widget.getZ();
        if (z != null) {
            Widget widgetWIthTheSameZIndex = uniqueZIndex.get(z);
            if (widgetWIthTheSameZIndex != null) {
                rebuildIndex(widget);
            }
        } else {
            Integer maxZ = uniqueZIndex.isEmpty() ? -1 : uniqueZIndex.lastKey();
            widget.setZ(maxZ + 1);
        }
        uniqueZIndex.put(widget.getZ(), widget);
    }

    private void rebuildIndex(Widget widgetWithTargetZ) {
        SortedMap<Integer, Widget> moreThanPassedZ = uniqueZIndex.tailMap(widgetWithTargetZ.getZ(), true);
        List<Widget> values = new ArrayList<>(moreThanPassedZ.values());
        for (int i = values.size() - 1; i >= 0; i--) {
            Widget removed = uniqueZIndex.remove(values.get(i).getZ());
            if (removed != null) {
                Integer currentZ = removed.getZ();
                removed.setZ(currentZ + 1);
                uniqueZIndex.put(removed.getZ(), removed);
            }
        }
    }

    @Override
    public Optional<Widget> findById(Integer id) {
        try {
            lock.readLock().lock();
            return Optional.ofNullable(storage.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Widget update(Widget source) {
        try {
            lock.writeLock().lock();
            Widget target = storage.get(source.getId());
            if (target == null) {
                throw new EntityNotFoundException(source.getId(), Widget.class);
            }
            if (coordsHasChanged(target, source)) {
                spatialIndex.delete(getRectangle(target), target.getId());

                target.setX(source.getX());
                target.setY(source.getY());
                target.setWidth(source.getWidth());
                target.setHeight(source.getHeight());

                spatialIndex.add(getRectangle(target), target.getId());
            }

            //if z has changed - update unique index
            if (!target.getZ().equals(source.getZ())) {
                uniqueZIndex.remove(target.getZ());

                target.setZ(source.getZ());

                updateUniqueIndex(target);
            }
            target.setModificationDate(LocalDateTime.now());
            return target;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean coordsHasChanged(Widget target, Widget source) {
        return target.getX() != source.getX()
                || target.getY() != source.getY()
                || target.getWidth() != source.getWidth()
                || target.getHeight() != source.getHeight();
    }


    @Override
    public void delete(Integer id) {
        try {
            lock.writeLock().lock();
            Widget removed = storage.remove(id);
            if (removed == null) {
                throw new EntityNotFoundException(id, Widget.class);
            }
            uniqueZIndex.remove(removed.getZ());
            spatialIndex.delete(getRectangle(removed), removed.getId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Page<Widget> list(Pageable pageable, SearchParams params) {
        try {
            lock.readLock().lock();
            Iterator<Widget> iterator;
            int size;
            if (params == null) {
                iterator = uniqueZIndex.values().iterator();
                size = storage.size();
            } else {
                Rectangle rectangle = new Rectangle(
                        params.getLowerLeftX(),
                        params.getLowerLeftY(),
                        params.getUpperRightX(),
                        params.getUpperRightY()
                );
                SaveToSortedSetProcedure proc = new SaveToSortedSetProcedure(storage);
                // search complexity is on average ~ log(n) but z-index ordering makes the whole method work slower
                spatialIndex.contains(rectangle, proc);
                final Set<Widget> foundWidgets = proc.getWidgets();
                size = foundWidgets.size();
                iterator = foundWidgets.iterator();
            }

            skip(iterator, pageable.getOffset());
            return new PageImpl<>(getPortion(iterator, pageable.getPageSize()), pageable, size);

        } finally {
            lock.readLock().unlock();
        }
    }

    private <T> List<T> getPortion(Iterator<T> iterator, int pageSize) {
        List<T> portion = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            if (iterator.hasNext()) {
                portion.add(iterator.next());
            } else {
                return portion;
            }
        }

        return portion;
    }

    private <T> void skip(Iterator<T> iterator, long offset) {
        for (long i = 0; i < offset; i++) {
            if (iterator.hasNext()) {
                iterator.next();
            } else {
                return;
            }
        }
    }

    List<Widget> findAll() {
        try {
            lock.readLock().lock();
            return new ArrayList<>(uniqueZIndex.values());
        } finally {
            lock.readLock().unlock();
        }

    }

    private static class SaveToSortedSetProcedure implements TIntProcedure {
        private final Set<Widget> widgets = new TreeSet<>(Comparator.comparingInt(Widget::getZ));
        private final Map<Integer, Widget> storage;

        SaveToSortedSetProcedure(Map<Integer, Widget> storage) {
            this.storage = storage;
        }

        public boolean execute(int id) {
            widgets.add(storage.get(id));
            return true;
        }

        private Set<Widget> getWidgets() {
            return widgets;
        }
    }
}

