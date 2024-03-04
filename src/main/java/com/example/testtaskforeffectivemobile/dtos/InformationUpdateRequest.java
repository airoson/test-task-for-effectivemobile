package com.example.testtaskforeffectivemobile.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformationUpdateRequest {
    private List<String> phones;
    private List<String> emails;
}
