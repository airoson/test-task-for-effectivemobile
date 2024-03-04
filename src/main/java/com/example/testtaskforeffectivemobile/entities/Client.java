package com.example.testtaskforeffectivemobile.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="bank_client", uniqueConstraints = {
        @UniqueConstraint(name="unique_login_constraint", columnNames = {"login"})
})
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long id;

    @Column(unique = true)
    private String login;
    private String password;

    private String name;
    @Column(name="middle_name")
    private String middleName;
    private String lastname;
    @Column(name="birth_date")
    private LocalDate birthDate;

    @CollectionTable(uniqueConstraints = {@UniqueConstraint(columnNames = {"phones"}, name="unique_phone_constraint")})
    @ElementCollection
    private List<String> phones;
    @CollectionTable(uniqueConstraints = {@UniqueConstraint(columnNames = {"emails"}, name="unique_email_constraint")})
    @ElementCollection
    private List<String> emails;

    @OneToOne(mappedBy = "client", cascade = {CascadeType.PERSIST})
    @JoinColumns(
            @JoinColumn(name = "bank_account")
    )
    private BankAccount bankAccount;
}
