package com.wechat.checkin.auth.interceptor;

import com.wechat.checkin.auth.annotation.RequirePermission;
import com.wechat.checkin.auth.annotation.RequireRole;
import com.wechat.checkin.auth.entity.Admin;
import com.wechat.checkin.auth.security.JwtTokenProvider;
import com.wechat.checkin.auth.security.UserPrincipal;
import com.wechat.checkin.common.enums.StatusEnum;
import com.wechat.checkin.common.enums.UserRoleEnum;
import com.wechat.checkin.common.enums.PermissionTypeEnum;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * 认证拦截器
 * 负责JWT令牌验证和权限检查
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理Controller方法
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 提取并验证JWT令牌
        String token = extractTokenFromRequest(request);
        if (token == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "缺少访问令牌");
        }

        // 验证令牌有效性
        if (!jwtTokenProvider.validateToken(token)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "访问令牌无效或已过期");
        }

        // 从令牌中获取用户信息并构建UserPrincipal
        UserPrincipal userPrincipal = buildUserPrincipalFromToken(token);
        
        // 设置Spring Security上下文
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 检查角色权限
        checkRolePermission(method, userPrincipal);

        // 检查接口权限
        checkInterfacePermission(method, userPrincipal);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理Security上下文
        SecurityContextHolder.clearContext();
    }

    /**
     * 从请求中提取JWT令牌
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.debug("Authorization header: {}", bearerToken != null ? bearerToken.substring(0, Math.min(50, bearerToken.length())) + "..." : "null");
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 从JWT令牌构建UserPrincipal
     */
    private UserPrincipal buildUserPrincipalFromToken(String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String username = jwtTokenProvider.getUsernameFromToken(token);
        String role = jwtTokenProvider.getRoleFromToken(token);
        String countyCode = jwtTokenProvider.getCountyCodeFromToken(token);

        // 构建Admin对象
        Admin admin = new Admin();
        admin.setId(userId);
        admin.setUsername(username);
        admin.setRole(role);
        admin.setCountyCode(countyCode);
        admin.setStatus(StatusEnum.getByCode(1)); // 假设状态为启用

        return UserPrincipal.create(admin);
    }

    /**
     * 检查角色权限
     */
    private void checkRolePermission(Method method, UserPrincipal userPrincipal) {
        RequireRole requireRole = method.getAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = method.getDeclaringClass().getAnnotation(RequireRole.class);
        }

        if (requireRole != null) {
            String[] requiredRoles = requireRole.value();
            String userRole = userPrincipal.getRole();

            // 检查用户角色是否在允许的角色列表中
            boolean hasRole = false;
            for (String role : requiredRoles) {
                if (role.equals(userRole)) {
                    hasRole = true;
                    break;
                }
            }

            if (!hasRole) {
                throw new BusinessException(ResultCode.FORBIDDEN, 
                    String.format("访问被拒绝：需要角色 %s，当前角色 %s", 
                        String.join(",", requiredRoles), userRole));
            }

            // 如果启用了县级权限隔离，检查县级权限
            if (requireRole.countyIsolation()) {
                checkCountyPermission(userPrincipal);
            }
        }
    }

    /**
     * 检查接口权限
     */
    private void checkInterfacePermission(Method method, UserPrincipal userPrincipal) {
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        if (requirePermission == null) {
            requirePermission = method.getDeclaringClass().getAnnotation(RequirePermission.class);
        }

        if (requirePermission != null) {
            String permissionCode = requirePermission.value();
            
            // 这里可以扩展权限检查逻辑
            // 目前简单检查：市级管理员拥有所有权限，县级管理员只能访问县级权限
            if (UserRoleEnum.COUNTY.getValue().equals(userPrincipal.getRole())) {
                // 县级管理员权限检查
                if (!isCountyPermission(permissionCode)) {
                    throw new BusinessException(ResultCode.FORBIDDEN, 
                        String.format("访问被拒绝：县级管理员无权限访问 %s", permissionCode));
                }

                // 如果启用了县级数据权限过滤，进行数据权限检查
                if (requirePermission.countyDataFilter()) {
                    checkCountyDataPermission(userPrincipal);
                }
            }
        }
    }

    /**
     * 检查县级权限
     */
    private void checkCountyPermission(UserPrincipal userPrincipal) {
        if (UserRoleEnum.COUNTY.getValue().equals(userPrincipal.getRole())) {
            String countyCode = userPrincipal.getCountyCode();
            if (countyCode == null || countyCode.trim().isEmpty()) {
                throw new BusinessException(ResultCode.FORBIDDEN, "县级管理员缺少县区代码");
            }
        }
    }

    /**
     * 判断是否为县级权限
     */
    private boolean isCountyPermission(String permissionCode) {
        // 使用枚举替代硬编码字符串，提高代码可维护性
        return PermissionTypeEnum.isCountyRelated(permissionCode);
    }

    /**
     * 检查县级数据权限
     */
    private void checkCountyDataPermission(UserPrincipal userPrincipal) {
        // 县级数据权限检查逻辑
        // 这里可以设置ThreadLocal变量，供Service层使用进行数据过滤
        log.debug("县级数据权限检查: countyCode={}", userPrincipal.getCountyCode());
    }
}