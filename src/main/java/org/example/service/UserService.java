package org.example.service;

import java.util.Objects;

import org.example.api.request.EmailAddRequest;
import org.example.api.request.PhoneNumberAddRequest;
import org.example.api.request.RemoveEmailRequest;
import org.example.api.request.RemovePhoneNumberRequest;
import org.example.api.request.UpdateEmailRequest;
import org.example.api.request.UpdatePhoneNumberRequest;
import org.example.domain.EmailData;
import org.example.domain.PhoneData;
import org.example.domain.User;
import org.example.exception.ClientSideException;
import org.example.exception.EmailIsAlreadyInUseException;
import org.example.exception.NoEmailLeftException;
import org.example.exception.NoPhoneNumberLeftException;
import org.example.exception.PhoneNumberIsAlreadyInUseException;
import org.example.exception.UserNotFoundException;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @NonNull
    public User findUserById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    @NonNull
    public User findUserWithPhones(Long id) {
        return this.userRepository.findByIdWithPhones(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    @NonNull
    public User findUserWithEmails(Long id) {
        return this.userRepository.findByIdWithEmails(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public void removePhoneNumber(RemovePhoneNumberRequest request) {
        User user = this.findUserWithPhones(request.getUserId());
        user.getPhoneNumbers().removeIf(it -> Objects.equals(it.getPhone(), request.getPhoneNumber()));
        if (CollectionUtils.isEmpty(user.getPhoneNumbers())) {
            throw new NoPhoneNumberLeftException(request.getUserId(), request.getPhoneNumber());
        }
        this.userRepository.save(user);
    }

    @Transactional
    public void removeEmail(RemoveEmailRequest request) {
        User user = this.findUserWithEmails(request.getUserId());
        user.getEmails().removeIf(it -> Objects.equals(it.getEmail(), request.getEmail()));
        if (CollectionUtils.isEmpty(user.getEmails())) {
            throw new NoEmailLeftException(request.getUserId(), request.getEmail());
        }
        this.userRepository.save(user);
    }

    @Transactional
    public void updatePhoneNumber(UpdatePhoneNumberRequest request) {

        if (request.getPhoneNumberOld().equals(request.getPhoneNumberNew())) {
            throw new ClientSideException("Cannot change number onto itself");
        }

        if (this.userRepository.existByPhoneNumber(request.getPhoneNumberNew())) {
            throw new PhoneNumberIsAlreadyInUseException(request.getPhoneNumberNew());
        }

        User user = this.findUserWithPhones(request.getUserId());
        user.getPhoneNumbers().forEach(phoneData -> {
            if (Objects.equals(phoneData.getPhone(), request.getPhoneNumberOld())) {
                phoneData.setPhone(request.getPhoneNumberNew());
            }
        });
        this.userRepository.save(user);
    }

    @Transactional
    public void updateEmail(UpdateEmailRequest request) {

        if (request.getEmailOld().equals(request.getEmailNew())) {
            throw new ClientSideException("Cannot change number onto itself");
        }

        if (this.userRepository.existByEmail(request.getEmailNew())) {
            throw new EmailIsAlreadyInUseException(request.getEmailNew());
        }

        User user = this.findUserWithEmails(request.getUserId());
        user.getEmails().forEach(emailData -> {
            if (Objects.equals(emailData.getEmail(), request.getEmailOld())) {
                emailData.setEmail(request.getEmailNew());
            }
        });
        this.userRepository.save(user);
    }

    @Transactional
    public void addPhoneNumberToUser(PhoneNumberAddRequest request) {
        if (userRepository.existByPhoneNumber(request.getPhoneNumber())) {
            throw new PhoneNumberIsAlreadyInUseException(request.getPhoneNumber());
        }
        User user = this.findUserWithPhones(request.getUserId());
        user.getPhoneNumbers().add(new PhoneData().setPhone(request.getPhoneNumber()).setUser(user));
        userRepository.save(user);
    }

    @Transactional
    public void addEmailToUser(EmailAddRequest request) {
        if (userRepository.existByEmail(request.getEmail())) {
            throw new EmailIsAlreadyInUseException(request.getEmail());
        }
        User user = this.findUserWithEmails(request.getUserId());
        user.getEmails().add(new EmailData().setEmail(request.getEmail()).setUser(user));
        userRepository.save(user);
    }
}