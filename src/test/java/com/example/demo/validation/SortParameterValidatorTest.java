package com.example.demo.validation;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class SortParameterValidatorTest {

        // 1. 创建被测试的校验组件
        private final SortParameterValidator validator = new SortParameterValidator();

        @Test
        void validSortFieldDoesNotThrow() {

                // 2. 模拟客户端传入合法排序参数
                MockHttpServletRequest request = new MockHttpServletRequest();

                request.addParameter("sort", "id,asc");

                // 3. 创建 Pageable 分页对象
                Pageable pageable = PageRequest.of(
                                0, 5, Sort.by("id").ascending());

                // 4. 验证合法参数不会抛出异常
                assertDoesNotThrow(
                                () -> validator.validate(request, pageable));
        }

        @Test
        void invalidSortFieldThrows400() {

                // 5. 模拟客户端传入非法排序字段
                MockHttpServletRequest request = new MockHttpServletRequest();

                request.addParameter("sort", "unknownField,asc");

                // 6. 创建包含非法排序字段的 Pageable
                Pageable pageable = PageRequest.of(
                                0, 5, Sort.by("unknownField").ascending());

                // 7. 验证校验器会抛出异常
                ResponseStatusException ex = assertThrows(
                                ResponseStatusException.class,
                                () -> validator.validate(request, pageable));

                // 8. 验证异常状态码为 400
                assertEquals(400, ex.getStatusCode().value());

                // 9. 验证异常原因正确
                assertEquals(
                                "Unsupported sort field: unknownField",
                                ex.getReason());
        }

        @Test
        void invalidSortDirectionThrows400() {

                // 1. 创建模拟 HTTP 请求
                MockHttpServletRequest request = new MockHttpServletRequest();

                // 2. 设置非法排序方向 up
                request.addParameter("sort", "id,up");

                // 3. 创建正常的 Pageable 对象
                Pageable pageable = PageRequest.of(
                                0, 5, Sort.by("id").ascending());

                // 4. 验证非法方向会触发异常
                ResponseStatusException ex = assertThrows(
                                ResponseStatusException.class,
                                () -> validator.validate(request, pageable));

                // 5. 验证 HTTP 状态码为 400
                assertEquals(400, ex.getStatusCode().value());

                // 6. 验证异常信息
                assertEquals(
                                "Invalid sort direction: up",
                                ex.getReason());
        }

        @Test
        void invalidSortFormatThrows400() {

                // 1. 创建模拟 HTTP 请求
                MockHttpServletRequest request = new MockHttpServletRequest();

                // 2. 设置包含三个部分的非法排序参数
                request.addParameter("sort", "id,asc,desc");

                // 3. 创建正常的 Pageable 对象
                Pageable pageable = PageRequest.of(
                                0, 5, Sort.by("id").ascending());

                // 4. 验证参数格式错误会触发异常
                ResponseStatusException ex = assertThrows(
                                ResponseStatusException.class,
                                () -> validator.validate(request, pageable));

                // 5. 验证异常状态码
                assertEquals(400, ex.getStatusCode().value());

                // 6. 验证格式错误信息
                assertEquals(
                                "Invalid sort format: id,asc,desc",
                                ex.getReason());
        }
}
