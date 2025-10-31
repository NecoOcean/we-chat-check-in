package com.wechat.checkin.checkins.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 打卡实体类
 * 对应数据库表：checkins
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("checkins")
public class Checkin {

    /**
     * 打卡ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动ID
     */
    @TableField("activity_id")
    private Long activityId;

    /**
     * 教学点ID
     */
    @TableField("teaching_point_id")
    private Long teachingPointId;

    /**
     * 实到人数
     */
    @TableField("attendee_count")
    private Integer attendeeCount;

    /**
     * 提交时间
     */
    @TableField("submitted_time")
    private LocalDateTime submittedTime;

    /**
     * 来源二维码ID
     */
    @TableField("source_qrcode_id")
    private Long sourceQrcodeId;
}
