package com.wechat.checkin.checkins;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 打卡参与模块配置类
 * 
 * 负责扫描和注册打卡参与模块中的所有Bean
 * - 打卡提交服务
 * - 打卡数据查询
 * - 打卡统计分析
 * - 幂等性处理
 * - API接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.wechat.checkin.checkins")
public class CheckinsModuleConfig {
}
