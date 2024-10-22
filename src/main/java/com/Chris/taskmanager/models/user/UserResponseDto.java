package com.Chris.taskmanager.models.user;

import com.Chris.taskmanager.models.task.TaskDto;

import java.util.List;

public record UserResponseDto(String username, Role role, List<TaskDto> taskDtos) {
}