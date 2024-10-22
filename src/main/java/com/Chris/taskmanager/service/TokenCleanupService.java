package com.Chris.taskmanager.service;

import com.Chris.taskmanager.repository.TokenBlacklistRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class TokenCleanupService {

    private final TokenBlacklistRepository tokenRepository;

    public TokenCleanupService(TokenBlacklistRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Scheduled(cron = "0 0/1 * * * ?")  // At midnight every day
    public void cleanUpExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteByExpirationTimeBefore(now);
        System.out.println("Expired tokens cleanup completed.");
    }
}
