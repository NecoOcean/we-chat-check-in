package com.wechat.checkin.qrcode.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 二维码响应VO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "二维码响应")
public class QrCodeVO {

    /**
     * 二维码ID
     */
    @Schema(description = "二维码ID", example = "1")
    private Long id;

    /**
     * 关联活动ID
     */
    @Schema(description = "关联活动ID", example = "1")
    private Long activityId;

    /**
     * 活动名称
     */
    @Schema(description = "活动名称", example = "2024年春季教学活动")
    private String activityName;

    /**
     * 二维码类型
     */
    @Schema(description = "二维码类型(checkin-打卡, evaluation-评价)", example = "checkin")
    private String type;

    /**
     * 二维码令牌
     */
    @Schema(description = "二维码令牌", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;

    /**
     * 二维码访问URL
     */
    @Schema(description = "二维码访问URL", example = "https://example.com/qr?token=...")
    private String url;

    /**
     * 二维码Base64图片（可选，按需生成）
     */
    @Schema(description = "二维码Base64图片", example = "data:image/png;base64,...")
    private String qrCodeImage;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间", example = "2024-12-31T23:59:59")
    private LocalDateTime expireTime;

    /**
     * 禁用时间
     */
    @Schema(description = "禁用时间", example = "2024-12-31T23:59:59")
    private LocalDateTime disabledTime;

    /**
     * 二维码状态
     */
    @Schema(description = "二维码状态(enabled-启用, disabled-禁用)", example = "enabled")
    private String status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-12-01T10:00:00")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-12-01T10:00:00")
    private LocalDateTime updatedTime;
}

