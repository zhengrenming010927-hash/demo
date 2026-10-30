package com.example.demo.service;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.entity.User;
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

    // 根据 id 查询用户，不存在时返回 404
    public User getUserById(Long id) {

        User entity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "用户不存在"
                ));

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getName()
        );
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

        User entity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "用户不存在"
                ));

        entity.setUsername(request.getUsername());
        entity.setName(request.getName());

        return userRepository.save(entity);
    }

    // 删除用户，不存在时返回 404
    public void deleteUser(Long id) {

        User entity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "用户不存在"
                ));

        userRepository.delete(entity);
    }
}