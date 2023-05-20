package org.example.api.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class PhoneNumberAddRequest implements UserIdRequest {

    @NotNull
    private Long userId;

    @NotEmpty
    @Size(max = 13)
    private String phoneNumber;
}