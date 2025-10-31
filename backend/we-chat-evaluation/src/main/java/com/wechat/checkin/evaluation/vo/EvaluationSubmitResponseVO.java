package com.wechat.checkin.evaluation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评价提交响应VO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评价提交响应")
public class EvaluationSubmitResponseVO {

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
     * 提交时间
     */
    @Schema(description = "提交时间")
    private LocalDateTime submittedTime;

    /**
     * 状态消息
     */
    @Schema(description = "状态消息", example = "评价提交成功")
    private String message;
}
