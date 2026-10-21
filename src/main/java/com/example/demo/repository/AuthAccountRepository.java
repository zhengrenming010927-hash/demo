package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.AuthAccount;

public interface AuthAccountRepository
        extends JpaRepository<AuthAccount, Long> {

    Optional<AuthAccount> findByUsername(String username);
}