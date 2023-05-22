package org.example.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.Join;

import org.example.api.request.MoneyTransferRequest;
import org.example.api.request.UserSearchRequest;
import org.example.api.request.emails.EmailAddRequest;
import org.example.api.request.emails.RemoveEmailRequest;
import org.example.api.request.emails.UpdateEmailRequest;
import org.example.api.request.phones.PhoneNumberAddRequest;
import org.example.api.request.phones.RemovePhoneNumberRequest;
import org.example.api.request.phones.UpdatePhoneNumberRequest;
import org.example.domain.EmailData;
import org.example.domain.PhoneData;
import org.example.domain.User;
import org.example.exception.ClientSideException;
import org.example.exception.EmailIsAlreadyInUseException;
import org.example.exception.InsufficientFundsException;
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
    private final PhoneService phoneService;

    private final EmailService emailService;

    @Transactional
    public void updateUserInDb(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByName(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    @Transactional(readOnly = true)
    public List<User> findAllWithBalances() {
        return userRepository.findAllWithAccounts();
    }

    @Transactional(readOnly = true)
    public User selectForUpdateByIdWithBalance(Long id) {
        return userRepository.findByIdWithAccount(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public void performMoneyTransfer(MoneyTransferRequest request) {
        User from = selectForUpdateByIdWithBalance(request.getFromUserId());
        User to = selectForUpdateByIdWithBalance(request.getToUserId());
        BigDecimal afterSubtraction = from.getAccount().getBalance().subtract(request.getAmount());
        if (afterSubtraction.signum() < 0) {
            log.info("Impossible to subtract money : {} from user with id : {}. Not enough funds", request.getAmount(), request.getFromUserId());
            throw new InsufficientFundsException(request.getUserId(), request.getAmount());
        }
        from.getAccount().setBalance(afterSubtraction);
        to.getAccount().setBalance(to.getAccount().getBalance().add(request.getAmount()));
        userRepository.save(from);
        userRepository.save(to);
    }

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
        return this.userRepository.findWithPhonesById(id).orElseThrow(() -> new UserNotFoundException(id));
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
            log.warn("Unable to remove phone '{}' from user '{}' no phones would left", request.getPhoneNumber(), request.getUserId());
            throw new NoPhoneNumberLeftException(request.getUserId(), request.getPhoneNumber());
        }
        this.phoneService.removePhoneFromUser(request.getUserId(), request.getPhoneNumber());
    }

    @Transactional
    public void removeEmail(RemoveEmailRequest request) {
        User user = this.findUserWithEmails(request.getUserId());
        user.getEmails().removeIf(it -> Objects.equals(it.getEmail(), request.getEmail()));
        if (CollectionUtils.isEmpty(user.getEmails())) {
            log.warn("Unable to remove email '{}' from user '{}' no emails would left", request.getEmail(), request.getUserId());
            throw new NoEmailLeftException(request.getUserId(), request.getEmail());
        }
        this.emailService.removeEmailFromUser(request.getUserId(), request.getEmail());
    }

    @Transactional
    public void updatePhoneNumber(UpdatePhoneNumberRequest request) {

        if (request.getPhoneNumberOld().equals(request.getPhoneNumberNew())) {
            log.warn("unable to change phone number onto itself, request : {}", request);
            throw new ClientSideException("Cannot change number onto itself");
        }

        checkPhoneIsNotOccupied(request.getPhoneNumberNew());

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
            log.warn("unable to change email onto itself, request : {}", request);
            throw new ClientSideException("Cannot change number onto itself");
        }

        checkEmailIsNotOccupied(request.getEmailNew());

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
        checkPhoneIsNotOccupied(request.getPhoneNumber());
        User user = this.findUserWithPhones(request.getUserId());
        user.getPhoneNumbers().add(new PhoneData().setPhone(request.getPhoneNumber()).setUser(user));
        userRepository.save(user);
    }

    @Transactional
    public void addEmailToUser(EmailAddRequest request) {
        checkEmailIsNotOccupied(request.getEmail());
        User user = this.findUserWithEmails(request.getUserId());
        user.getEmails().add(new EmailData().setEmail(request.getEmail()).setUser(user));
        userRepository.save(user);
    }

    private void checkPhoneIsNotOccupied(String phone) {
        if (userRepository.existByPhoneNumber(phone)) {
            log.warn("Phone : {} is already occupied", phone);
            throw new PhoneNumberIsAlreadyInUseException(phone);
        }
    }

    private void checkEmailIsNotOccupied(String email) {
        if (userRepository.existByEmail(email)) {
            log.warn("Email : {} is already occupied", email);
            throw new EmailIsAlreadyInUseException(email);
        }
    }
}