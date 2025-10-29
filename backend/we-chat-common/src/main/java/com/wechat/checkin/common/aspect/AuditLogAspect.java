package com.wechat.checkin.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.checkin.common.annotation.AuditLog;
import com.wechat.checkin.common.entity.OperationLog;
import com.wechat.checkin.common.service.OperationLogService;
import com.wechat.checkin.common.util.IpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 审计日志切面
 * 自动记录标注了@AuditLog注解的方法的操作日志
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final ObjectMapper objectMapper;
    private final OperationLogService operationLogService;

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        
        // 创建操作日志对象
        OperationLog operationLog = new OperationLog();
        operationLog.setDescription(auditLog.value());
        operationLog.setOperationType(auditLog.operationType());
        operationLog.setBusinessModule(auditLog.module());
        operationLog.setClassName(className);
        operationLog.setMethodName(methodName);
        operationLog.setCreatedAt(LocalDateTime.now());
        
        // 设置请求信息
        if (request != null) {
            operationLog.setRequestUri(request.getRequestURI());
            operationLog.setRequestMethod(request.getMethod());
            operationLog.setClientIp(IpUtils.getClientIp(request));
            operationLog.setUserAgent(request.getHeader("User-Agent"));
            
            // 从请求头获取用户信息（如果有的话）
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // 这里可以解析JWT获取用户信息，暂时留空
                // 在实际使用时，可以通过JWT工具类解析用户ID和用户名
            }
        }
        
        // 记录请求参数
        if (auditLog.recordParams()) {
            try {
                Object[] args = joinPoint.getArgs();
                String params = Arrays.stream(args)
                    .map(this::filterSensitiveData)
                    .collect(Collectors.toList())
                    .toString();
                operationLog.setRequestParams(params);
            } catch (Exception e) {
                log.warn("记录请求参数失败", e);
                operationLog.setRequestParams("参数记录失败");
            }
        }
        
        Object result = null;
        Exception exception = null;
        
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            operationLog.setStatus(1); // 成功
            
            // 记录返回结果
            if (auditLog.recordResult() && result != null) {
                try {
                    String resultStr = objectMapper.writeValueAsString(result);
                    // 限制结果长度，避免过长
                    if (resultStr.length() > 2000) {
                        resultStr = resultStr.substring(0, 2000) + "...";
                    }
                    operationLog.setResponseResult(resultStr);
                } catch (Exception e) {
                    log.warn("记录返回结果失败", e);
                    operationLog.setResponseResult("结果记录失败");
                }
            }
            
        } catch (Exception e) {
            exception = e;
            operationLog.setStatus(0); // 失败
            
            // 记录异常信息
            if (auditLog.recordException()) {
                String exceptionInfo = e.getClass().getSimpleName() + ": " + e.getMessage();
                if (exceptionInfo.length() > 1000) {
                    exceptionInfo = exceptionInfo.substring(0, 1000) + "...";
                }
                operationLog.setExceptionInfo(exceptionInfo);
            }
            
            throw e;
        } finally {
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            operationLog.setExecutionTime(executionTime);
            
            // 异步保存操作日志
            operationLogService.saveAsync(operationLog);
        }
        
        return result;
    }
    
    /**
     * 过滤敏感数据
     */
    private String filterSensitiveData(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        try {
            String jsonStr = objectMapper.writeValueAsString(obj);
            
            // 过滤密码等敏感字段
            jsonStr = jsonStr.replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"");
            jsonStr = jsonStr.replaceAll("\"token\"\\s*:\\s*\"[^\"]*\"", "\"token\":\"***\"");
            jsonStr = jsonStr.replaceAll("\"secret\"\\s*:\\s*\"[^\"]*\"", "\"secret\":\"***\"");
            
            return jsonStr;
        } catch (Exception e) {
            return obj.toString();
        }
    }
}