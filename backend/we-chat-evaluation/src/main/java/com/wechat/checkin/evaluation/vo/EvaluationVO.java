package com.wechat.checkin.evaluation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评价VO
 * 用于返回评价信息
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评价信息")
public class EvaluationVO {

    /**
     * 评价ID
     */
    @Schema(description = "评价ID", example = "1")
    private Long id;

    /**
     * 活动ID
     */
    @Schema(description = "活动ID", example = "1")
    private Long activityId;

    /**
     * 教学点ID
     */
    @Schema(description = "教学点ID", example = "1")
    private Long teachingPointId;

    /**
     * 教学点名称
     */
    @Schema(description = "教学点名称", example = "第一教学点")
    private String teachingPointName;

    /**
     * 满意度评分（1-3）
     */
    @Schema(description = "满意度评分", example = "3")
    private Integer q1Satisfaction;

    /**
     * 实用性评分（1-3）
     */
    @Schema(description = "实用性评分", example = "2")
    private Integer q2Practicality;

    /**
     * 质量评分（1-3，可选）
     */
    @Schema(description = "质量评分（可选）", example = "3")
    private Integer q3Quality;

    /**
     * 建议文本
     */
    @Schema(description = "建议文本", example = "希望能增加更多互动环节")
    private String suggestionText;

    /**
     * 提交时间
     */
    @Schema(description = "提交时间")
    private LocalDateTime submittedTime;
}
