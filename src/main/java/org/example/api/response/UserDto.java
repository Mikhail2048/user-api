package org.example.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserDto {

    private String name;

    private LocalDate dateOfBirth;

    private BigDecimal accountBalance;

    private List<String> emails;

    private List<String> phones;
}