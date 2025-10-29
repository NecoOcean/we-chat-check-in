package com.wechat.checkin.web.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI配置类
 * 配置Swagger文档和API安全认证
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Configuration
@EnableKnife4j
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme()));
    }

    /**
     * API信息配置
     */
    private Info apiInfo() {
        return new Info()
                .title("WeChat 打卡小程序 API")
                .description("微信打卡小程序后端API接口文档")
                .version("1.0.0")
                .contact(new Contact()
                        .name("WeChat Check-in Team")
                        .email("support@wechat-checkin.com")
                        .url("https://github.com/wechat-checkin"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * 安全认证配置
     */
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("请输入JWT Token（不需要Bearer前缀，系统会自动添加）");
    }
}