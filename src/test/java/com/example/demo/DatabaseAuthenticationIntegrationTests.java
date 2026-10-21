package com.example.demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/** Real HTTP authentication checks, isolated from the file database. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:authentication_integration",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class DatabaseAuthenticationIntegrationTests {
    @LocalServerPort
    int port;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    ApplicationContext context;

    final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();

    @BeforeEach
    void prepareAccounts() {
        jdbc.update("DELETE FROM auth_accounts");
        jdbc.update("INSERT INTO auth_accounts(username,password_hash,enabled) VALUES(?,?,?)",
                "testuser", encoder.encode("Test123!"), true);
        jdbc.update("INSERT INTO auth_accounts(username,password_hash,enabled) VALUES(?,?,?)",
                "disableduser", encoder.encode("Test123!"), false);
        if (jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE id=1", Long.class) == 0) {
            jdbc.update("INSERT INTO users(id,username,name) VALUES(1,'fixture_user','Fixture User')");
        }
    }

    HttpResponse<String> get(String path, String username, String password) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .timeout(Duration.ofSeconds(15)).GET();
        if (username != null) {
            String credentials = Base64.getEncoder().encodeToString(
                    (username + ":" + password).getBytes(StandardCharsets.UTF_8));
            request.header("Authorization", "Basic " + credentials);
        }
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void missingCredentialsReturns401() throws Exception {
        assertEquals(401, get("/api/users/1", null, null).statusCode());
    }

    @Test
    void validDatabaseCredentialsReturnUser() throws Exception {
        var response = get("/api/users/1", "testuser", "Test123!");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().matches("(?s).*\\\"id\\\"\\s*:\\s*1\\s*[,}].*"));
    }

    @Test
    void wrongPasswordReturns401() throws Exception {
        assertEquals(401, get("/api/users/1", "testuser", "wrong-password").statusCode());
    }

    @Test
    void publicHelloWorksWithoutCredentials() throws Exception {
        var response = get("/api/hello", null, null);
        assertEquals(200, response.statusCode());
        assertEquals("Hello Spring Boot", response.body());
    }

    @Test
    void nonexistentAccountReturns401() throws Exception {
        assertEquals(401, get("/api/users/1", "missing_account", "Test123!").statusCode());
    }

    @Test
    void disabledAccountReturns401() throws Exception {
        assertEquals(401, get("/api/users/1", "disableduser", "Test123!").statusCode());
    }

    @Test
    void authenticatedMissingUserReturns404() throws Exception {
        assertEquals(404, get("/api/users/99999", "testuser", "Test123!").statusCode());
    }

    @Test
    void h2ConsolePageIsAccessible() throws Exception {
        var response = get("/h2-console/", null, null);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().toLowerCase().contains("h2"));
        assertEquals("SAMEORIGIN", response.headers().firstValue("X-Frame-Options").orElse(""));
    }

    @Test
    void databaseServiceAndBCryptAreConfigured() {
        assertTrue(context.containsBean("dbUserDetailsService"));
        String hash = jdbc.queryForObject(
                "SELECT password_hash FROM auth_accounts WHERE username='testuser'", String.class);
        assertTrue(hash.startsWith("$2"));
        assertTrue(encoder.matches("Test123!", hash));
        assertFalse(encoder.matches("wrong-password", hash));
    }
}
