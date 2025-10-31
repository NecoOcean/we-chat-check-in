package com.wechat.checkin.evaluation;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 评价模块配置类
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "com.wechat.checkin.evaluation")
@MapperScan(basePackages = "com.wechat.checkin.evaluation.mapper")
public class EvaluationModuleConfig {

    public EvaluationModuleConfig() {
        log.info("评价模块配置初始化");
    }
}
