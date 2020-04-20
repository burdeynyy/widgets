package com.miro.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application properties.
 */
@ConfigurationProperties("app")
@Data
public class ApplicationProperties {
    private String storage;
    private Integer defaultRateLimit;

}
