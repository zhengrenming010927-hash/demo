package com.example.demo.controller.controller.service;

import com.example.demo.controller.model.User;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public User getUserById(Long id) {
        return new User(id, "user" + id, "Test User");
    }

    public List<User> getUsers() {
        return List.of(
                new User(1L, "user1", "Test User 1"),
                new User(2L, "user2", "Test User 2"));
    }
}
