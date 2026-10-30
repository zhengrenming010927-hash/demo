package com.example.demo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        // 第一条：H2 控制台专用配置
        @Bean
        @Order(1)
        @ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
        public SecurityFilterChain h2ConsoleSecurityFilterChain(
                        HttpSecurity http) throws Exception {

                var h2Console = PathRequest.toH2Console();

                http
                                .securityMatcher(h2Console)
                                .authorizeHttpRequests(auth -> auth
                                                .anyRequest().permitAll())
                                .csrf(csrf -> csrf.disable())
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.sameOrigin()));

                return http.build();
        }

        // 第二条：普通接口配置
        @Bean
        @Order(2)
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                // 未登录、错误账密共用的 401 响应
                AuthenticationEntryPoint unauthorizedHandler = (request, response, exception) -> {
                        response.setStatus(401);
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json");
                        response.setHeader(
                                        "WWW-Authenticate", "Basic realm=\"API\"");
                        response.getWriter().write(
                                        "{\"status\":401,\"message\":\"Unauthorized\"}");
                };

                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(HttpMethod.GET, "/api/hello").permitAll()
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())

                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(unauthorizedHandler)
                                                .accessDeniedHandler((request, response, exception) -> {
                                                        response.setStatus(403);
                                                        response.setCharacterEncoding("UTF-8");
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        "{\"status\":403,\"message\":\"Forbidden\"}");
                                                }))

                                .httpBasic(basic -> basic
                                                .authenticationEntryPoint(unauthorizedHandler));

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
