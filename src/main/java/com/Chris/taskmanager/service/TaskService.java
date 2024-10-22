package com.Chris.taskmanager.service;

import com.Chris.taskmanager.mapper.TaskMapper;
import com.Chris.taskmanager.models.task.Task;
import com.Chris.taskmanager.models.task.TaskDto;
import com.Chris.taskmanager.models.task.TaskDtoResponse;
import com.Chris.taskmanager.models.user.User;
import com.Chris.taskmanager.repository.TaskRepository;
import com.Chris.taskmanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepo;
    private final TaskMapper taskMapper;
    private final UserRepository userRepo;

    public TaskService(TaskRepository taskRepo, TaskMapper taskMapper, UserRepository userRepo) {
        this.taskRepo = taskRepo;
        this.taskMapper = taskMapper;
        this.userRepo = userRepo;
    }

    public ResponseEntity<?> addTask(TaskDto dto) {
        User user = getUser(getCurrentUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        Task task = this.taskMapper.toTask(dto);
        task.setUser(user);

        Task savedTask = this.taskRepo.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.taskMapper.toTaskDtoResponse(savedTask));
    }

    public ResponseEntity<?> findUserTasks() {
        User user = getUser(getCurrentUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        List<TaskDtoResponse> tasks = user.getTasks().stream().map(this.taskMapper::toTaskDtoResponse).toList();
        return ResponseEntity.ok(tasks);
    }

    public ResponseEntity<?> updateUserTask(Integer id, TaskDto dto) {
        ResponseEntity<Task> taskResponse = getUserAndTask(id);
        if (!taskResponse.getStatusCode().is2xxSuccessful()) {
            return taskResponse;
        }

        Task existingTask = taskResponse.getBody();

        assert existingTask != null;
        existingTask.setTitle(dto.title());
        existingTask.setDescription(dto.description());
        existingTask.setPriority(dto.priority());
        existingTask.setStatus(dto.status());
        existingTask.setDueDate(dto.dueDate());

        Task updatedTask = this.taskRepo.save(existingTask);
        TaskDtoResponse updatedTaskDto = this.taskMapper.toTaskDtoResponse(updatedTask);
        return ResponseEntity.ok(updatedTaskDto);
    }

    public ResponseEntity<?> deleteOwnTask(Integer id) {
        ResponseEntity<Task> taskResponse = getUserAndTask(id);
        if (!taskResponse.getStatusCode().is2xxSuccessful()) {
            return taskResponse;
        }

        this.taskRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    public ResponseEntity<?> addTaskToUser(TaskDto dto, Integer userId) {
        User user = this.userRepo.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        }

        Task task = this.taskMapper.toTask(dto);
        task.setUser(user);
        this.taskRepo.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.taskMapper.toTaskDtoResponse(task));
    }

    public ResponseEntity<List<TaskDtoResponse>> findAllTasks() {
        List<TaskDtoResponse> tasks = this.taskRepo.findAll().stream().map(this.taskMapper::toTaskDtoResponse).toList();
        return ResponseEntity.ok(tasks);
    }

    public ResponseEntity<?> updateTask(Integer taskId, TaskDto dto) {
        Task taskToUpdate = this.taskRepo.findById(taskId).orElse(null);

        if(taskToUpdate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task does not exist");
        }

        taskToUpdate.setTitle(dto.title());
        taskToUpdate.setDescription(dto.description());
        taskToUpdate.setPriority(dto.priority());
        taskToUpdate.setStatus(dto.status());
        taskToUpdate.setDueDate(dto.dueDate());

        this.taskRepo.save(taskToUpdate);

        return ResponseEntity.ok(this.taskMapper.toTaskDtoResponse(taskToUpdate));

    }

    public ResponseEntity<?> deleteTask(Integer taskId) {
        Task taskToDelete = this.taskRepo.findById(taskId).orElse(null);

        if (taskToDelete == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task does not exist");
        }

        taskRepo.deleteById(taskId);
        return ResponseEntity.noContent().build();
    }

    protected String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    protected User getUser(String username){
        return this.userRepo.findByUsername(username);
    }

    private ResponseEntity<Task> getUserAndTask(Integer taskId) {
        User user = getUser(getCurrentUsername());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Task task = this.taskRepo.findById(taskId).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if (!task.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        return ResponseEntity.ok(task);
    }
}
