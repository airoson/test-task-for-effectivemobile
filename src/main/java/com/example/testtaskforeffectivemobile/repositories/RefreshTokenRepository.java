package com.example.testtaskforeffectivemobile.repositories;

import com.example.testtaskforeffectivemobile.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    boolean existsByIdAndExpiresAfter(String id, Instant before);
}
