package com.wechat.checkin.evaluation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评价查询请求DTO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评价查询请求")
public class EvaluationQueryRequest {

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
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须从1开始")
    @Schema(description = "页码", example = "1")
    private Long pageNum = 1L;

    /**
     * 每页数量
     */
    @Min(value = 1, message = "每页数量必须至少为1")
    @Schema(description = "每页数量", example = "10")
    private Long pageSize = 10L;
}
