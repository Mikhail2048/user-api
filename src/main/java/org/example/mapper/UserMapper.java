package org.example.mapper;

import java.util.Optional;
import java.util.stream.Collectors;

import org.example.api.response.UserDto;
import org.example.domain.Account;
import org.example.domain.EmailData;
import org.example.domain.PhoneData;
import org.example.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto()
                      .setName(user.getName())
                      .setPhones(
                        Optional.ofNullable(user.getPhoneNumbers())
                                .map(it -> it.stream().map(PhoneData::getPhone).collect(Collectors.toList()))
                                .orElse(null)
                      )
                      .setDateOfBirth(user.getDateOfBirth())
                      .setAccountBalance(Optional.ofNullable(user.getAccount()).map(Account::getBalance).orElse(null))
                      .setEmails(
                        Optional.ofNullable(user.getEmails())
                          .map(it -> it.stream().map(EmailData::getEmail).collect(Collectors.toList()))
                          .orElse(null)
                      );
    }
}