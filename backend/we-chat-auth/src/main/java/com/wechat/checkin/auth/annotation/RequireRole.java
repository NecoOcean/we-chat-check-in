package com.wechat.checkin.auth.annotation;

import java.lang.annotation.*;

/**
 * 角色权限注解
 * 用于标记需要特定角色才能访问的方法或类
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /**
     * 需要的角色
     * 支持多个角色，满足其中一个即可
     * 
     * @return 角色数组
     */
    String[] value();

    /**
     * 是否需要县级权限隔离
     * 当为true时，县级管理员只能访问自己县的数据
     * 
     * @return 是否需要县级权限隔离
     */
    boolean countyIsolation() default false;

    /**
     * 权限描述
     * 
     * @return 权限描述
     */
    String description() default "";
}