package com.Chris.taskmanager.repository;

import com.Chris.taskmanager.models.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenBlacklistRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByToken(String token);

    void  deleteByExpirationTimeBefore(LocalDateTime expirationTime);

    void deleteByUsername(String username);
}
