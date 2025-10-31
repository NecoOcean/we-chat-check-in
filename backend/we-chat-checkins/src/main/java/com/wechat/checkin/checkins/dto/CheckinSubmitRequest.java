package com.wechat.checkin.checkins.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 打卡提交请求DTO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "打卡提交请求")
public class CheckinSubmitRequest {

    /**
     * 二维码令牌
     */
    @NotBlank(message = "二维码令牌不能为空")
    @Schema(description = "二维码令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    /**
     * 教学点ID
     */
    @NotNull(message = "教学点ID不能为空")
    @Positive(message = "教学点ID必须为正整数")
    @Schema(description = "教学点ID", example = "1")
    private Long teachingPointId;

    /**
     * 实到人数
     */
    @NotNull(message = "实到人数不能为空")
    @Positive(message = "实到人数必须为正整数")
    @Schema(description = "实到人数", example = "30")
    private Integer attendeeCount;
}
