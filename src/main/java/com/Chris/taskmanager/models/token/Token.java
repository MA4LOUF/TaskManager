package com.Chris.taskmanager.models.token;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    private LocalDateTime expirationTime;

    private LocalDateTime blackListedAt;

    private boolean blacklisted;

    private String username;

    public Token(String token, LocalDateTime expirationTime, String username) {
        this.token = token;
        this.expirationTime = expirationTime;
        this.blackListedAt = null;
        this.blacklisted = false;
        this.username = username;
    }

}

