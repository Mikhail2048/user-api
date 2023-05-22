package org.example.service;

import org.example.repository.EmailRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;

    @Transactional
    public void removeEmailFromUser(Long userId, String phone) {
        emailRepository.deleteByUserIdAndEmail(userId, phone);
    }
}