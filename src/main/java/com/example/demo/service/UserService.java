package com.example.demo.service;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                        "用户不存在"));

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getName());
    }

    // 分页查询用户
    public Page<User> getUsersPage(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(entity -> new User(
                        entity.getId(),
                        entity.getUsername(),
                        entity.getName()));
    }

    // 1. 新增支持关键词搜索的分页方法
    public Page<User> getUsersPage(
            String keyword, Pageable pageable) {

        // 2. 判断关键词是否为空或只有空格
        if (keyword == null || keyword.isBlank()) {

            // 3. 没有有效关键词时，调用原来的分页方法
            return getUsersPage(pageable);
        }

        // 4. 去除关键词前后的空格
        String normalizedKeyword = keyword.trim();

        // 5. 查询 username 或 name 包含关键词的用户
        // 6. Pageable 继续负责分页和排序
        return userRepository
                .findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(
                        normalizedKeyword,
                        normalizedKeyword,
                        pageable)

                // 7. 将数据库实体转换为 API 使用的 User 对象
                .map(entity -> new User(
                        entity.getId(),
                        entity.getUsername(),
                        entity.getName()));
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
                        "用户不存在"));

        entity.setUsername(request.getUsername());
        entity.setName(request.getName());

        return userRepository.save(entity);
    }

    // 删除用户，不存在时返回 404
    public void deleteUser(Long id) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "用户不存在"));

        userRepository.delete(entity);
    }
}
