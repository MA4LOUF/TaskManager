package com.Chris.taskmanager.repository;

import com.Chris.taskmanager.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String userName);
}