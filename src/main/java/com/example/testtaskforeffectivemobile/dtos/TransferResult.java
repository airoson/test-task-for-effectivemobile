package com.example.testtaskforeffectivemobile.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferResult {
    @JsonProperty("new_amount")
    private BigDecimal newAmount;
    private BigDecimal transferred;
}
