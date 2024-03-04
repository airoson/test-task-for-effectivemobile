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
    @Test
    @DisplayName("Transfer money with success")
    void transferSuccessFlow(){
        ClientService clientService = mock(ClientService.class);
        BankAccountRepository repo = mock(BankAccountRepository.class);
        BankAccountService service = new BankAccountService(repo, clientService);

        Client client1 = new Client();
        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setClient(client1);
        bankAccount1.setAmount(new BigDecimal("1000.80"));
        client1.setBankAccount(bankAccount1);
        doReturn(client1).when(clientService).getClientByLogin("client1");

        Client client2 = new Client();
        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setClient(client1);
        bankAccount2.setAmount(new BigDecimal("190.50"));
        client2.setBankAccount(bankAccount2);
        doReturn(client2).when(clientService).getClientByLogin("client2");

        BigDecimal transferAmount = new BigDecimal("100.5");
        var result = service.transfer("client1", "client2", transferAmount);

        assertEquals(result.getTransferred(), transferAmount);
        assertEquals(new BigDecimal("900.30"), result.getNewAmount());
    }

    @Test
    @DisplayName("Not enough money to transfer")
    void transferFatalFlow1(){
        ClientService clientService = mock(ClientService.class);
        BankAccountRepository repo = mock(BankAccountRepository.class);
        BankAccountService service = new BankAccountService(repo, clientService);

        Client client1 = new Client();
        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setClient(client1);
        bankAccount1.setAmount(new BigDecimal("15.45"));
        client1.setBankAccount(bankAccount1);
        doReturn(client1).when(clientService).getClientByLogin("client1");

        Client client2 = new Client();
        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setClient(client1);
        bankAccount2.setAmount(new BigDecimal("1000"));
        client2.setBankAccount(bankAccount2);
        doReturn(client2).when(clientService).getClientByLogin("client2");

        BigDecimal transferAmount = new BigDecimal("100.5");
        Exception exception = assertThrows(ServiceException.class, () -> {
            var result = service.transfer("client1", "client2", transferAmount);
        });
        assertEquals("Not enough money for transfer", exception.getMessage());
    }

    @Test
    @DisplayName("Can't transfer to the same account")
    void transferFatalFlow2(){
        ClientService clientService = mock(ClientService.class);
        BankAccountRepository repo = mock(BankAccountRepository.class);
        BankAccountService service = new BankAccountService(repo, clientService);

        Client client1 = new Client();
        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setClient(client1);
        bankAccount1.setAmount(new BigDecimal("100"));
        client1.setBankAccount(bankAccount1);
        doReturn(client1).when(clientService).getClientByLogin("client1");

        BigDecimal transferAmount = new BigDecimal("50.5");
        Exception exception = assertThrows(ServiceException.class, () -> {
            var result = service.transfer("client1", "client1", transferAmount);
        });
        assertEquals("Can't transfer money to the same account", exception.getMessage());
    }

    @Test
    @DisplayName("Can't found one of the clients")
    void transferFatalFlow3(){
        ClientService clientService = mock(ClientService.class);
        BankAccountRepository repo = mock(BankAccountRepository.class);
        BankAccountService service = new BankAccountService(repo, clientService);

        Client client1 = new Client();
        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setClient(client1);
        bankAccount1.setAmount(new BigDecimal("100.6"));
        client1.setBankAccount(bankAccount1);
        doReturn(client1).when(clientService).getClientByLogin("client1");
        doReturn(null).when(clientService).getClientByLogin("client2");
        BigDecimal transferAmount = new BigDecimal("90.89");
        Exception exception = assertThrows(ServiceException.class, () -> {
            var result = service.transfer("client1", "client2", transferAmount);
        });
        assertEquals("Client with login client2 was not found", exception.getMessage());
    }

    @Test
    @DisplayName("negative transfer amount is prohibited")
    void transferFatalFlow4(){
        ClientService clientService = mock(ClientService.class);
        BankAccountRepository repo = mock(BankAccountRepository.class);
        BankAccountService service = new BankAccountService(repo, clientService);

        Client client1 = new Client();
        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setClient(client1);
        bankAccount1.setAmount(new BigDecimal("100"));
        client1.setBankAccount(bankAccount1);
        doReturn(client1).when(clientService).getClientByLogin("client1");

        Client client2 = new Client();
        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setClient(client1);
        bankAccount2.setAmount(new BigDecimal("200"));
        client2.setBankAccount(bankAccount2);
        doReturn(client2).when(clientService).getClientByLogin("client2");

        BigDecimal transferAmount = new BigDecimal("-69.90");
        Exception exception = assertThrows(ServiceException.class, () -> {
            var result = service.transfer("client1", "client2", transferAmount);
        });
        assertEquals("Can transfer only positive amounts", exception.getMessage());
    }
}
