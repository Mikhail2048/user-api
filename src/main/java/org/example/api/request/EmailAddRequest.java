package org.example.api.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class EmailAddRequest implements UserIdRequest {

    @NotNull
    private Long userId;

    @NotEmpty
    @Email
    private String email;
}