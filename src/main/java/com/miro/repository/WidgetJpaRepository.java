package com.miro.repository;

import com.miro.entity.Widget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;

/**
 * JPA Repository for work with widgets.
 */
@Repository
public interface WidgetJpaRepository extends JpaRepository<Widget, Integer> {

    String GET_BY_RECTANGLE_QUERY = "SELECT * from widget " +
            "WHERE rect && ST_MakeEnvelope(:xMin, :yMin, :xMax, :yMax) " +
            "AND ST_Contains(ST_MakeEnvelope(:xMin, :yMin, :xMax, :yMax), rect)";

    String GET_BY_RECTANGLE_COUNT_QUERY = "SELECT count(*) from widget " +
            "WHERE rect && ST_MakeEnvelope(:xMin, :yMin, :xMax, :yMax) " +
            "AND ST_Contains(ST_MakeEnvelope(:xMin, :yMin, :xMax, :yMax), rect)";

    @Query("SELECT COALESCE(MAX(widget.z) + 1, 0) FROM Widget widget")
    int getNextZIndex();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT id FROM Widget")
    List<Long> selectExistingForUpdate();

    boolean existsByZ(Integer z);

    @Modifying
    @Query("update Widget u set u.z = u.z + 1 where u.z >= ?1")
    void shiftByZIndex(Integer z);

    @Query(nativeQuery = true, value = GET_BY_RECTANGLE_QUERY, countQuery = GET_BY_RECTANGLE_COUNT_QUERY)
    Page<Widget> findByRectangle(
            @Param("xMin") double xMin,
            @Param("yMin") double yMin,
            @Param("xMax") double xMax,
            @Param("yMax") double yMax,
            Pageable pageable);
}
