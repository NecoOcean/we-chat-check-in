package com.wechat.checkin.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置类
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * 密码编码器
     *
     * @return BCrypt密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤器链配置
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 禁用CSRF（使用JWT不需要CSRF保护）
                .csrf(csrf -> csrf.disable())
                
                // 禁用CORS（在WebMvcConfig中单独配置）
                .cors(cors -> cors.disable())
                
                // 设置会话管理为无状态（JWT不需要会话）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // 配置请求授权
                .authorizeHttpRequests(authz -> authz
                        // 允许所有请求通过Spring Security
                        // 实际的认证和权限控制由AuthInterceptor和DataPermissionInterceptor处理
                        .anyRequest().permitAll()
                )
                
                // 禁用默认登录页面
                .formLogin(form -> form.disable())
                
                // 禁用HTTP Basic认证
                .httpBasic(basic -> basic.disable())
                
                // 禁用默认登出
                .logout(logout -> logout.disable())
                
                .build();
    }
}