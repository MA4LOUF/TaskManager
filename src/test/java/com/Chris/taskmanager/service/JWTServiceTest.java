package com.Chris.taskmanager.service;

import com.Chris.taskmanager.models.token.Token;
import com.Chris.taskmanager.repository.TokenBlacklistRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JWTServiceTest {

    private JWTService jwtService;

    @BeforeEach
    void setUp() {
        TokenBlacklistRepository tokenBlacklistRepository = mock(TokenBlacklistRepository.class);
        jwtService = new JWTService(tokenBlacklistRepository);
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken("user1");
        assertNotNull(token);
    }

    @Test
    void testExtractUsername() {
        String token = jwtService.generateToken("user1");
        String username = jwtService.extractUsername(token);
        assertEquals("user1", username);
    }

    @Test
    void testValidateToken() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");

        String token = jwtService.generateToken("user1");
        boolean isValid = jwtService.validateToken(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void testExpiredToken() {
        String expiredToken = Jwts.builder()
                .subject("user1")
                .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 10))
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60))
                .signWith(jwtService.getKey())
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenExpired(expiredToken));
    }

    @Test
    void blacklistToken() {
        String token = "some-valid-token";

        Token realToken = new Token(token, LocalDateTime.now().plusMinutes(30), "user1");
        Token spyToken = spy(realToken);

        TokenBlacklistRepository repo = mock(TokenBlacklistRepository.class);
        when(repo.findByToken(token)).thenReturn(Optional.of(spyToken));

        JWTService jwtService = new JWTService(repo);

        jwtService.blacklistToken(token);

        assertTrue(spyToken.isBlacklisted());
        verify(repo).save(spyToken);
    }
}

