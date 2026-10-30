package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class UserCreateRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String name;

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }
}