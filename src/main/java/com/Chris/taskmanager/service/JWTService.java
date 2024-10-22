package com.Chris.taskmanager.service;

import com.Chris.taskmanager.models.token.Token;
import com.Chris.taskmanager.repository.TokenBlacklistRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JWTService {

    private final TokenBlacklistRepository tokenRepository;

    public JWTService(TokenBlacklistRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public SecretKey getKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode("aH5Gm1z9m3V8cW6cWt5kPfJ9uToNs9SaQ3LpqZ2cJwM="));
    }

    public String generateToken(String username){

        Map<String, Object> claims = new HashMap<>();

        String token = Jwts
                        .builder()
                        .claims()
                        .add(claims)
                        .subject(username)
                        .issuedAt(new Date(System.currentTimeMillis()))
                        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                        .and()
                        .signWith(getKey())
                        .compact();

        Token dbToken = new Token(token, LocalDateTime.now().plusMinutes(30), username);
        tokenRepository.save(dbToken);

        return token;
    }

    public String generateRefreshToken(String username) {
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(getKey())
                .compact();

        Token dbToken = new Token(token, LocalDateTime.now().plusDays(7), username);
        tokenRepository.save(dbToken);

        return token;
    }

    public boolean isTokenBlacklisted(String token) {
        Optional<Token> current =  tokenRepository.findByToken(token);
        return current.map(Token::isBlacklisted).orElse(false);
    }

    public void blacklistToken(String token) {
        tokenRepository.findByToken(token).ifPresent(t -> {
            t.setBlacklisted(true);
            t.setBlackListedAt(LocalDateTime.now());
            tokenRepository.save(t);
        });
    }

    public Claims extractClaims(String token){
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }


    public boolean validateToken(String token, UserDetails userDetailsService){
        String username  = extractUsername(token);
        return (username.equals(userDetailsService.getUsername()) && !isTokenExpired(token));
    }

}
