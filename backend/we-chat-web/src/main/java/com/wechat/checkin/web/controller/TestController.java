package com.wechat.checkin.web.controller;

import com.wechat.checkin.common.response.Result;
import com.wechat.checkin.auth.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 用于测试JWT认证授权系统
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Tag(name = "测试接口", description = "用于测试JWT认证授权系统的接口")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Operation(summary = "公开接口测试", description = "无需认证的公开接口")
    @GetMapping("/public")
    public Result<Map<String, Object>> publicEndpoint() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "这是一个公开接口，无需认证");
        data.put("timestamp", LocalDateTime.now());
        data.put("status", "success");
        
        return Result.success(data);
    }

    @Operation(summary = "需要认证的接口", description = "需要JWT Token认证的接口")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/authenticated")
    public Result<Map<String, Object>> authenticatedEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "认证成功！");
        data.put("username", authentication.getName());
        data.put("authorities", authentication.getAuthorities());
        data.put("timestamp", LocalDateTime.now());
        
        return Result.success(data);
    }

    @Operation(summary = "市级管理员接口", description = "需要市级管理员权限的接口")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CITY_ADMIN')")
    @GetMapping("/city-admin")
    public Result<Map<String, Object>> cityAdminEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "市级管理员权限验证成功！");
        data.put("username", authentication.getName());
        data.put("role", "CITY_ADMIN");
        data.put("timestamp", LocalDateTime.now());
        
        return Result.success(data);
    }

    @Operation(summary = "县级管理员接口", description = "需要县级管理员权限的接口")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('COUNTY_ADMIN')")
    @GetMapping("/county-admin")
    public Result<Map<String, Object>> countyAdminEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "县级管理员权限验证成功！");
        data.put("username", authentication.getName());
        data.put("role", "COUNTY_ADMIN");
        data.put("timestamp", LocalDateTime.now());
        
        return Result.success(data);
    }

    @Operation(summary = "教学点管理员接口", description = "需要教学点管理员权限的接口")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('TEACHING_POINT_ADMIN')")
    @GetMapping("/teaching-point-admin")
    public Result<Map<String, Object>> teachingPointAdminEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "教学点管理员权限验证成功！");
        data.put("username", authentication.getName());
        data.put("role", "TEACHING_POINT_ADMIN");
        data.put("timestamp", LocalDateTime.now());
        
        return Result.success(data);
    }

    @Operation(summary = "自定义权限接口", description = "使用自定义权限注解的接口")
    @SecurityRequirement(name = "bearerAuth")
    @RequirePermission("activity:read")
    @GetMapping("/custom-permission")
    public Result<Map<String, Object>> customPermissionEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "自定义权限验证成功！");
        data.put("username", authentication.getName());
        data.put("permission", "activity:read");
        data.put("timestamp", LocalDateTime.now());
        
        return Result.success(data);
    }
}