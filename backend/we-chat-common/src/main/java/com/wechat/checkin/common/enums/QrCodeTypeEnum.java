package com.wechat.checkin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 二维码类型枚举
 * 对应数据库qrcodes表的type字段：ENUM('checkin', 'evaluation')
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum QrCodeTypeEnum {

    /**
     * 签到二维码
     */
    CHECKIN("checkin", "签到二维码"),

    /**
     * 评价二维码
     */
    EVALUATION("evaluation", "评价二维码");

    /**
     * 类型值（对应数据库中的enum值）
     */
    private final String value;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据类型值获取枚举
     */
    public static QrCodeTypeEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (QrCodeTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为签到类型
     */
    public static boolean isCheckin(String value) {
        return CHECKIN.getValue().equals(value);
    }

    /**
     * 判断是否为评价类型
     */
    public static boolean isEvaluation(String value) {
        return EVALUATION.getValue().equals(value);
    }

    /**
     * 判断是否为签到类型
     */
    public boolean isCheckinType() {
        return CHECKIN.equals(this);
    }

    /**
     * 判断是否为评价类型
     */
    public boolean isEvaluationType() {
        return EVALUATION.equals(this);
    }
}