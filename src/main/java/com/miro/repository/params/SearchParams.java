package com.miro.repository.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Search widgets parameters.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchParams {
    private Integer lowerLeftX;
    private Integer lowerLeftY;
    private Integer upperRightX;
    private Integer upperRightY;
}
