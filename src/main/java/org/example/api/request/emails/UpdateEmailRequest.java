package org.example.api.request.emails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.example.api.request.UserIdRequest;

import lombok.Data;

@Data
public class UpdateEmailRequest implements UserIdRequest {

    @NotNull
    private Long userId;

    @NotEmpty
    @Email
    private String emailOld;

    @NotEmpty
    @Email
    private String emailNew;
}