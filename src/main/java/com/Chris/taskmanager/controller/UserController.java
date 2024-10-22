package com.Chris.taskmanager.controller;

import com.Chris.taskmanager.models.user.UserDto;
import com.Chris.taskmanager.models.user.UserEditor;
import com.Chris.taskmanager.models.user.UserLogin;
import com.Chris.taskmanager.models.user.UserResponseDto;
import com.Chris.taskmanager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto user){
        return this.userService.register(user);
    }

    @PostMapping("/login")
    @Valid
    public Map<String, String> login(@RequestBody UserLogin user){
        return this.userService.loginUser(user);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> request){
        return this.userService.refreshAccessToken(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout (HttpServletRequest request){
        return this.userService.logout(request);
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<UserResponseDto>> findAllUsers(){
        return this.userService.findAllUsers();
    }

    @PutMapping({"/users/update"})
    public ResponseEntity<?> userUpdate(@RequestBody UserEditor userEditor){
        return this.userService.updateUser(userEditor);
    }

    @PutMapping({"/admin/users/{id}"})
    public ResponseEntity<?> adminUpdateUser(@PathVariable Integer id, @RequestBody UserEditor userEditor){
        return this.userService.adminUpdateUser(id, userEditor);
    }

    @DeleteMapping("/admin/delete/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id){
        return this.userService.deleteUser(id);
    }
}
