package com.example.testtaskforeffectivemobile.repositories;

import com.example.testtaskforeffectivemobile.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> getBankAccountByLastUpdate(Instant lastUpdate);
}
