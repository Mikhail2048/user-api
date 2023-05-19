package org.example.api;

import org.example.api.request.PhoneNumberAddRequest;
import org.example.api.request.RemovePhoneNumberRequest;
import org.example.api.request.UpdatePhoneNumberRequest;
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
        userService.addPhoneNumberToUser(request);
    }

    @DeleteMapping
    public void removePhoneNumber(@RequestBody RemovePhoneNumberRequest request) {
        userService.removePhoneNumber(request);
    }

    @PutMapping("/update")
    public void updatePhoneNumber(@RequestBody UpdatePhoneNumberRequest request) {
        userService.updatePhoneNumber(request);
    }
}