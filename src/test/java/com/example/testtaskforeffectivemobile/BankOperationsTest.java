package com.example.testtaskforeffectivemobile;

import com.example.testtaskforeffectivemobile.entities.BankAccount;
import com.example.testtaskforeffectivemobile.entities.Client;
import com.example.testtaskforeffectivemobile.exception.ServiceException;
import com.example.testtaskforeffectivemobile.repositories.BankAccountRepository;
import com.example.testtaskforeffectivemobile.services.BankAccountService;
import com.example.testtaskforeffectivemobile.services.ClientService;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SpringBootTest
class BankOperationsTest {

}
