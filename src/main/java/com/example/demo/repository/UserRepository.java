package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<User, Long> {

    // 1. 根据 username 或 name 进行不区分大小写的模糊搜索
    // 2. 使用 Pageable 保留分页和排序功能
    Page<User> findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(
            String username,
            String name,
            Pageable pageable);
}
