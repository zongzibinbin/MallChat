package com.abin.mallchat.common.common.config;

import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @Author HuaPai
 * @Email flowercard591@gmail.com
 * @Date 2024/1/22 11:39
 * @Description 统一响应体
 */
@RestControllerAdvice(basePackages = "com.abin.mallchat")
public class BaseResponseAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> parameterType = returnType.getParameterType();
        return !parameterType.isAssignableFrom(ResponseEntity.class);
    }

    /**
     * 响应体重写
     * @param body                  响应体
     * @param returnType            返回类型
     * @param selectedContentType   选择的内容类型
     * @param selectedConverterType 选择的转换类型
     * @param request               请求
     * @param response              响应
     * @return                     响应体
     */
    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // 原始类型就直接返回
        if (body instanceof ApiResult) {
            return body;
        }

        if (body instanceof String) {
            // String 类型需要重置响应体为 Json 格式
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return objectMapper.writeValueAsString(
                    ApiResult.success(body)
            );
        }

        return ApiResult.success(body);
    }
}
