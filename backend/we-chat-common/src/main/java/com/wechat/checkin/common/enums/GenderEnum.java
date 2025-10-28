package com.wechat.checkin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {

    /**
     * 未知
     */
    UNKNOWN(0, "未知"),

    /**
     * 男性
     */
    MALE(1, "男"),

    /**
     * 女性
     */
    FEMALE(2, "女");

    /**
     * 性别码
     */
    private final Integer code;

    /**
     * 性别描述
     */
    private final String description;

    /**
     * 根据性别码获取枚举
     */
    public static GenderEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (GenderEnum genderEnum : values()) {
            if (genderEnum.getCode().equals(code)) {
                return genderEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否为男性
     */
    public static boolean isMale(Integer code) {
        return MALE.getCode().equals(code);
    }

    /**
     * 判断是否为女性
     */
    public static boolean isFemale(Integer code) {
        return FEMALE.getCode().equals(code);
    }

    /**
     * 判断是否为未知性别
     */
    public static boolean isUnknown(Integer code) {
        return UNKNOWN.getCode().equals(code);
    }
}