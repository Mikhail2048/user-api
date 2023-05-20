package org.example.api;

import java.util.Objects;

import org.example.api.request.PhoneNumberAddRequest;
import org.example.api.request.RemovePhoneNumberRequest;
import org.example.api.request.UpdatePhoneNumberRequest;
import org.example.api.request.UserIdRequest;
import org.example.exception.AccessDeniedException;
import org.example.filter.SecurityContextHolder;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/phones/v1/")
@RequiredArgsConstructor
public class PhonesApi {

    private final UserService userService;

    @PutMapping("/add")
    public void addPhoneNumberToUser(@RequestBody PhoneNumberAddRequest request) {
        checkUserIdBeforeRequest(request);
        userService.addPhoneNumberToUser(request);
    }

    @DeleteMapping
    public void removePhoneNumber(@RequestBody RemovePhoneNumberRequest request) {
        checkUserIdBeforeRequest(request);
        userService.removePhoneNumber(request);
    }

    @PutMapping("/update")
    public void updatePhoneNumber(@RequestBody UpdatePhoneNumberRequest request) {
        checkUserIdBeforeRequest(request);
        userService.updatePhoneNumber(request);
    }

    private void checkUserIdBeforeRequest(UserIdRequest request) {
        Long userId = SecurityContextHolder.getUserId();
        if (!Objects.equals(request.getUserId(), userId)) {
            throw new AccessDeniedException(request.getUserId());
        }
    }
}