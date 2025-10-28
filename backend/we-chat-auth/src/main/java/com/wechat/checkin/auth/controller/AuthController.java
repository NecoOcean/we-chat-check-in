package com.wechat.checkin.auth.controller;

import com.wechat.checkin.auth.dto.LoginRequest;
import com.wechat.checkin.auth.service.AuthService;
import com.wechat.checkin.auth.vo.LoginResponse;
import com.wechat.checkin.common.response.Result;
import com.wechat.checkin.common.util.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供登录、刷新令牌、登出等接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "认证管理", description = "用户登录、登出、令牌管理等认证相关接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @param request HTTP请求
     * @return 登录响应
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "管理员用户登录，返回JWT访问令牌和刷新令牌")
    public Result<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        
        String clientIp = IpUtils.getClientIp(request);
        LoginResponse response = authService.login(loginRequest, clientIp);
        
        return Result.success(response);
    }

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public Result<LoginResponse> refreshToken(
            @Parameter(description = "刷新令牌", required = true)
            @NotBlank(message = "刷新令牌不能为空")
            @RequestParam("refreshToken") String refreshToken) {
        
        LoginResponse response = authService.refreshToken(refreshToken);
        
        return Result.success(response);
    }

    /**
     * 用户登出
     *
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出，使当前访问令牌失效")
    public Result<Void> logout(HttpServletRequest request) {
        
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            authService.logout(accessToken);
        }
        
        return Result.success();
    }

    /**
     * 验证令牌
     *
     * @param token 访问令牌
     * @return 验证结果
     */
    @GetMapping("/validate")
    @Operation(summary = "验证令牌", description = "验证访问令牌是否有效")
    public Result<Boolean> validateToken(
            @Parameter(description = "访问令牌", required = true)
            @NotBlank(message = "访问令牌不能为空")
            @RequestParam("token") String token) {
        
        boolean isValid = authService.validateToken(token);
        
        return Result.success(isValid);
    }

    /**
     * 获取当前用户信息
     *
     * @param request HTTP请求
     * @return 用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "根据访问令牌获取当前登录用户的基本信息")
    public Result<LoginResponse.UserInfo> getCurrentUser(HttpServletRequest request) {
        
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("缺少访问令牌");
        }
        
        String token = authHeader.substring(7);
        
        // 验证令牌
        if (!authService.validateToken(token)) {
            return Result.error("访问令牌无效");
        }
        
        // 构建用户信息
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(authService.getUserIdFromToken(token))
                .username(authService.getUsernameFromToken(token))
                .role(authService.getRoleFromToken(token))
                .countyCode(authService.getCountyCodeFromToken(token))
                .build();
        
        return Result.success(userInfo);
    }
}