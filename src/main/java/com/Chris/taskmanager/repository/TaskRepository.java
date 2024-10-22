package com.Chris.taskmanager.repository;

import com.Chris.taskmanager.models.task.Task;
import com.Chris.taskmanager.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findAllByUser(User user);
    Task findTaskByUserAndId(User user, Integer id);
    Task deleteTaskByUserAndId(User user, Integer id);
}
