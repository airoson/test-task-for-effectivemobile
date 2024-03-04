package com.example.testtaskforeffectivemobile.repositories;

import com.example.testtaskforeffectivemobile.entities.Client;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client getClientByLogin(String login);

    @Query("SELECT c FROM Client c WHERE (?1 IN (SELECT phones FROM c.phones) OR ?1 IS NULL) AND (?2 IN (SELECT emails FROM c.emails) OR ?2 IS NULL) " +
            " AND ((c.name || ' '|| c.middleName || ' ' || c.lastname) LIKE concat(?3, '%') OR ?3 IS NULL) AND (c.birthDate > ?4 OR ?5 = true)")
    List<Client> getClientCustomParams(String phone, String email, String fullName, LocalDate birthDate, boolean checkDate, Pageable pageable);
}
