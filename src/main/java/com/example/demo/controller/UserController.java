package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.validation.SortParameterValidator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 1. 声明用户业务 Service
    private final UserService userService;

    // 2. 声明排序参数校验组件
    private final SortParameterValidator sortParameterValidator;

    // 3. 通过构造器注入两个 Spring 组件
    public UserController(
            UserService userService,
            SortParameterValidator sortParameterValidator) {

        // 4. 保存注入的 Service
        this.userService = userService;

        // 5. 保存注入的 Validator
        this.sortParameterValidator = sortParameterValidator;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/page")
    public Page<User> getUsersPage(
            @RequestParam(value = "page", required = false) Integer requestedPage,
            @RequestParam(value = "size", required = false) Integer requestedSize,
            Pageable pageable,
            HttpServletRequest request) {

        // 1. 检查 page 不能小于 0
        if (requestedPage != null && requestedPage < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "page must be >= 0");
        }

        // 2. 检查 size 最小值
        if (requestedSize != null && requestedSize < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "size must be >= 1");
        }

        // 3. 检查 size 最大值
        if (requestedSize != null && requestedSize > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "size must be <= 100");
        }

        // 4. 调用独立组件校验排序字段、方向和格式
        sortParameterValidator.validate(request, pageable);

        // 5. 全部校验通过后，执行分页查询
        return userService.getUsersPage(pageable);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {

        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
