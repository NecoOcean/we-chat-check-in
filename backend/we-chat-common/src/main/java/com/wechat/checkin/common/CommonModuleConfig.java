package com.wechat.checkin.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 公共模块配置类
 * 
 * 负责扫描和注册公共模块中的所有Bean
 * - 工具类
 * - 通用组件
 * - 公共配置
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.wechat.checkin.common")
public class CommonModuleConfig {
}
