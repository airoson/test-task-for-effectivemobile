package com.example.testtaskforeffectivemobile.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bank_account")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    @Column(name = "increase_limit")
    private BigDecimal increaseLimit;
    @Column(name = "last_update")
    private Instant lastUpdate;

    @OneToOne
    private Client client;
}
