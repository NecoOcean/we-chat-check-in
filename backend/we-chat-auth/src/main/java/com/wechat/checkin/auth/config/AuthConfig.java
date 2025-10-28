package com.wechat.checkin.auth.config;

import com.wechat.checkin.auth.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 认证模块配置类
 * 注册认证拦截器
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // 拦截所有API请求
                .addPathPatterns("/api/**")
                
                // 排除登录相关接口
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/refresh"
                )
                
                // 排除健康检查接口
                .excludePathPatterns(
                        "/actuator/health",
                        "/actuator/info"
                )
                
                // 排除API文档接口
                .excludePathPatterns(
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**"
                )
                
                // 排除静态资源
                .excludePathPatterns(
                        "/static/**",
                        "/favicon.ico"
                );
    }
}