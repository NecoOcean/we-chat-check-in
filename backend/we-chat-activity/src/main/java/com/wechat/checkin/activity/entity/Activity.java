package com.wechat.checkin.activity.entity;

import com.wechat.checkin.common.enums.ActivityStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动说明
     */
    private String description;

    /**
     * 县域范围（县级活动必填；市级活动为空时覆盖本市所有县域，指定时仅覆盖该县域）
     */
    private String scopeCountyCode;

    /**
     * 打卡开始时间
     */
    private LocalDateTime startTime;

    /**
     * 打卡结束时间
     */
    private LocalDateTime endTime;

    /**
     * 结束操作时间
     */
    private LocalDateTime endedTime;

    /**
     * 发起管理员ID
     */
    private Long createdId;

    /**
     * 活动状态：草稿/进行中/已结束
     */
    private ActivityStatusEnum status;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
