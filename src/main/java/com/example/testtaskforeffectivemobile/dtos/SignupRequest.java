package com.example.testtaskforeffectivemobile.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    private String login;
    private String password;
    private String email;
    private String phone;
    @JsonProperty("start_amount")
    private BigDecimal startAmount;
    private String name;
    private String lastname;
    @JsonProperty("middle_name")
    private String middleName;
    @JsonProperty("birth_date")
    private String birthDate;
}
