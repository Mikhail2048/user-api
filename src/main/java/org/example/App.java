package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class App {
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }
}

@ConditionalOnProperty(value = "scheduling.enabled", matchIfMissing = true)
@Configuration
@EnableScheduling
class SchedulingConfiguration { }