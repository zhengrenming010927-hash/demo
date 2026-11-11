package com.example.demo.validation;

import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// 1. 注册为 Spring 管理的组件
@Component
public class SortParameterValidator {

    // 2. 定义允许排序的字段白名单
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "username", "name");

    // 3. 定义排序参数校验方法
    public void validate(
            HttpServletRequest request,
            Pageable pageable) {

        // 4. 获取客户端传入的原始 sort 参数
        String[] sortParams = request.getParameterValues("sort");

        // 5. 检查是否存在排序参数
        if (sortParams != null) {

            // 6. 遍历全部排序参数
            for (String rawSort : sortParams) {

                // 7. 按逗号拆分排序字段和方向
                String[] parts = rawSort.split(",", -1);

                // 8. 检查排序参数格式
                if (parts.length > 2) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid sort format: " + rawSort);
                }

                // 9. 检查是否指定了排序方向
                if (parts.length == 2) {

                    String direction = parts[1].trim();

                    // 10. 校验排序方向是否合法
                    if (!direction.equalsIgnoreCase("asc")
                            && !direction.equalsIgnoreCase("desc")) {

                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Invalid sort direction: " + direction);
                    }
                }
            }
        }

        // 11. 遍历 Pageable 中的排序规则
        for (Sort.Order order : pageable.getSort()) {

            // 12. 获取当前排序字段
            String property = order.getProperty();

            // 13. 检查排序字段白名单
            if (!ALLOWED_SORT_FIELDS.contains(property)) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Unsupported sort field: " + property);
            }
        }
    }
}
