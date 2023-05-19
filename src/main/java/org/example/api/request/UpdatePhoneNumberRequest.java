package org.example.api.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdatePhoneNumberRequest {

    @NotNull
    private Long userId;

    @NotEmpty
    @Size(max = 13)
    private String phoneNumberOld;

    @NotEmpty
    @Size(max = 13)
    private String phoneNumberNew;
}