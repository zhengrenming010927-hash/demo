package com.example.demo.controller.controller.service;

import com.example.demo.UserCreateRequest;
import com.example.demo.UserUpdateRequest;
import com.example.demo.controller.model.User;
import com.example.demo.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 根据 id 查询用户，找不到时返回 404
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "用户不存在"
                ));
    }

    // 查询全部用户
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // 创建用户，id 由数据库生成
    public User createUser(UserCreateRequest request) {
        User entity = new User();

        entity.setUsername(request.getUsername());
        entity.setName(request.getName());

        return userRepository.save(entity);
    }

    // 更新用户，保留原来的 id
    public User updateUser(Long id, UserUpdateRequest request) {
        User entity = getUserById(id);

        entity.setUsername(request.getUsername());
        entity.setName(request.getName());

        return userRepository.save(entity);
    }
}