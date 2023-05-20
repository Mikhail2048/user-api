package org.example.api;

import org.example.api.request.phones.PhoneNumberAddRequest;
import org.example.api.request.phones.RemovePhoneNumberRequest;
import org.example.api.request.phones.UpdatePhoneNumberRequest;
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
public class PhonesApi extends AbstractApiController {

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
}