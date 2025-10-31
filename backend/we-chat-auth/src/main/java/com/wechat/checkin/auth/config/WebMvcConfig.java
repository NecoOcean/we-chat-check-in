package com.wechat.checkin.auth.config;

import com.wechat.checkin.auth.interceptor.AuthInterceptor;
import com.wechat.checkin.auth.interceptor.DataPermissionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 配置拦截器等Web相关组件
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final DataPermissionInterceptor dataPermissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册认证拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        // 认证相关接口（登录、刷新、登出、验证令牌无需认证）
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/validate",
                        // 参与端接口（打卡和评价接口无需登录，通过二维码令牌验证）
                        "/api/checkins/checkin",
                        "/api/checkins/evaluate",
                        "/api/evaluations/evaluation",
                        "/api/qrcodes/verify",
                        // 文档和健康检查
                        "/actuator/**",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/static/**",
                        "/favicon.ico"
                )
                .order(1);

        // 注册数据权限拦截器
        registry.addInterceptor(dataPermissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        // 认证相关接口
                        "/api/auth/**",
                        // 参与端接口（无需数据权限控制）
                        "/api/checkins/checkin",
                        "/api/checkins/evaluate",
                        "/api/evaluations/evaluation",
                        "/api/qrcodes/verify",
                        // 文档和健康检查
                        "/actuator/**",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/static/**",
                        "/favicon.ico"
                )
                .order(2);
    }
}