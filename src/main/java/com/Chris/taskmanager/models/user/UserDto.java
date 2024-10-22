package com.Chris.taskmanager.models.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDto(@NotBlank String username, @Email String email, @NotBlank @Size(min = 8) String password, Role role){}