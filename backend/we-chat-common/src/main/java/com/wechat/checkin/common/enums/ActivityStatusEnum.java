package com.wechat.checkin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动状态枚举
 * 对应数据库activities表的status字段：ENUM('draft', 'ongoing', 'ended')
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ActivityStatusEnum {

    /**
     * 草稿状态
     */
    DRAFT("draft", "草稿"),

    /**
     * 进行中
     */
    ONGOING("ongoing", "进行中"),

    /**
     * 已结束
     */
    ENDED("ended", "已结束");

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
    public static ActivityStatusEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (ActivityStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为草稿状态
     */
    public static boolean isDraft(String value) {
        return DRAFT.getValue().equals(value);
    }

    /**
     * 判断是否为进行中状态
     */
    public static boolean isOngoing(String value) {
        return ONGOING.getValue().equals(value);
    }

    /**
     * 判断是否为已结束状态
     */
    public static boolean isEnded(String value) {
        return ENDED.getValue().equals(value);
    }

    /**
     * 判断活动是否可以编辑（草稿状态）
     */
    public boolean isEditable() {
        return DRAFT.equals(this);
    }

    /**
     * 判断活动是否正在进行
     */
    public boolean isActive() {
        return ONGOING.equals(this);
    }

    /**
     * 判断活动是否已完成
     */
    public boolean isCompleted() {
        return ENDED.equals(this);
    }
}