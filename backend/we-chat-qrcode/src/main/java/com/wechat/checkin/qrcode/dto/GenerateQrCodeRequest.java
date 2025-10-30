package com.wechat.checkin.qrcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 生成二维码请求DTO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Schema(description = "生成二维码请求")
public class GenerateQrCodeRequest {

    /**
     * 二维码类型：checkin-打卡，evaluation-评价
     */
    @NotNull(message = "二维码类型不能为空")
    @Schema(description = "二维码类型(checkin-打卡, evaluation-评价)", example = "checkin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    /**
     * 过期时间（可选，默认7天后）
     */
    @Schema(description = "过期时间（ISO 8601格式），不传则默认7天后", example = "2024-12-31T23:59:59")
    private LocalDateTime expireTime;
}

