package com.example.demo.controller.controller;

import com.example.demo.UserCreateRequest;
import com.example.demo.UserUpdateRequest;
import com.example.demo.controller.controller.service.UserService;
import com.example.demo.controller.model.User;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 根据 id 查询用户
    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    // 查询全部用户
    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    // 创建用户
    @PostMapping
    public User createUser(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    // 更新用户
    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable("id") Long id,
            @RequestBody UserUpdateRequest request) {

        return userService.updateUser(id, request);
    }
}