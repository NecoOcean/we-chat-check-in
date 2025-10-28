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
                        // 允许登录接口无需认证
                        .requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()
                        
                        // 允许健康检查接口
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        
                        // 允许API文档接口
                        .requestMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", 
                                        "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        
                        // 允许静态资源
                        .requestMatchers("/static/**", "/favicon.ico").permitAll()
                        
                        // 其他所有请求都需要认证（通过拦截器处理）
                        .anyRequest().authenticated()
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