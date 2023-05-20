package org.example.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.persistence.criteria.Join;

import org.example.api.request.UserSearchRequest;
import org.example.api.request.emails.EmailAddRequest;
import org.example.api.request.phones.PhoneNumberAddRequest;
import org.example.api.request.emails.RemoveEmailRequest;
import org.example.api.request.phones.RemovePhoneNumberRequest;
import org.example.api.request.emails.UpdateEmailRequest;
import org.example.api.request.phones.UpdatePhoneNumberRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<User> findUsersByRequest(UserSearchRequest request, PageRequest pageRequest) {
        Specification<User> specification = null;

        if (StringUtils.hasText(request.getEmail())) {
            specification = getUserEmailSpecification(request);
        }

        if (StringUtils.hasText(request.getPhoneNumber())) {
            if (specification == null) {
                specification = getUserPhoneSpecification(request);
            } else {
                specification.and(getUserPhoneSpecification(request));
            }
        }

        if (StringUtils.hasText(request.getName())) {
            if (specification == null) {
                specification = getUserNameSpecification(request);
            } else {
                specification.and(getUserNameSpecification(request));
            }
        }

        if (StringUtils.hasText(request.getDateOfBirth())) {
            if (specification == null) {
                specification = getDateOfBirthSpecification(request);
            } else {
                specification.and(getDateOfBirthSpecification(request));
            }
        }

        Page<User> users = userRepository.findAll(specification, pageRequest);
        log.info("Fetched users : {} by request : {} and pageable : {}", users.getContent(), request, pageRequest);
        return users;
    }

    private static Specification<User> getDateOfBirthSpecification(UserSearchRequest request) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("dateOfBirth"), LocalDate.parse(request.getDateOfBirth(), DateTimeFormatter.ISO_DATE));
    }

    private static Specification<User> getUserNameSpecification(UserSearchRequest request) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), request.getName() + "%");
    }

    private static Specification<User> getUserPhoneSpecification(UserSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            Join<PhoneData, User> phoneNumbers = root.join("phoneNumbers");
            return criteriaBuilder.equal(phoneNumbers.get("phone"), request.getPhoneNumber());
        };
    }

    private static Specification<User> getUserEmailSpecification(UserSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            Join<EmailData, User> emailsTable = root.join("emails");
            return criteriaBuilder.equal(emailsTable.get("email"), request.getEmail());
        };
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