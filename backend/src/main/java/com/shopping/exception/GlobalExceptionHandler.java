package com.shopping.exception;

import com.shopping.utils.Result;
import com.shopping.utils.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常：{}，请求路径：{}", e.getMessage(), request.getRequestURI());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e, HttpServletRequest request) {
        log.error("参数绑定异常，请求路径：{}", request.getRequestURI());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(ResultCode.VALIDATE_FAILED.getCode(), message);
    }

    /**
     * Process validation exception (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("Validation error: {}", request.getRequestURI());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(ResultCode.VALIDATE_FAILED.getCode(), message);
    }

    /**
     * Process null pointer exception
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("Null pointer: {}", request.getRequestURI(), e);
        return Result.error("System error");
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常：{}，请求路径：{}", e.getMessage(), request.getRequestURI(), e);
        return Result.error("系统内部错误");
    }
}
