package org.example.service;

import org.example.repository.PhonesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhoneService {

    private final PhonesRepository phonesRepository;

    @Transactional
    public void removePhoneFromUser(Long userId, String phone) {
        phonesRepository.deleteByUserIdAndPhone(userId, phone);
    }
}