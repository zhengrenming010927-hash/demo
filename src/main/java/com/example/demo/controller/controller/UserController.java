package com.example.demo.controller.controller;

import com.example.demo.controller.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/users/{id}")
    public User getUser(@PathVariable Long id) {
        return new User(id, "tanaka", "田中");
    }
}