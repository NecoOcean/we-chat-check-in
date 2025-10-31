package com.wechat.checkin.activity;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 活动管理模块配置类
 * 
 * 负责扫描和注册活动管理模块中的所有Bean
 * - 活动CRUD服务
 * - 活动状态管理
 * - 活动API接口
 * - 与二维码服务的联动
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.wechat.checkin.activity")
public class ActivityModuleConfig {
}
