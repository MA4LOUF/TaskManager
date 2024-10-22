package com.Chris.taskmanager.mapper;

import com.Chris.taskmanager.models.task.Task;
import com.Chris.taskmanager.models.task.TaskDto;
import com.Chris.taskmanager.models.task.TaskDtoResponse;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toTask(TaskDto dto){
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setPriority(dto.priority());
        task.setStatus(dto.status());
        task.setDueDate(dto.dueDate());
        return task;
    }

    public TaskDto toTaskDto(Task task){
        return new TaskDto(task.getTitle(), task.getDescription(), task.getStatus(), task.getPriority(), task.getDueDate());
    }

    public TaskDtoResponse toTaskDtoResponse(Task task) {
        return new TaskDtoResponse(task.getTitle(), task.getDescription(), task.getStatus(), task.getPriority(), task.getDueDate(), task.getUser().getUsername(), task.getCreatedAt(), task.getUpdatedAt());
    }
}
