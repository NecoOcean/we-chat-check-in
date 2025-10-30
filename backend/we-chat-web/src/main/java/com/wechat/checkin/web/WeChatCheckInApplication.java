package com.wechat.checkin.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * WeChat 打卡小程序 - 主启动类
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.wechat.checkin.common",
    "com.wechat.checkin.auth",
    "com.wechat.checkin.activity",
    "com.wechat.checkin.web"
})
@MapperScan(basePackages = {
    "com.wechat.checkin.common.mapper",
    "com.wechat.checkin.auth.mapper",
    "com.wechat.checkin.activity.mapper"
})
@EnableAsync
public class WeChatCheckInApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeChatCheckInApplication.class, args);
    }
}