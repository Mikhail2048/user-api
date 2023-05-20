package org.example.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserSearchRequest {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String dateOfBirth;

    private String phoneNumber;

    private String name;

    private String email;
}