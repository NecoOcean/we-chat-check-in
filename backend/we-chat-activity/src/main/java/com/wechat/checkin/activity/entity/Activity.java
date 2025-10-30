package com.wechat.checkin.activity.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wechat.checkin.common.enums.ActivityStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动实体类
 * 对应数据库表：activities
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("activities")
public class Activity {

    /**
     * 活动ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动名称
     */
    @TableField("name")
    private String name;

    /**
     * 活动说明
     */
    @TableField("description")
    private String description;

    /**
     * 县域范围（县级活动必填；市级活动为空时覆盖本市所有县域，指定时仅覆盖该县域）
     */
    @TableField("scope_county_code")
    private String scopeCountyCode;

    /**
     * 打卡开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 打卡结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 结束操作时间
     */
    @TableField("ended_time")
    private LocalDateTime endedTime;

    /**
     * 发起管理员ID
     */
    @TableField("created_id")
    private Long createdId;

    /**
     * 活动状态：草稿/进行中/已结束
     */
    @TableField("status")
    private ActivityStatusEnum status;

    /**
     * 创建时间（自动填充）
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间（自动填充）
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
