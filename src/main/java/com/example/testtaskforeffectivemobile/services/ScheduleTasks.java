package com.example.testtaskforeffectivemobile.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ScheduleTasks {
    private BankAccountService bankAccountService;
    private BigDecimal by;

    public ScheduleTasks(@Value("${bank.increase-by}")String increaseByPercent,
                         BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
        by = new BigDecimal(increaseByPercent);
    }

    @Scheduled(fixedRate = 1000L, initialDelay = 1000L)
    public void updateBankAccounts(){
        bankAccountService.increaseAllAmountsBy(by);
    }
}
