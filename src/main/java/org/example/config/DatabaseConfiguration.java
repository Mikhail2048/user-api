package org.example.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .locations("db/migrations")
                .dataSource(dataSource)
                .load();
    }
}