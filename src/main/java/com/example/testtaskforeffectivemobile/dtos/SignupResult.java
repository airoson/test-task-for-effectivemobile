package com.example.testtaskforeffectivemobile.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupResult {
    @JsonProperty("client_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long clientId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
}
