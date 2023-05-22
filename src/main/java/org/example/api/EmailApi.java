package org.example.api;

import org.example.api.request.emails.EmailAddRequest;
import org.example.api.request.emails.RemoveEmailRequest;
import org.example.api.request.emails.UpdateEmailRequest;
import org.example.service.UserService;
import org.springframework.validation.annotation.Validated;
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
    public void addEmailToUser(@RequestBody @Validated EmailAddRequest request) {
        checkUserIdBeforeRequest(request);
        userService.addEmailToUser(request);
    }

    @DeleteMapping
    public void removeEmail(@RequestBody @Validated RemoveEmailRequest request) {
        checkUserIdBeforeRequest(request);
        userService.removeEmail(request);
    }

    @PutMapping("/update")
    public void updateEmail(@RequestBody @Validated UpdateEmailRequest request) {
        checkUserIdBeforeRequest(request);
        userService.updateEmail(request);
    }
}