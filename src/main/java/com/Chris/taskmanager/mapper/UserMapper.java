package com.Chris.taskmanager.mapper;

import com.Chris.taskmanager.models.task.Task;
import com.Chris.taskmanager.models.user.User;
import com.Chris.taskmanager.models.user.UserDto;
import com.Chris.taskmanager.models.user.UserResponseDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserMapper {

    private final TaskMapper taskMapper;

    public UserMapper(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    public User toUser(UserDto dto){

        var savedUser = new User();

        if(dto != null) {
            savedUser.setUsername(dto.username());
            savedUser.setPassword(dto.password());
            savedUser.setEmail(dto.email());
            savedUser.setRole(dto.role());
            ArrayList<Task> tasks = new ArrayList<>();
            savedUser.setTasks(tasks);
            return savedUser;
        }

        else{
            throw new IllegalArgumentException("User Cannot be null");
        }
    }

    public UserResponseDto toUserDtoResponse(User user) {
        return new UserResponseDto(user.getUsername(), user.getRole(), user.getTasks().stream().map(this.taskMapper::toTaskDto).toList());
    }
}
