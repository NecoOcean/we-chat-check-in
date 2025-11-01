package com.wechat.checkin.admin;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 用户管理模块配置类
 * 配置组件扫描
 * 注意：AdminMapper使用auth模块的，无需在此扫描Mapper
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Configuration
@ComponentScan("com.wechat.checkin.admin")
public class AdminModuleConfig {
    
    // 模块配置类，用于Spring自动装配
    
}

