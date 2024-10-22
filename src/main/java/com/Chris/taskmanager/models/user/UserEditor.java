package com.Chris.taskmanager.models.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserEditor(
        @NotBlank String username,
        @Email String email,
        String oldPassword,
        @Size(min = 8) String newPassword) {}