package org.example.api.request.phones;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.example.api.request.UserIdRequest;

import lombok.Data;

@Data
public class RemovePhoneNumberRequest implements UserIdRequest {

    @NotEmpty
    @Size(max = 13)
    private String phoneNumber;

    @NotNull
    private Long userId;
}