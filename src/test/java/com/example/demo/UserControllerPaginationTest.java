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
        void uppercaseSortDirectionReturns200() throws Exception {
                // 1. 发送使用大写 DESC 的排序请求
                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "5")
                                                .param("sort", "id,DESC"))
                                // 2. 验证 HTTP 状态码为 200
                                .andExpect(status().isOk())
                                // 3. 验证返回分页数据数组
                                .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @WithMockUser
        void emptySortDirectionReturns400() throws Exception {
                // 1. 发送排序方向为空的请求
                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "5")
                                                .param("sort", "id,"))
                                // 2. 验证 HTTP 状态码为 400
                                .andExpect(status().isBadRequest())
                                // 3. 验证 JSON 状态字段
                                .andExpect(jsonPath("$.status").value(400))
                                // 4. 验证错误提示
                                .andExpect(jsonPath("$.message")
                                                .value("Invalid sort direction: "));
        }

        @Test
        @WithMockUser
        void invalidSortFormatReturns400() throws Exception {
                // 1. 发送包含三个部分的排序参数
                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "5")
                                                .param("sort", "id,asc,desc"))
                                // 2. 验证 HTTP 状态码为 400
                                .andExpect(status().isBadRequest())
                                // 3. 验证 JSON 状态码
                                .andExpect(jsonPath("$.status").value(400))
                                // 4. 验证具体的格式错误信息
                                .andExpect(jsonPath("$.message")
                                                .value("Invalid sort format: id,asc,desc"));
        }

        @Test
        @WithMockUser
        void multipleValidSortFieldsReturns200() throws Exception {
                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "5")
                                                .param("sort", "username,asc", "id,desc"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @WithMockUser
        void multipleSortFieldsWithInvalidFieldReturns400() throws Exception {
                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "5")
                                                .param("sort", "id,asc", "unknownField,desc"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message")
                                                .value("Unsupported sort field: unknownField"));
        }

        @Test
        @WithMockUser
        void invalidSortFieldReturns400() throws Exception {

                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "5")
                                                .param("sort", "unknownField,asc"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message")
                                                .value("Unsupported sort field: unknownField"));
        }

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

        @Test
        @WithMockUser
        void invalidSortDirectionReturnsJson400() throws Exception {

                // 1. 模拟用户发送非法排序方向的请求
                mockMvc.perform(get("/api/users/page")
                                .param("sort", "id,up"))

                                // 2. 验证实际 HTTP 状态码是 400
                                .andExpect(status().isBadRequest())

                                // 3. 验证 JSON 中的 status 字段
                                .andExpect(jsonPath("$.status").value(400))

                                // 4. 验证 JSON 中的错误信息
                                .andExpect(jsonPath("$.message")
                                                .value("Invalid sort direction: up"));
        }

        @Test
        @WithMockUser
        void invalidSortFieldReturnsJson400() throws Exception {

                // 1. 发送包含非法排序字段的请求
                mockMvc.perform(get("/api/users/page")
                                .param("sort", "unknownField,asc"))

                                // 2. 验证 HTTP 状态码为 400
                                .andExpect(status().isBadRequest())

                                // 3. 验证 JSON 状态码
                                .andExpect(jsonPath("$.status").value(400))

                                // 4. 验证具体的错误信息
                                .andExpect(jsonPath("$.message")
                                                .value("Unsupported sort field: unknownField"));
        }

        @Test
        @WithMockUser
        void invalidSortFormatReturnsJson400() throws Exception {

                // 1. 发送包含三个部分的非法排序参数
                mockMvc.perform(get("/api/users/page")
                                .param("sort", "id,asc,desc"))

                                // 2. 验证 HTTP 状态码为 400
                                .andExpect(status().isBadRequest())

                                // 3. 验证 JSON 状态码
                                .andExpect(jsonPath("$.status").value(400))

                                // 4. 验证排序格式的错误信息
                                .andExpect(jsonPath("$.message")
                                                .value("Invalid sort format: id,asc,desc"));
        }

        @Test
        @WithMockUser
        void keywordSearchMatchesNameIgnoreCase() throws Exception {

                // 1. 创建测试用户
                User entity = new User();
                entity.setUsername("day28_name_user_742");
                entity.setName("SpringSearchCase742");

                // 2. 保存测试数据
                User saved = userRepository.saveAndFlush(entity);

                try {
                        // 3. 使用小写关键词搜索 name 字段
                        mockMvc.perform(get("/api/users/page")
                                        .param("keyword", "searchcase742")
                                        .param("page", "0")
                                        .param("size", "5"))

                                        // 4. 验证 HTTP 200
                                        .andExpect(status().isOk())

                                        // 5. 验证只匹配到一名用户
                                        .andExpect(jsonPath("$.totalElements").value(1))

                                        // 6. 验证返回的用户姓名
                                        .andExpect(jsonPath("$.content[0].name")
                                                        .value("SpringSearchCase742"));

                } finally {
                        // 7. 清理本次测试创建的数据
                        userRepository.deleteById(saved.getId());
                }
        }

        @Test
        @WithMockUser
        void blankKeywordUsesNormalPagination() throws Exception {

                // 1. 创建用于验证的测试用户
                User entity = new User();
                entity.setUsername("day28_blank_user_842");
                entity.setName("Blank Keyword Test");

                // 2. 保存测试用户
                User saved = userRepository.saveAndFlush(entity);

                try {
                        // 3. 取得当前数据库中的用户总数
                        long expectedCount = userRepository.count();

                        // 4. 使用只有空格的 keyword 发送请求
                        mockMvc.perform(get("/api/users/page")
                                        .param("keyword", "   ")
                                        .param("page", "0")
                                        .param("size", "100"))

                                        // 5. 验证请求成功
                                        .andExpect(status().isOk())

                                        // 6. 验证返回全部用户的总数量
                                        .andExpect(jsonPath("$.totalElements")
                                                        .value((int) expectedCount))

                                        // 7. 验证分页 content 是数组
                                        .andExpect(jsonPath("$.content").isArray());

                } finally {
                        // 8. 清理测试数据
                        userRepository.deleteById(saved.getId());
                }
        }

        @Test
        @WithMockUser
        void keywordSearchSupportsPaginationAndSorting() throws Exception {

                // 1. 创建第一个测试用户
                User userA = new User();
                userA.setUsername("day28_combo_9061_a");
                userA.setName("Test A");

                // 2. 创建第二个测试用户
                User userB = new User();
                userB.setUsername("day28_combo_9061_b");
                userB.setName("Test B");

                // 3. 创建第三个测试用户
                User userC = new User();
                userC.setUsername("day28_combo_9061_c");
                userC.setName("Test C");

                // 4. 保存三个测试用户
                User savedA = userRepository.saveAndFlush(userA);
                User savedB = userRepository.saveAndFlush(userB);
                User savedC = userRepository.saveAndFlush(userC);

                try {
                        // 5. 请求第一页：关键词搜索 + 每页 2 条 + username 降序
                        mockMvc.perform(get("/api/users/page")
                                        .param("keyword", "day28_combo_9061")
                                        .param("page", "0")
                                        .param("size", "2")
                                        .param("sort", "username,desc"))

                                        // 6. 验证请求成功
                                        .andExpect(status().isOk())

                                        // 7. 验证总记录数为 3
                                        .andExpect(jsonPath("$.totalElements").value(3))

                                        // 8. 验证总页数为 2
                                        .andExpect(jsonPath("$.totalPages").value(2))

                                        // 9. 验证第一页按降序返回 c、b
                                        .andExpect(jsonPath("$.content[0].username")
                                                        .value("day28_combo_9061_c"))
                                        .andExpect(jsonPath("$.content[1].username")
                                                        .value("day28_combo_9061_b"));

                        // 10. 请求第二页，确认剩余的用户
                        mockMvc.perform(get("/api/users/page")
                                        .param("keyword", "day28_combo_9061")
                                        .param("page", "1")
                                        .param("size", "2")
                                        .param("sort", "username,desc"))

                                        // 11. 验证第二页请求成功
                                        .andExpect(status().isOk())

                                        // 12. 验证第二页返回 a
                                        .andExpect(jsonPath("$.content[0].username")
                                                        .value("day28_combo_9061_a"))

                                        // 13. 第二页只有一条记录
                                        .andExpect(jsonPath("$.content.length()").value(1));

                } finally {
                        // 14. 清理本次创建的三个测试用户
                        userRepository.deleteById(savedA.getId());
                        userRepository.deleteById(savedB.getId());
                        userRepository.deleteById(savedC.getId());
                }
        }

        @Test
        @WithMockUser
        void getUsersPageOutOfRangeReturnsEmptyContent() throws Exception {

                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "999")
                                                .param("size", "5")
                                                .param("sort", "id,asc"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.number").value(999))
                                .andExpect(jsonPath("$.size").value(5))
                                .andExpect(jsonPath("$.content").isEmpty())
                                .andExpect(jsonPath("$.totalElements").exists());
        }

        @Test
        @WithMockUser
        void minimumSizeReturns200() throws Exception {

                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size").value(1))
                                .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @WithMockUser
        void maximumSizeReturns200() throws Exception {

                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "100"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size").value(100))
                                .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @WithMockUser
        void invalidSortDirectionReturns400() throws Exception {

                mockMvc.perform(
                                get("/api/users/page")
                                                .param("page", "0")
                                                .param("size", "5")
                                                .param("sort", "id,up"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message")
                                                .value("Invalid sort direction: up"));
        }

        @Test
        @WithMockUser
        void keywordSearchMatchesUsernameIgnoreCase() throws Exception {

                // 1. 创建一条测试用户数据
                User entity = new User();
                entity.setUsername("day28_lookup_731");
                entity.setName("Test User");

                // 2. 保存到测试数据库
                User saved = userRepository.saveAndFlush(entity);

                try {
                        // 3. 使用大写关键词发送搜索请求
                        mockMvc.perform(get("/api/users/page")
                                        .param("keyword", "LOOKUP_731")
                                        .param("page", "0")
                                        .param("size", "5"))

                                        // 4. 验证 HTTP 状态码为200
                                        .andExpect(status().isOk())

                                        // 5. 验证搜索结果数量为1
                                        .andExpect(jsonPath("$.totalElements")
                                                        .value(1))

                                        // 6. 验证返回了预期用户
                                        .andExpect(jsonPath("$.content[0].username")
                                                        .value("day28_lookup_731"));

                } finally {
                        // 7. 清理本测试创建的用户数据
                        userRepository.deleteById(saved.getId());
                }
        }
}
