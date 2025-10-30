package com.wechat.checkin.qrcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 验证二维码请求DTO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Schema(description = "验证二维码请求")
public class VerifyQrCodeRequest {

    /**
     * 二维码令牌
     */
    @NotBlank(message = "二维码令牌不能为空")
    @Schema(description = "二维码令牌", example = "eyJhbGciOiJIUzUxMiJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;
}

