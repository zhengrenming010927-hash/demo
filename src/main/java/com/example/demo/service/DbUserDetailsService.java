
package com.example.demo.service;

import com.example.demo.entity.AuthAccount;
import com.example.demo.repository.AuthAccountRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DbUserDetailsService implements UserDetailsService {

    private final AuthAccountRepository repository;

    public DbUserDetailsService(AuthAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        AuthAccount account = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String role = account.getRole();

        if (role == null || role.isBlank()) {
            role = "USER";
        }

        return User.withUsername(account.getUsername())
                .password(account.getPasswordHash())
                .disabled(!account.isEnabled())
                .roles(role)
                .build();
    }
}