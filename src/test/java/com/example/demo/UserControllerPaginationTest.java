package com.example.demo;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import com.example.demo.repository.UserRepository;
import com.example.demo.entity.User;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerPaginationTest {

        @Test
        @WithMockUser
        void sizeOverLimitReturns400() throws Exception {
                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "101"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message")
                                                .value("size must be <= 100"));
        }

        @Test
        @Transactional
        @WithMockUser
        void getUsersPageSortsDescending() throws Exception {
                User first = new User();
                first.setUsername("sort_desc_first");
                first.setName("Sort Desc First");
                userRepository.saveAndFlush(first);

                User second = new User();
                second.setUsername("sort_desc_second");
                second.setName("Sort Desc Second");
                userRepository.saveAndFlush(second);

                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "100")
                                                .param("sort", "id,desc"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[*].id")
                                                .value(containsInRelativeOrder(
                                                                second.getId().intValue(),
                                                                first.getId().intValue())));
        }

        @Test
        @Transactional
        @WithMockUser
        void getUsersPageSortsAscending() throws Exception {
                User first = new User();
                first.setUsername("sort_asc_first");
                first.setName("Sort Asc First");
                userRepository.saveAndFlush(first);

                User second = new User();
                second.setUsername("sort_asc_second");
                second.setName("Sort Asc Second");
                userRepository.saveAndFlush(second);

                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "100")
                                                .param("sort", "id,asc"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[*].id")
                                                .value(containsInRelativeOrder(
                                                                first.getId().intValue(),
                                                                second.getId().intValue())));
        }

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

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
        void invalidSizeTypeReturns400() throws Exception {
                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "abc"))
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
                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "-5"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
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
