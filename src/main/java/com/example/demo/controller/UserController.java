package com.example.demo.controller;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.service.UserService;
import com.example.demo.entity.User;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 分页查询用户
    @GetMapping("/page")
    public Page<User> getUsersPage(
            @RequestParam(value = "page", required = false) Integer requestedPage,
            @RequestParam(value = "size", required = false) Integer requestedSize,
            Pageable pageable) {

        if (requestedPage != null && requestedPage < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "page must be >= 0");
        }

        if (requestedSize != null && requestedSize < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "size must be >= 1");
        }

        if (requestedSize != null && requestedSize > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "size must be <= 100");
        }

        return userService.getUsersPage(pageable);
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

    // 创建用户，先校验请求数据
    @PostMapping
    public User createUser(
            @Valid @RequestBody UserCreateRequest request) {

        return userService.createUser(request);
    }

    // 更新用户
    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable("id") Long id,
            @RequestBody UserUpdateRequest request) {

        return userService.updateUser(id, request);
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("id") Long id) {

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
