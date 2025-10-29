package com.wechat.checkin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 签到状态枚举
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum CheckInStatusEnum {

    /**
     * 未签到
     */
    NOT_CHECKED("not_checked", "未签到"),

    /**
     * 已签到
     */
    CHECKED("checked", "已签到"),

    /**
     * 迟到签到
     */
    LATE_CHECKED("late_checked", "迟到签到"),

    /**
     * 签到异常
     */
    ABNORMAL("abnormal", "签到异常");

    /**
     * 状态值
     */
    private final String value;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据状态值获取枚举
     */
    public static CheckInStatusEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (CheckInStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否未签到
     */
    public static boolean isNotChecked(String value) {
        return NOT_CHECKED.getValue().equals(value);
    }

    /**
     * 判断是否已签到
     */
    public static boolean isChecked(String value) {
        return CHECKED.getValue().equals(value);
    }

    /**
     * 判断是否迟到签到
     */
    public static boolean isLateChecked(String value) {
        return LATE_CHECKED.getValue().equals(value);
    }

    /**
     * 判断是否签到异常
     */
    public static boolean isAbnormal(String value) {
        return ABNORMAL.getValue().equals(value);
    }

    /**
     * 判断是否已完成签到（包括正常签到和迟到签到）
     */
    public boolean isCompleted() {
        return CHECKED.equals(this) || LATE_CHECKED.equals(this);
    }

    /**
     * 判断是否为正常状态（非异常）
     */
    public boolean isNormal() {
        return !ABNORMAL.equals(this);
    }
}