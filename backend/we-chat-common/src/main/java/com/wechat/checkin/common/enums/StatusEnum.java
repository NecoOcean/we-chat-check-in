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
    ENABLED(1, "启用"),

    /**
     * 禁用
     */
    DISABLED(0, "禁用");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据状态码获取枚举
     */
    public static StatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (StatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否启用
     */
    public static boolean isEnabled(Integer code) {
        return ENABLED.getCode().equals(code);
    }

    /**
     * 判断是否禁用
     */
    public static boolean isDisabled(Integer code) {
        return DISABLED.getCode().equals(code);
    }
}