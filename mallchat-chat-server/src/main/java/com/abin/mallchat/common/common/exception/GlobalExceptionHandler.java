package com.abin.mallchat.common.common.exception;

import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * validation参数校验异常（@RequestBody + @Valid）
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResult methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException e) {
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(x -> errorMsg.append(x.getField()).append(x.getDefaultMessage()).append(","));
        String message = errorMsg.toString();
        log.info("validation parameters error！The reason is:{}", message);
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getErrorCode(), message.substring(0, message.length() - 1));
    }

    /**
     * validation参数校验异常（表单参数绑定）
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BindException.class)
    public ApiResult bindException(BindException e) {
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(x -> errorMsg.append(x.getField()).append(x.getDefaultMessage()).append(","));
        String message = errorMsg.toString();
        log.info("validation parameters error！The reason is:{}", message);
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getErrorCode(), message.substring(0, message.length() - 1));
    }

    /**
     * validation参数校验异常（@RequestParam + @Validated）
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ApiResult constraintViolationExceptionHandler(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(","));
        log.info("constraint violation error！The reason is:{}", message);
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getErrorCode(), message);
    }

    /**
     * 缺少必需的请求参数
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResult<Void> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        log.info("missing request parameter！The reason is:{}", e.getMessage());
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getErrorCode(), "缺少必要参数: " + e.getParameterName());
    }

    /**
     * 请求参数类型不匹配
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResult<Void> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e) {
        log.info("argument type mismatch！The reason is:{}", e.getMessage());
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getErrorCode(),
                String.format("参数'%s'类型不匹配，期望类型: %s", e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知"));
    }

    /**
     * 请求体解析失败（JSON格式错误）
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult<Void> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        log.info("http message not readable！The reason is:{}", e.getMessage());
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getErrorCode(), "请求体格式错误，请检查JSON格式");
    }

    /**
     * 不支持的媒体类型
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ApiResult<Void> httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException e) {
        log.error("media type not supported！The reason is:{}", e.getMessage(), e);
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getErrorCode(), "不支持的媒体类型: " + e.getContentType());
    }

    /**
     * 处理空指针的异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = NullPointerException.class)
    public ApiResult exceptionHandler(NullPointerException e) {
        log.error("null point exception！The reason is: ", e);
        return ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR);
    }

    /**
     * 非法参数异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ApiResult illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.info("illegal argument exception！The reason is：{}", e.getMessage(), e);
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getErrorCode(), e.getMessage());
    }

    /**
     * 未知异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ApiResult systemExceptionHandler(Exception e) {
        log.error("system exception！The reason is：{}", e.getMessage(), e);
        return ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR);
    }

    /**
     * 自定义校验异常（如参数校验等）
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BusinessException.class)
    public ApiResult businessExceptionHandler(BusinessException e) {
        log.info("business exception！The reason is：{}", e.getMessage(), e);
        return ApiResult.fail(e.getErrorCode(), e.getMessage());
    }

    /**
     * http请求方式不支持
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult<Void> handleException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return ApiResult.fail(-1, String.format("不支持'%s'请求", e.getMethod()));
    }

    /**
     * 限流异常
     */
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(value = FrequencyControlException.class)
    public ApiResult frequencyControlExceptionHandler(FrequencyControlException e) {
        log.info("frequencyControl exception！The reason is：{}", e.getMessage(), e);
        return ApiResult.fail(e.getErrorCode(), e.getMessage());
    }
}
