package com.wechat.checkin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 删除状态枚举
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DeletedEnum {

    /**
     * 未删除
     */
    NOT_DELETED(0, "未删除"),

    /**
     * 已删除
     */
    DELETED(1, "已删除");

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
    public static DeletedEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (DeletedEnum deletedEnum : values()) {
            if (deletedEnum.getCode().equals(code)) {
                return deletedEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否已删除
     */
    public static boolean isDeleted(Integer code) {
        return DELETED.getCode().equals(code);
    }

    /**
     * 判断是否未删除
     */
    public static boolean isNotDeleted(Integer code) {
        return NOT_DELETED.getCode().equals(code);
    }
}