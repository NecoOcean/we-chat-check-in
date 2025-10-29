package com.wechat.checkin.common.exception;

import com.wechat.checkin.common.response.Result;
import com.wechat.checkin.common.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理器
 * 统一处理系统中的各种异常，返回标准的错误响应格式
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        // 对于常见的业务异常（如用户不存在、密码错误等），使用INFO级别记录
        if (isCommonBusinessError(e.getCode())) {
            log.info("业务异常: {} - {}", e.getCode(), e.getMessage());
        } else {
            log.warn("业务异常: {} - {}", e.getCode(), e.getMessage());
        }
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 判断是否为常见的业务错误（不需要详细日志记录）
     */
    private boolean isCommonBusinessError(Integer code) {
        return ResultCode.USER_NOT_FOUND.getCode().equals(code) ||
               ResultCode.PASSWORD_ERROR.getCode().equals(code) ||
               ResultCode.USER_DISABLED.getCode().equals(code) ||
               ResultCode.TOKEN_INVALID.getCode().equals(code) ||
               ResultCode.TOKEN_EXPIRED.getCode().equals(code) ||
               ResultCode.UNAUTHORIZED.getCode().equals(code);
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleSystemException(SystemException e, HttpServletRequest request) {
        log.error("系统异常: {} - {}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("认证异常: {}", e.getMessage(), e);
        return Result.error(ResultCode.UNAUTHORIZED.getCode(), "认证失败，请重新登录");
    }

    /**
     * 处理凭据错误异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        log.warn("凭据错误: {}", e.getMessage());
        return Result.error(ResultCode.PASSWORD_ERROR.getCode(), "用户名或密码错误");
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("权限不足: {} - URI: {}", e.getMessage(), request.getRequestURI());
        return Result.error(ResultCode.FORBIDDEN.getCode(), "权限不足，无法访问该资源");
    }

    /**
     * 处理参数校验异常（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("参数校验失败: {}", e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMsg.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }
        
        return Result.error(ResultCode.PARAM_ERROR.getCode(), "参数校验失败: " + errorMsg.toString());
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e, HttpServletRequest request) {
        log.warn("参数绑定异常: {}", e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMsg.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }
        
        return Result.error(ResultCode.PARAM_ERROR.getCode(), "参数绑定失败: " + errorMsg.toString());
    }

    /**
     * 处理约束违反异常（@Validated）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.warn("约束违反异常: {}", e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            errorMsg.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
        }
        
        return Result.error(ResultCode.PARAM_ERROR.getCode(), "参数校验失败: " + errorMsg.toString());
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少请求参数: {}", e.getMessage());
        return Result.error(ResultCode.PARAM_ERROR.getCode(), "缺少必需的请求参数: " + e.getParameterName());
    }

    /**
     * 处理方法参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("方法参数类型不匹配: {}", e.getMessage());
        return Result.error(ResultCode.PARAM_ERROR.getCode(), "参数类型不匹配: " + e.getName());
    }

    /**
     * 处理HTTP请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("HTTP请求方法不支持: {} - URI: {}", e.getMessage(), request.getRequestURI());
        return Result.error(ResultCode.METHOD_NOT_ALLOWED.getCode(), "不支持的HTTP请求方法: " + e.getMethod());
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("请求路径不存在: {}", e.getRequestURL());
        return Result.error(ResultCode.NOT_FOUND.getCode(), "请求的资源不存在: " + e.getRequestURL());
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常 - URI: {}", request.getRequestURI(), e);
        return Result.error(ResultCode.SYSTEM_ERROR.getCode(), "系统内部错误，请联系管理员");
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数异常: {}", e.getMessage());
        return Result.error(ResultCode.PARAM_ERROR.getCode(), "参数错误: " + e.getMessage());
    }

    /**
     * 处理非法状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        log.warn("非法状态异常: {}", e.getMessage());
        return Result.error(ResultCode.BUSINESS_ERROR.getCode(), "操作失败: " + e.getMessage());
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("未捕获的异常 - URI: {}", request.getRequestURI(), e);
        return Result.error(ResultCode.SYSTEM_ERROR.getCode(), "系统内部错误，请联系管理员");
    }
}