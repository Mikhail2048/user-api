package org.example.repository;

import org.example.domain.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailData, Long> {

    void deleteByUserIdAndEmail(Long userId, String email);
}