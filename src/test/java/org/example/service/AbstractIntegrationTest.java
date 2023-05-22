package org.example.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
public class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.3")
      .withDatabaseName("postgres")
      .withUsername("postgres")
      .withPassword("does_not_matter_here");

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void property(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
          () -> String.format("jdbc:postgresql://localhost:%d/postgres", postgreSQLContainer.getFirstMappedPort()));
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.close();
    }
}