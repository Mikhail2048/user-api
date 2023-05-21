package org.example.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import org.example.domain.LockEntry;
import org.example.repository.LockEntryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LockAcquiringService {

    private final LockEntryRepository lockEntryRepository;

    @Value("${healthcheck.timeout.seconds:5000}")
    private long healthCheckTimeoutSeconds;

    /**
     * @return true if successful, false otherwise
     */
    @Transactional
    public boolean isCurrentPodResponsibleForProcessing() {
        try {
            String podName = System.getenv("POD_NAME");

            List<LockEntry> all = lockEntryRepository.findAll();

            Assert.state(all.size() <= 1, "Locks table contains multiple entries");

            if (all.size() != 0) {
                LockEntry lockEntry = all.get(0);
                log.info("Fetched lock records : {}", lockEntry);
                long now = Instant.now().getEpochSecond();
                long lastTtl = lockEntry.getLastTtl().toInstant().getEpochSecond();
                if (now - lastTtl > healthCheckTimeoutSeconds) {
                    log.warn("Currently responsible pod  : '{}' for processing has timed out the healthcheck", lockEntry.getPodName());
                    lockEntryRepository.deleteById(lockEntry.getId());
                    createRecordWithPodNameAndSave(podName);
                    return true;
                } else {
                    return lockEntry.getPodName().equals(podName);
                }
            } else {
                log.warn("There is no record in DB for lock, assigning this pod as master");
                createRecordWithPodNameAndSave(podName);
                return true;
            }
        } catch (DataAccessException exception) {
            log.error("Unexpected exception occurred during processing", exception);
            return false;
        }
    }

    private void createRecordWithPodNameAndSave(String podName) {
        LockEntry newEntry = new LockEntry();
        newEntry.setAcquiredAt(OffsetDateTime.now());
        newEntry.setLastTtl(OffsetDateTime.now());
        newEntry.setPodName(podName);
        lockEntryRepository.save(newEntry);
    }
}