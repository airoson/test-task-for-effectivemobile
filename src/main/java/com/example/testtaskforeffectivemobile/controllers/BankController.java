package com.example.testtaskforeffectivemobile.controllers;

import com.example.testtaskforeffectivemobile.dtos.TransferRequest;
import com.example.testtaskforeffectivemobile.dtos.TransferResult;
import com.example.testtaskforeffectivemobile.services.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@Tag(name="Банковские операции", description = "Операции перевода денег между счетами")
public class BankController {
    private BankAccountService bankAccountService;

    public BankController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("/transfer")
    @Operation(summary = "Перевод денег", description = "Перевод денег со счета текущего аутентифицированного клиента на счет другого клиента")
    public ResponseEntity<TransferResult> performTransfer(@RequestBody TransferRequest transferRequest, Principal principal){
        TransferResult res = bankAccountService.transfer(principal.getName(), transferRequest.getTo(), transferRequest.getAmount());
        return ResponseEntity.ok(res);
    }
}
