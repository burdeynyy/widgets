package com.miro.web.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Widget dto.
 */
@Getter
@Setter
public class WidgetDto {
    private Long id;

    @NotNull
    private Integer x;
    @NotNull
    private Integer y;
    private Integer z;
    @NotNull
    @Min(1)
    private int width;
    @Min(1)
    @NotNull
    private int height;

    private LocalDateTime modificationDate;
}
