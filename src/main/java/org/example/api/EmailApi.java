package org.example.api;

import org.example.api.request.EmailAddRequest;
import org.example.api.request.PhoneNumberAddRequest;
import org.example.api.request.RemoveEmailRequest;
import org.example.api.request.RemovePhoneNumberRequest;
import org.example.api.request.UpdateEmailRequest;
import org.example.api.request.UpdatePhoneNumberRequest;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/emails/v1")
@RequiredArgsConstructor
public class EmailApi extends AbstractApiController {

    private final UserService userService;

    @PutMapping("/add")
    public void addEmailToUser(@RequestBody EmailAddRequest request) {
        checkUserIdBeforeRequest(request);
        userService.addEmailToUser(request);
    }

    @DeleteMapping
    public void removeEmail(@RequestBody RemoveEmailRequest request) {
        checkUserIdBeforeRequest(request);
        userService.removeEmail(request);
    }

    @PutMapping("/update")
    public void updateEmail(@RequestBody UpdateEmailRequest request) {
        checkUserIdBeforeRequest(request);
        userService.updateEmail(request);
    }
}