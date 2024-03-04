package com.example.testtaskforeffectivemobile.services;

import com.example.testtaskforeffectivemobile.dtos.TransferResult;
import com.example.testtaskforeffectivemobile.entities.BankAccount;
import com.example.testtaskforeffectivemobile.entities.Client;
import com.example.testtaskforeffectivemobile.exception.ServiceException;
import com.example.testtaskforeffectivemobile.repositories.BankAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class BankAccountService {
    private BankAccountRepository repository;
    private ClientService clientService;

    public BankAccountService(BankAccountRepository repository, ClientService clientService) {
        this.repository = repository;
        this.clientService = clientService;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 4, noRetryFor = {ServiceException.class})
    public TransferResult transfer(String fromLogin, String toLogin, BigDecimal transferAmount){
        if(fromLogin.equals(toLogin)) throw new ServiceException("Can't transfer money to the same account");
        if(transferAmount.compareTo(BigDecimal.ZERO) <= 0){
            throw new ServiceException("Can transfer only positive amounts");
        }
        Client fromClient = clientService.getClientByLogin(fromLogin);
        if(fromClient == null){
            throw new ServiceException("Client with login %s was not found".formatted(fromLogin));
        }
        Client toClient = clientService.getClientByLogin(toLogin);
        if(toClient == null){
            throw new ServiceException("Client with login %s was not found".formatted(toLogin));
        }
        BigDecimal currentAmountFrom = fromClient.getBankAccount().getAmount();
        BigDecimal currentAmountTo = toClient.getBankAccount().getAmount();
        if(fromClient.getBankAccount().getAmount().compareTo(transferAmount) >= 0){
            fromClient.getBankAccount().setAmount(currentAmountFrom.subtract(transferAmount));
            toClient.getBankAccount().setAmount(currentAmountTo.add(transferAmount));
        }else throw new ServiceException("Not enough money for transfer");
        return new TransferResult(fromClient.getBankAccount().getAmount(), transferAmount);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 4)
    public void increaseAllAmountsBy(BigDecimal by){
        Instant now = Instant.ofEpochSecond(System.currentTimeMillis() / 1000 - 60);
        List<BankAccount> accounts = repository.getBankAccountByLastUpdate(now);
        for(BankAccount account: accounts){
            BigDecimal newAmount = account.getAmount().multiply(by);
            if(newAmount.compareTo(account.getIncreaseLimit()) < 1){
                account.setAmount(newAmount);
                account.setLastUpdate(Instant.ofEpochSecond(System.currentTimeMillis() / 1000));
                log.info("Updated amount for bank account {} set {}", account.getId(), newAmount);
            }
        }
    }
}
