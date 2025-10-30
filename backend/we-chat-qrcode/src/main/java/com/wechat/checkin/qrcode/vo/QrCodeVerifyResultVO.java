package com.wechat.checkin.qrcode.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 二维码验证结果VO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "二维码验证结果")
public class QrCodeVerifyResultVO {

    /**
     * 是否有效
     */
    @Schema(description = "是否有效", example = "true")
    private Boolean valid;

    /**
     * 二维码ID
     */
    @Schema(description = "二维码ID", example = "1")
    private Long qrcodeId;

    /**
     * 活动ID
     */
    @Schema(description = "活动ID", example = "1")
    private Long activityId;

    /**
     * 二维码类型
     */
    @Schema(description = "二维码类型(checkin-打卡, evaluation-评价)", example = "checkin")
    private String type;

    /**
     * 验证失败原因（仅在valid=false时存在）
     */
    @Schema(description = "验证失败原因", example = "二维码已过期")
    private String reason;
}

