package com.miro.web.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Error message dto.
 */
@Getter
@Setter
public class ErrorMessage {

    private String description;

    public ErrorMessage(final String description) {
        this.description = description;
    }

}
