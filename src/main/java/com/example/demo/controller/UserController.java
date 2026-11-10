package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserUpdateRequest;

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
import org.springframework.data.domain.Sort;

import java.util.List;
import jakarta.validation.Valid;
import java.util.Set;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "username", "name");

    public UserController(UserService userService) {
        this.userService = userService;
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

        // 4. 检查原始 sort 参数的格式和方向
        String[] sortParams = request.getParameterValues("sort");

        // 4.1 判断是否传入 sort 参数
        if (sortParams != null) {

            // 4.2 遍历每一个 sort 参数
            for (String rawSort : sortParams) {

                // 4.3 根据逗号拆分字段和方向
                String[] parts = rawSort.split(",", -1);

                // 4.4 检查格式：最多只能有两个部分
                if (parts.length > 2) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid sort format: " + rawSort);
                }

                // 4.5 如果指定了方向，检查是否合法
                if (parts.length == 2) {

                    String direction = parts[1].trim();

                    // 4.6 方向必须是 asc 或 desc
                    if (!direction.equalsIgnoreCase("asc")
                            && !direction.equalsIgnoreCase("desc")) {

                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Invalid sort direction: " + direction);
                    }
                }
            }
        }

        // 5. 检查排序字段白名单
        for (Sort.Order order : pageable.getSort()) {

            String property = order.getProperty();

            if (!ALLOWED_SORT_FIELDS.contains(property)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Unsupported sort field: " + property);
            }
        }

        // 6. 全部检查通过，执行分页查询
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