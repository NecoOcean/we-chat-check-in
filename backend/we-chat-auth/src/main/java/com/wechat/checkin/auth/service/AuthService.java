package com.wechat.checkin.auth.service;

import com.wechat.checkin.auth.dto.ChangePasswordRequest;
import com.wechat.checkin.auth.dto.LoginRequest;
import com.wechat.checkin.auth.vo.LoginResponse;

/**
 * 认证服务接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @param clientIp 客户端IP
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest, String clientIp);

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * 用户登出
     *
     * @param accessToken 访问令牌
     */
    void logout(String accessToken);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param request 修改密码请求
     */
    void changePassword(Long userId, ChangePasswordRequest request);

    /**
     * 验证令牌
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 从令牌获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    Long getUserIdFromToken(String token);

    /**
     * 从令牌获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    String getUsernameFromToken(String token);

    /**
     * 从令牌获取角色
     *
     * @param token JWT令牌
     * @return 角色
     */
    String getRoleFromToken(String token);

    /**
     * 从令牌获取县级代码
     *
     * @param token JWT令牌
     * @return 县级代码
     */
    String getCountyCodeFromToken(String token);
}