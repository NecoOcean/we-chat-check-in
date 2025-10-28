package com.wechat.checkin.auth.annotation;

import java.lang.annotation.*;

/**
 * 接口权限注解
 * 用于标记需要特定权限才能访问的接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限代码
     * 
     * @return 权限代码
     */
    String value();

    /**
     * 权限描述
     * 
     * @return 权限描述
     */
    String description() default "";

    /**
     * 是否需要县级数据权限过滤
     * 当为true时，县级管理员只能操作自己县的数据
     * 
     * @return 是否需要县级数据权限过滤
     */
    boolean countyDataFilter() default false;
}