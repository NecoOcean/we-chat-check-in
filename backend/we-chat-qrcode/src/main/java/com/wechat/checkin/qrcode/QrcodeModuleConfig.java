package com.wechat.checkin.qrcode;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 二维码管理模块配置类
 * 
 * 负责扫描和注册二维码管理模块中的所有Bean
 * - 二维码生成服务
 * - 二维码验证服务
 * - 二维码状态管理
 * - 二维码工具类
 * - JWT签名与验证
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.wechat.checkin.qrcode")
public class QrcodeModuleConfig {
}
