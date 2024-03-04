package com.example.testtaskforeffectivemobile.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientDescription {
    private String login;
    private String name;
    private String lastname;
    @JsonProperty("middle_name")
    private String middleName;
    private BigDecimal amount;
    private List<String> phones;
    private List<String> emails;
    private String birthDate;
}
