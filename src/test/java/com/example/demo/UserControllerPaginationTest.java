package com.example.demo;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerPaginationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void invalidPageTypeReturns400() throws Exception {
        mockMvc.perform(
                get("/api/users/page")
                        .param("page", "abc")
                        .param("size", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid parameter: page"));
    }

    @Test
    @WithMockUser
    void nonNumericSizeReturns400Json() throws Exception {
        mockMvc.perform(get("/api/users/page").param("size", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid parameter: size"));
    }

    @Test
    @WithMockUser
    void negativePageReturns400() throws Exception {
        mockMvc.perform(get("/api/users/page").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("page must be >= 0"));
    }

    @Test
    @WithMockUser
    void zeroSizeReturns400() throws Exception {
        mockMvc.perform(
                get("/api/users/page")
                        .param("page", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("size must be >= 1"));
    }

    @Test
    @WithMockUser
    void negativeSizeReturns400() throws Exception {
        mockMvc.perform(get("/api/users/page").param("size", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("size must be >= 1"));
    }

    @Test
    @WithMockUser
    void omittedPaginationParametersReturn200() throws Exception {
        mockMvc.perform(get("/api/users/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser
    void getUsersPageReturnsPaginationInfo() throws Exception {

        mockMvc.perform(
                get("/api/users/page")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }
}
