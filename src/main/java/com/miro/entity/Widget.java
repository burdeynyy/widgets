package com.miro.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Widget entity.
 */
@Getter
@Setter
@Entity
@Table(name = "widget")
@ToString
public class Widget {
    @Id
    @SequenceGenerator(name = "widget_seq_gen", sequenceName = "widget_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "widget_seq_gen")
    @Column(name = "id")
    private Integer id;

    @Column(name = "x")
    private int x;

    @Column(name = "y")
    private int y;

    @Column(name = "z")
    private Integer z;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    @LastModifiedDate
    @CreatedDate
    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

}
