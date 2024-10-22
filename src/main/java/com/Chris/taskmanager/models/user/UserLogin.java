package com.Chris.taskmanager.models.user;

import jakarta.validation.constraints.NotBlank;

public record UserLogin(@NotBlank String username, @NotBlank String password) {}
