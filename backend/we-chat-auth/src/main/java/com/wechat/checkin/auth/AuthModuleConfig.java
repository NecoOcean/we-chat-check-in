package com.wechat.checkin.auth;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 认证授权模块配置类
 * 
 * 负责扫描和注册认证授权模块中的所有Bean
 * - JWT令牌提供者
 * - 登录认证服务
 * - 权限拦截器
 * - 权限注解处理
 * - Spring Security配置
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.wechat.checkin.auth")
public class AuthModuleConfig {
}
