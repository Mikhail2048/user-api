package org.example.batch;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthCheckJob {

    private final JdbcTemplate template;

    @Scheduled(fixedDelayString = "${healthcheck.interval.milliseconds:1000}")
    public void healthcheckLock() {
        log.trace("Updating the TTL in lock table if current pod is in charge");
        template.execute(String.format("UPDATE balance_increasing_job_lock SET last_ttl = NOW() WHERE pod_name = '%s'", System.getenv("POD_NAME")));
    }
}