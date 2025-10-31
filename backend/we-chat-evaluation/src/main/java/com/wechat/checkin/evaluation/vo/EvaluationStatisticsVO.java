package com.wechat.checkin.evaluation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评价统计VO
 * 用于返回评价汇总统计信息
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评价统计信息")
public class EvaluationStatisticsVO {

    /**
     * 活动ID
     */
    @Schema(description = "活动ID", example = "1")
    private Long activityId;

    /**
     * 总评价数
     */
    @Schema(description = "总评价数", example = "50")
    private Long totalCount;

    /**
     * 满意度平均分
     */
    @Schema(description = "满意度平均分", example = "2.8")
    private Double avgSatisfaction;

    /**
     * 实用性平均分
     */
    @Schema(description = "实用性平均分", example = "2.6")
    private Double avgPracticality;

    /**
     * 质量平均分
     */
    @Schema(description = "质量平均分（可选）", example = "2.7")
    private Double avgQuality;

    /**
     * 满意度评分为3的数量
     */
    @Schema(description = "满意度评分为3的数量", example = "30")
    private Long satisfactionLevel3;

    /**
     * 满意度评分为2的数量
     */
    @Schema(description = "满意度评分为2的数量", example = "15")
    private Long satisfactionLevel2;

    /**
     * 满意度评分为1的数量
     */
    @Schema(description = "满意度评分为1的数量", example = "5")
    private Long satisfactionLevel1;

    /**
     * 包含建议的评价数量
     */
    @Schema(description = "包含建议的评价数量", example = "20")
    private Long suggestionCount;
}
