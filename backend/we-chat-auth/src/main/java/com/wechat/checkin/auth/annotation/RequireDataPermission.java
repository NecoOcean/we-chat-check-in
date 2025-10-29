package com.wechat.checkin.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解
 * 用于标记需要进行数据权限控制的方法
 * 县级管理员只能访问本县数据，市级管理员可以访问所有数据
 * 
 * @author system
 * @since 2024-01-01
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireDataPermission {
    
    /**
     * 权限描述
     */
    String value() default "";
    
    /**
     * 是否启用数据权限过滤
     */
    boolean enabled() default true;
}