package com.example.testtaskforeffectivemobile.services;

import com.example.testtaskforeffectivemobile.dtos.ClientDescription;
import com.example.testtaskforeffectivemobile.dtos.InformationUpdateRequest;
import com.example.testtaskforeffectivemobile.dtos.SignupRequest;
import com.example.testtaskforeffectivemobile.entities.BankAccount;
import com.example.testtaskforeffectivemobile.entities.Client;
import com.example.testtaskforeffectivemobile.exception.ServiceException;
import com.example.testtaskforeffectivemobile.repositories.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class ClientService {
    private ClientRepository clientRepository;
    private PasswordEncoder encoder;
    private PlatformTransactionManager transactionManager;

    @Value("${bank.increase-max}")
    private String increaseMaxPercent;

    public ClientService(ClientRepository clientRepository, PasswordEncoder encoder, PlatformTransactionManager transactionManager) {
        this.clientRepository = clientRepository;
        this.encoder = encoder;
        this.transactionManager = transactionManager;
    }

    public Client getClientByLogin(String login){
        return clientRepository.getClientByLogin(login);
    }

    public List<Client> getClientByCustomParams(String phone, String email, String fullName, String stringBirthDate, Pageable pageable){
        LocalDate birthDate = stringBirthDate == null ? null : LocalDate.parse(stringBirthDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        return clientRepository.getClientCustomParams(phone, email, fullName, birthDate, stringBirthDate == null, pageable);
    }

    public Long createClient(SignupRequest signupRequest){
        Client client = Client.builder()
                .login(signupRequest.getLogin())
                .phones(List.of(signupRequest.getPhone()))
                .emails(List.of(signupRequest.getEmail()))
                .name(signupRequest.getName())
                .middleName(signupRequest.getMiddleName())
                .lastname(signupRequest.getLastname())
                .birthDate(LocalDate.parse(signupRequest.getBirthDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .password(encoder.encode(signupRequest.getPassword())).build();
        log.info("Creating new client with start amount = {}", signupRequest.getStartAmount());
        var deposit = signupRequest.getStartAmount();
        BankAccount bankAccount = BankAccount.builder()
                .client(client)
                .amount(deposit)
                .increaseLimit(deposit.multiply(new BigDecimal(increaseMaxPercent)))
                .lastUpdate(Instant.ofEpochSecond(System.currentTimeMillis() / 1000)).build();
        client.setBankAccount(bankAccount);
        try{
            TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
            TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
            clientRepository.save(client);
            transactionManager.commit(transactionStatus);
        }catch(DataIntegrityViolationException e){
            var cause = e.getCause();
            if(cause instanceof ConstraintViolationException){
                String messageCause = cause.getMessage();
                int from = messageCause.indexOf("unique_") + 7;
                int to = messageCause.indexOf("_constraint", from);
                throw new ServiceException("%s is already taken".formatted(messageCause.substring(from, to)));
            }else{
                log.error(e.getMessage());
                throw e;
            }
        }
        return client.getId();
    }

    public ClientDescription getDescriptionForClient(Client client){
        return ClientDescription.builder().login(client.getLogin())
                .phones(client.getPhones())
                .emails(client.getEmails())
                .birthDate(client.getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .name(client.getName())
                .lastname(client.getLastname())
                .middleName(client.getMiddleName())
                .amount(client.getBankAccount().getAmount()).build();
    }

    public Client updateClient(InformationUpdateRequest updateRequest, String login){
        Client client = null;
        try{
            client = clientRepository.getClientByLogin(login);
            TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
            TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
            if(client == null){
                throw new ServiceException("Current client was deleted");
            }
            if(updateRequest.getEmails() != null){
                if(updateRequest.getEmails().isEmpty()){
                    throw new ServiceException("At least one email must be specified");
                }
                client.setEmails(updateRequest.getEmails());
            }
            if(updateRequest.getPhones() != null){
                if(updateRequest.getPhones().isEmpty()){
                    throw new ServiceException("At least one phone must be specified");
                }
                client.setPhones(updateRequest.getPhones());
            }
            transactionManager.commit(transactionStatus);
        }catch(DataIntegrityViolationException e){
            var cause = e.getCause();
            if(cause instanceof ConstraintViolationException){
                String messageCause = cause.getMessage();
                int from = messageCause.indexOf("unique_") + 7;
                int to = messageCause.indexOf("_constraint", from);
                throw new ServiceException("%s is already taken".formatted(messageCause.substring(from, to)));
            }else{
                log.error(e.getMessage());
                throw e;
            }
        }
        return client;
    }
}
