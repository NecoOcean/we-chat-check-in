package com.wechat.checkin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    /**
     * 启用
     */
    ENABLED("enabled", "启用"),

    /**
     * 禁用
     */
    DISABLED("disabled", "禁用"),

    /**
     * 已删除
     */
    DELETED("deleted", "已删除");

    /**
     * 状态值（对应数据库中的enum值）
     */
    private final String value;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据状态值获取枚举
     */
    public static StatusEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (StatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据状态码获取枚举（兼容旧版本）
     */
    public static StatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        if (code == 1) {
            return ENABLED;
        } else if (code == 0) {
            return DISABLED;
        }
        return null;
    }

    /**
     * 获取状态码（兼容旧版本）
     */
    public Integer getCode() {
        return ENABLED.equals(this) ? 1 : 0;
    }

    /**
     * 判断是否启用
     */
    public static boolean isEnabled(String value) {
        return ENABLED.getValue().equals(value);
    }

    /**
     * 判断是否启用（兼容旧版本）
     */
    public static boolean isEnabled(Integer code) {
        return Integer.valueOf(1).equals(code);
    }

    /**
     * 判断是否禁用
     */
    public static boolean isDisabled(String value) {
        return DISABLED.getValue().equals(value);
    }

    /**
     * 判断是否禁用（兼容旧版本）
     */
    public static boolean isDisabled(Integer code) {
        return Integer.valueOf(0).equals(code);
    }

    /**
     * 判断是否已删除
     */
    public static boolean isDeleted(String value) {
        return DELETED.getValue().equals(value);
    }

    /**
     * 判断是否有效状态（非删除状态）
     */
    public static boolean isValid(String value) {
        return !isDeleted(value);
    }
}