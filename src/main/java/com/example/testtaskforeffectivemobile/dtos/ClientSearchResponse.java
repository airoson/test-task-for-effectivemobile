package com.example.testtaskforeffectivemobile.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientSearchResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ClientDescription> data;

    public ClientSearchResponse(String error) {
        this.error = error;
    }

    public ClientSearchResponse(List<ClientDescription> data) {
        this.data = data;
    }
}
