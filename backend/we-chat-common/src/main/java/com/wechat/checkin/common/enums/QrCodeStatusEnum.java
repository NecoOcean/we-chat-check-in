package com.wechat.checkin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 二维码状态枚举
 * 对应数据库qrcodes表的status字段：ENUM('enabled', 'disabled', 'deleted')
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum QrCodeStatusEnum {

    /**
     * 启用状态
     */
    ENABLED("enabled", "启用"),

    /**
     * 禁用状态
     */
    DISABLED("disabled", "禁用"),

    /**
     * 软删除状态
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
    public static QrCodeStatusEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (QrCodeStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为启用状态
     */
    public static boolean isEnabled(String value) {
        return ENABLED.getValue().equals(value);
    }

    /**
     * 判断是否为禁用状态
     */
    public static boolean isDisabled(String value) {
        return DISABLED.getValue().equals(value);
    }

    /**
     * 判断是否为已删除状态
     */
    public static boolean isDeleted(String value) {
        return DELETED.getValue().equals(value);
    }

    /**
     * 判断是否为启用状态
     */
    public boolean isEnabledStatus() {
        return ENABLED.equals(this);
    }

    /**
     * 判断是否为禁用状态
     */
    public boolean isDisabledStatus() {
        return DISABLED.equals(this);
    }

    /**
     * 判断二维码是否可用（启用状态）
     */
    public boolean isAvailable() {
        return ENABLED.equals(this);
    }

    /**
     * 判断二维码是否有效（非删除状态）
     */
    public boolean isValid() {
        return !DELETED.equals(this);
    }
}