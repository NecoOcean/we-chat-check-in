package com.wechat.checkin.auth.interceptor;

import com.wechat.checkin.auth.annotation.RequireDataPermission;
import com.wechat.checkin.auth.security.JwtTokenProvider;
import com.wechat.checkin.common.constant.CommonConstants;
import com.wechat.checkin.common.enums.UserRoleEnum;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 数据权限拦截器
 * 实现县域数据权限控制，确保县级管理员只能访问本县数据
 * 
 * @author system
 * @since 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataPermissionInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理Controller方法
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireDataPermission annotation = handlerMethod.getMethodAnnotation(RequireDataPermission.class);
        
        // 如果没有数据权限注解，直接通过
        if (annotation == null) {
            return true;
        }

        // 获取JWT令牌
        String token = extractToken(request);
        if (token == null) {
            throw new BusinessException(ResultCode.TOKEN_MISSING);
        }

        // 验证令牌并获取用户信息
        if (!jwtTokenProvider.validateToken(token)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        String userId = String.valueOf(jwtTokenProvider.getUserIdFromToken(token));
        String userRole = jwtTokenProvider.getRoleFromToken(token);
        
        // 如果是市级管理员，可以访问所有数据
        if (UserRoleEnum.CITY.getValue().equals(userRole)) {
            return true;
        }
        
        // 如果是县级管理员，需要验证数据权限
        if (UserRoleEnum.COUNTY.getValue().equals(userRole)) {
            String countyCode = jwtTokenProvider.getCountyCodeFromToken(token);
            if (countyCode == null) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR, "县级管理员缺少县域信息");
            }
            
            // 将县域代码设置到请求属性中，供后续业务逻辑使用
            request.setAttribute(CommonConstants.COUNTY_CODE_ATTR, countyCode);
            return true;
        }
        
        // 普通用户不允许访问需要数据权限的接口
        throw new BusinessException(ResultCode.PERMISSION_DENIED);
    }

    /**
     * 从请求中提取JWT令牌
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }


}