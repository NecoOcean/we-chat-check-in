package com.wechat.checkin.evaluation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动评价实体类
 * 对应数据库表：evaluations
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("evaluations")
public class Evaluation {

    /**
     * 评价ID（主键，自增）
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
     * 满意度（1-3）
     * 1: 不满意
     * 2: 一般
     * 3: 满意
     */
    @TableField("q1_satisfaction")
    private Integer q1Satisfaction;

    /**
     * 实用性（1-3）
     * 1: 弱
     * 2: 中
     * 3: 强
     */
    @TableField("q2_practicality")
    private Integer q2Practicality;

    /**
     * 质量（1-3，可选）
     * 1: 差
     * 2: 中
     * 3: 好
     */
    @TableField("q3_quality")
    private Integer q3Quality;

    /**
     * 建议文本（最多200字）
     */
    @TableField("suggestion_text")
    private String suggestionText;

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
