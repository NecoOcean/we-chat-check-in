package com.wechat.checkin.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wechat.checkin.auth.dto.LoginRequest;
import com.wechat.checkin.auth.entity.Admin;
import com.wechat.checkin.auth.mapper.AdminMapper;
import com.wechat.checkin.auth.security.JwtTokenProvider;
import com.wechat.checkin.auth.service.AuthService;
import com.wechat.checkin.auth.vo.LoginResponse;
import com.wechat.checkin.common.enums.StatusEnum;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务实现类
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminMapper adminMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest, String clientIp) {
        log.info("用户登录尝试: username={}, ip={}", loginRequest.getUsername(), clientIp);

        // 1. 使用LambdaQueryWrapper查找用户
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, loginRequest.getUsername());
        Admin admin = adminMapper.selectOne(wrapper);
        
        // 检查用户是否存在
        if (admin == null) {
            log.warn("登录失败: 用户不存在, username={}", loginRequest.getUsername());
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户名或密码错误");
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            log.warn("登录失败: 密码错误, username={}", loginRequest.getUsername());
            throw new BusinessException(ResultCode.PASSWORD_ERROR, "用户名或密码错误");
        }

        // 检查账户状态
        if (!StatusEnum.ENABLED.getValue().equals(admin.getStatus().getValue())) {
            log.warn("登录失败: 账号已禁用, username={}", loginRequest.getUsername());
            throw new BusinessException(ResultCode.USER_DISABLED, "账号已被禁用");
        }

        // 4. 生成JWT令牌
        String accessToken = jwtTokenProvider.generateAccessToken(
                admin.getId(), 
                admin.getUsername(), 
                admin.getRole(), 
                admin.getCountyCode()
        );
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                admin.getId(), 
                admin.getUsername()
        );

        // 5. 更新最后登录信息（如果存在相关字段）
        // TODO: 数据库表中删除了last_login_time和last_login_ip字段，可在审计日志中记录
        // adminMapper.updateLastLoginInfo(admin.getId(), LocalDateTime.now(), clientIp);

        // 6. 构建响应
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .role(admin.getRole())
                .countyCode(admin.getCountyCode())
                .build();

        LoginResponse response = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                .userInfo(userInfo)
                .build();

        log.info("用户登录成功: username={}, role={}, countyCode={}", 
                admin.getUsername(), admin.getRole(), admin.getCountyCode());

        return response;
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        log.info("刷新令牌请求");

        // 1. 验证刷新令牌
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("刷新令牌验证失败");
            throw new BusinessException(ResultCode.TOKEN_INVALID, "刷新令牌无效");
        }

        // 2. 检查令牌类型
        String tokenType = jwtTokenProvider.getTokenTypeFromToken(refreshToken);
        if (!"refresh".equals(tokenType)) {
            log.warn("令牌类型错误: {}", tokenType);
            throw new BusinessException(ResultCode.TOKEN_INVALID, "令牌类型错误");
        }

        // 3. 获取用户信息
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // 4. 查找用户
        Admin admin = adminMapper.selectById(userId);
        if (admin == null || !admin.getUsername().equals(username)) {
            log.warn("刷新令牌对应的用户不存在: userId={}, username={}", userId, username);
            throw new BusinessException(ResultCode.TOKEN_INVALID, "用户不存在");
        }

        // 5. 检查账号状态
        if (!StatusEnum.ENABLED.equals(admin.getStatus())) {
            log.warn("刷新令牌失败: 账号已禁用, username={}", username);
            throw new BusinessException(ResultCode.USER_DISABLED, "账号已被禁用");
        }

        // 6. 生成新的访问令牌
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                admin.getId(), 
                admin.getUsername(), 
                admin.getRole(), 
                admin.getCountyCode()
        );

        // 7. 构建响应
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .role(admin.getRole())
                .countyCode(admin.getCountyCode())
                .build();

        LoginResponse response = LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // 保持原刷新令牌
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                .userInfo(userInfo)
                .build();

        log.info("令牌刷新成功: username={}", username);

        return response;
    }

    @Override
    public void logout(String accessToken) {
        log.info("用户登出");
        // TODO: 实现令牌黑名单机制（可选）
        // 目前JWT是无状态的，客户端删除令牌即可实现登出
        // 如需要服务端控制，可以将令牌加入黑名单（Redis）
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    public Long getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return jwtTokenProvider.getUsernameFromToken(token);
    }

    @Override
    public String getRoleFromToken(String token) {
        return jwtTokenProvider.getRoleFromToken(token);
    }

    @Override
    public String getCountyCodeFromToken(String token) {
        return jwtTokenProvider.getCountyCodeFromToken(token);
    }
}