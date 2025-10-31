package com.wechat.checkin.evaluation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评价提交请求DTO
 * 用于接收教学点扫描评价二维码后的评价内容
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评价提交请求")
public class EvaluationSubmitRequest {

    /**
     * 二维码令牌（包含签名信息和验证）
     */
    @NotBlank(message = "token不能为空")
    @Schema(description = "二维码令牌", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    /**
     * 教学点ID
     */
    @NotNull(message = "教学点ID不能为空")
    @Positive(message = "教学点ID必须为正整数")
    @Schema(description = "教学点ID", example = "1")
    private Long teachingPointId;

    /**
     * 满意度评分（1-3）
     * 1: 不满意 2: 一般 3: 满意
     */
    @NotNull(message = "满意度不能为空")
    @Min(value = 1, message = "满意度必须为1-3之间的值")
    @Max(value = 3, message = "满意度必须为1-3之间的值")
    @Schema(description = "满意度（1-不满意,2-一般,3-满意）", example = "3")
    private Integer q1Satisfaction;

    /**
     * 实用性评分（1-3）
     * 1: 弱 2: 中 3: 强
     */
    @NotNull(message = "实用性不能为空")
    @Min(value = 1, message = "实用性必须为1-3之间的值")
    @Max(value = 3, message = "实用性必须为1-3之间的值")
    @Schema(description = "实用性（1-弱,2-中,3-强）", example = "2")
    private Integer q2Practicality;

    /**
     * 质量评分（1-3，可选）
     * 1: 差 2: 中 3: 好
     */
    @Min(value = 1, message = "质量必须为1-3之间的值")
    @Max(value = 3, message = "质量必须为1-3之间的值")
    @Schema(description = "质量（1-差,2-中,3-好，可选）", example = "3")
    private Integer q3Quality;

    /**
     * 建议文本（最多200字）
     */
    @Size(max = 200, message = "建议最多200个字符")
    @Schema(description = "建议文本（≤200字）", example = "希望能增加更多互动环节")
    private String suggestionText;
}
