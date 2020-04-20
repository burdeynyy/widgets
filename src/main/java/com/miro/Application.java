package com.miro;

import com.miro.config.properties.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Application entry point.
 */
@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
