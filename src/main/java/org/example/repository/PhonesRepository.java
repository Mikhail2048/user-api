package org.example.repository;

import org.example.domain.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhonesRepository extends JpaRepository<PhoneData, Long> {

    void deleteByUserIdAndPhone(Long userId, String phone);
}