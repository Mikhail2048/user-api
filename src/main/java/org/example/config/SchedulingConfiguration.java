package org.example.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConditionalOnProperty(value = "scheduling.enabled", matchIfMissing = true)
@Configuration
@EnableScheduling
class SchedulingConfiguration {

}