package org.example.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.example.domain.LockEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface LockEntryRepository extends JpaRepository<LockEntry, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<LockEntry> findAll();
}