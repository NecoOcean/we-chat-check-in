package com.wechat.checkin.auth.util;

import com.wechat.checkin.auth.security.UserPrincipal;
import com.wechat.checkin.common.enums.UserRoleEnum;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

/**
 * 安全上下文工具类
 * 用于获取当前登录用户信息
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
public class SecurityContextHolder {

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户
     * @throws BusinessException 未登录时抛出异常
     */
    public static UserPrincipal getCurrentUser() {
        SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ResultCode.LOGIN_REQUIRED, "请先登录");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户身份信息无效");
        }

        return (UserPrincipal) principal;
    }

    /**
     * 获取当前登录用户（如果未登录则返回null）
     *
     * @return 当前登录用户或null
     */
    public static UserPrincipal getCurrentUserOrNull() {
        try {
            return getCurrentUser();
        } catch (Exception e) {
            log.debug("获取当前用户失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名
     */
    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    /**
     * 获取当前登录用户角色
     *
     * @return 角色
     */
    public static String getCurrentUserRole() {
        return getCurrentUser().getRole();
    }

    /**
     * 获取当前登录用户的县域代码
     *
     * @return 县域代码（市级管理员返回null）
     */
    public static String getCurrentUserCountyCode() {
        return getCurrentUser().getCountyCode();
    }

    /**
     * 检查当前用户是否为市级管理员
     *
     * @return 是否为市级管理员
     */
    public static boolean isCityAdmin() {
        UserPrincipal user = getCurrentUserOrNull();
        return user != null && UserRoleEnum.CITY.getValue().equals(user.getRole());
    }

    /**
     * 检查当前用户是否为县级管理员
     *
     * @return 是否为县级管理员
     */
    public static boolean isCountyAdmin() {
        UserPrincipal user = getCurrentUserOrNull();
        return user != null && UserRoleEnum.COUNTY.getValue().equals(user.getRole());
    }

    /**
     * 检查当前用户是否有指定角色
     *
     * @param role 角色
     * @return 是否有指定角色
     */
    public static boolean hasRole(String role) {
        UserPrincipal user = getCurrentUserOrNull();
        return user != null && role.equals(user.getRole());
    }

    /**
     * 检查当前用户是否可以访问指定县域的数据
     *
     * @param countyCode 县域代码
     * @return 是否可以访问
     */
    public static boolean canAccessCountyData(String countyCode) {
        UserPrincipal user = getCurrentUser();
        
        // 市级管理员可以访问所有县域数据
        if (UserRoleEnum.CITY.getValue().equals(user.getRole())) {
            return true;
        }
        
        // 县级管理员只能访问自己县域的数据
        if (UserRoleEnum.COUNTY.getValue().equals(user.getRole())) {
            return user.getCountyCode() != null && user.getCountyCode().equals(countyCode);
        }
        
        return false;
    }

    /**
     * 验证当前用户可以访问指定县域的数据，不可访问时抛出异常
     *
     * @param countyCode 县域代码
     * @throws BusinessException 无权访问时抛出异常
     */
    public static void requireCountyAccess(String countyCode) {
        if (!canAccessCountyData(countyCode)) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED, "无权访问该县域数据");
        }
    }

    /**
     * 验证当前用户是市级管理员，否则抛出异常
     *
     * @throws BusinessException 非市级管理员时抛出异常
     */
    public static void requireCityAdmin() {
        if (!isCityAdmin()) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED, "该操作仅限市级管理员");
        }
    }

    /**
     * 清除安全上下文
     */
    public static void clearContext() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }
}

