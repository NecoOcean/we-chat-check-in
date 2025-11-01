package com.wechat.checkin.county;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 县域管理模块配置类
 * 负责注册县域管理模块的所有Bean
 * 
 * @author WeChat Check-in System
 * @since 1.3.0
 */
@Configuration
@ComponentScan("com.wechat.checkin.county")
@MapperScan("com.wechat.checkin.county.mapper")
public class CountyModuleConfig {
    // 配置类无需额外代码
    // Spring Boot会自动扫描并注册此包下的所有组件
}

