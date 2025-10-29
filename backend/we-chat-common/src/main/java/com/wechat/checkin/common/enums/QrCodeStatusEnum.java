package com.wechat.checkin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 二维码状态枚举
 * 对应数据库qrcodes表的status字段：ENUM('active', 'inactive')
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum QrCodeStatusEnum {

    /**
     * 激活状态
     */
    ACTIVE("active", "激活"),

    /**
     * 非激活状态
     */
    INACTIVE("inactive", "非激活");

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
     * 判断是否为激活状态
     */
    public static boolean isActive(String value) {
        return ACTIVE.getValue().equals(value);
    }

    /**
     * 判断是否为非激活状态
     */
    public static boolean isInactive(String value) {
        return INACTIVE.getValue().equals(value);
    }

    /**
     * 判断是否为激活状态
     */
    public boolean isActiveStatus() {
        return ACTIVE.equals(this);
    }

    /**
     * 判断是否为非激活状态
     */
    public boolean isInactiveStatus() {
        return INACTIVE.equals(this);
    }

    /**
     * 判断二维码是否可用
     */
    public boolean isAvailable() {
        return ACTIVE.equals(this);
    }
}