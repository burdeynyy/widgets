package com.miro.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Widget search parameters dto.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchParamsDto {

    private Integer lowerLeftX;
    private Integer lowerLeftY;
    private Integer upperRightX;
    private Integer upperRightY;
}
