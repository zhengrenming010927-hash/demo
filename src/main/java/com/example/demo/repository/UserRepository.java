package com.example.demo.repository;

import com.example.demo.controller.model.User;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UserRepository {

    // 根据传入的编号，创建并返回一个示例用户
    public User findById(Long id) {
        return new User(id, "user" + id, "Test User");
    }

    // 返回包含两个示例用户的列表
    public List<User> findAll() {
        return List.of(
                new User(1L, "user1", "Test User 1"),
                new User(2L, "user2", "Test User 2")
        );
    }
}