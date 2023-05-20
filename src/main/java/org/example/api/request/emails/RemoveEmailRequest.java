package org.example.api.request.emails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.example.api.request.UserIdRequest;

import lombok.Data;

@Data
public class RemoveEmailRequest implements UserIdRequest {

    @NotEmpty
    @Email
    private String email;

    @NotNull
    private Long userId;
}
