package com.wechat.checkin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 * 对应数据库admins表的role字段：ENUM('city', 'county')
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum UserRoleEnum {

    /**
     * 市级管理员
     */
    CITY("city", "市级管理员"),

    /**
     * 县级管理员
     */
    COUNTY("county", "县级管理员"),

    /**
     * 普通用户
     */
    USER("user", "普通用户");

    /**
     * 角色值（对应数据库中的enum值）
     */
    private final String value;

    /**
     * 角色描述
     */
    private final String description;

    /**
     * 根据角色值获取枚举
     */
    public static UserRoleEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (UserRoleEnum role : values()) {
            if (role.getValue().equals(value)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 判断是否为市级管理员
     */
    public static boolean isCityAdmin(String value) {
        return CITY.getValue().equals(value);
    }

    /**
     * 判断是否为县级管理员
     */
    public static boolean isCountyAdmin(String value) {
        return COUNTY.getValue().equals(value);
    }

    /**
     * 判断是否为普通用户
     */
    public static boolean isUser(String value) {
        return USER.getValue().equals(value);
    }

    /**
     * 判断是否为管理员角色
     */
    public boolean isAdmin() {
        return CITY.equals(this) || COUNTY.equals(this);
    }

    /**
     * 判断是否为市级管理员
     */
    public boolean isCityRole() {
        return CITY.equals(this);
    }

    /**
     * 判断是否为县级管理员
     */
    public boolean isCountyRole() {
        return COUNTY.equals(this);
    }

    /**
     * 判断是否为普通用户
     */
    public boolean isUserRole() {
        return USER.equals(this);
    }
}