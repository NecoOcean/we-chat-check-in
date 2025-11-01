package com.wechat.checkin.teachingpoint;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 教学点管理模块配置类
 * 配置模块的组件扫描和Mapper扫描路径
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Configuration
@ComponentScan("com.wechat.checkin.teachingpoint")
@MapperScan("com.wechat.checkin.teachingpoint.mapper")
public class TeachingPointModuleConfig {
    // 配置类无需额外代码，注解自动生效
}

