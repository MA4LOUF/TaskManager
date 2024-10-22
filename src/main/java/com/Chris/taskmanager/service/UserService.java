package com.Chris.taskmanager.service;

import com.Chris.taskmanager.mapper.UserMapper;
import com.Chris.taskmanager.models.user.*;
import com.Chris.taskmanager.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService{

    private final UserRepository userRepo;
    private final UserMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepo, UserMapper mapper, AuthenticationManager authenticationManager, JWTService jwtService, BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.mapper = mapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.encoder= encoder;
    }

    public ResponseEntity<?> register(UserDto dto) {
        if (userRepo.findByUsername(dto.username()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already taken.");
        }

        User user = this.mapper.toUser(dto);
        user.setPassword(encoder.encode(user.getPassword()));
        this.userRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mapper.toUserDtoResponse(user));
    }

    public Map<String, String> loginUser(UserLogin user){

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.username(), user.password())
        );

        if(auth.isAuthenticated()){
            String accessToken = this.jwtService.generateToken(user.username());
            String refreshToken = this.jwtService.generateRefreshToken(user.username());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            return tokens;
        }

        throw new RuntimeException("Login failed");
    }

    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No token found in the request.");
        }

        jwtService.blacklistToken(token);

        return ResponseEntity.ok("Logged out successfully.");
    }

    public ResponseEntity<List<UserResponseDto>> findAllUsers() {
        List<UserResponseDto> users = this.userRepo.findAll().stream().map(mapper::toUserDtoResponse).toList();
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<?> updateUser(UserEditor userEditor) {
        User user = getUser(getCurrentUsername());

        if(!encoder.matches(userEditor.oldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect");
        }
        return updateUserInfo(userEditor, user);
    }

    public ResponseEntity<?> adminUpdateUser(Integer id, UserEditor userEditor) {

        User toUpdate = this.userRepo.findById(id).orElse(null);

        if(toUpdate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User does not exist");
        }

        return updateUserInfo(userEditor, toUpdate);
    }

    public ResponseEntity<?> deleteUser(Integer id) {
        User user = this.userRepo.findById(id).orElse(null);
        if(user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        else{
            this.userRepo.deleteById(user.getId());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User successfully deleted");
        }
    }

    private User getUser(String username){
        return this.userRepo.findByUsername(username);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    private ResponseEntity<?> updateUserInfo(UserEditor userEditor, User toUpdate) {
        if (userEditor.username() != null) {
            toUpdate.setUsername(userEditor.username());
        }
        if (userEditor.email() != null) {
            toUpdate.setEmail(userEditor.email());
        }

        if (userEditor.newPassword() != null) {
            toUpdate.setPassword(encoder.encode(userEditor.newPassword()));
        }

        this.userRepo.save(toUpdate);
        return ResponseEntity.ok(this.mapper.toUserDtoResponse(toUpdate));
    }

    private String extractTokenFromRequest(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public String refreshToken(String refreshToken) {

        if (jwtService.isTokenBlacklisted(refreshToken) || jwtService.isTokenExpired(refreshToken)) {
            return null;
        }

        String username = jwtService.extractUsername(refreshToken);

        return jwtService.generateToken(username);
    }

    public ResponseEntity<?> refreshAccessToken(Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body("Refresh token is missing");
        }

        String newAccessToken = refreshToken(refreshToken);
        if (newAccessToken != null) {
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
    }
}
