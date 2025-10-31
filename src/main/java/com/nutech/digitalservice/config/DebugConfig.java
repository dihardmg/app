package com.nutech.digitalservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "app.debug.mode", havingValue = "true")
public class DebugConfig {

    @Bean
    public DataSource debugDataSource() {
        // Create an in-memory H2 database for debugging purposes
        // This allows the application to start and show environment variables
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("debugdb")
                .build();
    }
}