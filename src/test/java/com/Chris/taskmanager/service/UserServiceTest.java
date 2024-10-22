package com.Chris.taskmanager.service;

import com.Chris.taskmanager.mapper.TaskMapper;
import com.Chris.taskmanager.mapper.UserMapper;
import com.Chris.taskmanager.models.user.Role;
import com.Chris.taskmanager.models.user.User;
import com.Chris.taskmanager.models.user.UserDto;
import com.Chris.taskmanager.models.user.UserLogin;
import com.Chris.taskmanager.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private JWTService jwtService;
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        UserRepository userRepository = mock(UserRepository.class);
        jwtService = mock(JWTService.class);
        authenticationManager = mock(AuthenticationManager.class);
        BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
        UserMapper mapper = mock(UserMapper.class);
        userService = new UserService(userRepository, mapper, authenticationManager, jwtService, encoder);
    }

    @Test
    void loginUserSuccess(){
        UserLogin userLogin = mock(UserLogin.class);
        when(userLogin.username()).thenReturn("user1");
        when(userLogin.password()).thenReturn("password");

        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        when(jwtService.generateToken("user1")).thenReturn("accessToken");

        when(jwtService.generateRefreshToken("user1")).thenReturn("refreshToken");

        Map<String, String>  tokens = userService.loginUser(userLogin);

        assertEquals(jwtService.generateToken("user1"), tokens.get("accessToken"));
        assertEquals(jwtService.generateRefreshToken("user1"), tokens.get("refreshToken"));
    }

    @Test
    void loginUserFail(){
        UserLogin userLogin = mock(UserLogin.class);
        when(userLogin.username()).thenReturn("user1");
        when(userLogin.password()).thenReturn("password");
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenThrow(new RuntimeException("Wrong username or password"));
        assertThrows(RuntimeException.class, () -> userService.loginUser(userLogin));
    }

    @Test
    void testLogoutUser() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer validToken");

        userService.logout(mockRequest);
        verify(jwtService, times(1)).blacklistToken("validToken");
    }

    @Test
    void registerSuccess() {

        UserDto userDto = new UserDto("newUser", "newUser@test.com", "password123", Role.ROLE_USER);

        UserMapper mapper = new UserMapper(new TaskMapper());

        UserRepository userRepository = mock(UserRepository.class);
        BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

        when(userRepository.findByUsername(userDto.username())).thenReturn(null);
        when(encoder.encode(anyString())).thenReturn("encodedPassword123");

        UserService userService = new UserService(userRepository, mapper, null, null, encoder);

        ResponseEntity<?> response = userService.register(userDto);

        assertEquals(201, response.getStatusCode().value());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerFail() {

        UserDto userDto = new UserDto("existingUser", "existingUser@test.com", "password123", Role.ROLE_USER);
        User existingUser = new User();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByUsername(userDto.username())).thenReturn(existingUser);
        UserService userService1 = new UserService(userRepository, null, null, null, null);
        ResponseEntity<?> response = userService1.register(userDto);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Username is already taken.", response.getBody());
        verify(userRepository, never()).save(any(User.class));
    }

}

