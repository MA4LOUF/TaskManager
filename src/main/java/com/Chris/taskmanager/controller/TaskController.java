package com.Chris.taskmanager.controller;

import com.Chris.taskmanager.models.task.TaskDto;
import com.Chris.taskmanager.models.task.TaskDtoResponse;
import com.Chris.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    //USER CRUD

    @PostMapping("/user/tasks")
    @Valid
    public ResponseEntity<?> addTask(@RequestBody TaskDto dto){
        return this.service.addTask(dto);
    }

    @GetMapping("/user/tasks")
    public ResponseEntity<?> findTasks(){
        return this.service.findUserTasks();
    }

    @PutMapping("/user/tasks/{id}")
    @Valid
    public ResponseEntity<?> updateUserTask(@PathVariable Integer id, @RequestBody TaskDto dto){
        return this.service.updateUserTask(id, dto);
    }

    @DeleteMapping("/user/tasks")
    public ResponseEntity<?> deleteOwnTask(@RequestParam Integer id){
        return this.service.deleteOwnTask(id);
    }

    //ADMIN CAN ALSO CRUD ANYONE'S TASKS

    @PostMapping("/admin/tasks")
    @Valid
    public ResponseEntity<?> addTaskToUser(@RequestBody TaskDto dto, @RequestParam Integer id){
        return this.service.addTaskToUser(dto, id);
    }

    @GetMapping("/admin/tasks")
    public ResponseEntity<List<TaskDtoResponse>> findAllTasks(){
        return this.service.findAllTasks();
    }

    @PutMapping("/admin/tasks/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Integer taskId, @RequestBody TaskDto dto){
        return this.service.updateTask(taskId, dto);
    }

    @DeleteMapping("/admin/tasks/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Integer taskId) {
        return this.service.deleteTask(taskId);
    }
}
