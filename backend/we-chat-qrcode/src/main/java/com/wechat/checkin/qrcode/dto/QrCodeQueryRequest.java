package com.wechat.checkin.qrcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 二维码查询请求DTO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Schema(description = "二维码查询请求")
public class QrCodeQueryRequest {

    /**
     * 活动ID
     */
    @Schema(description = "活动ID", example = "1")
    private Long activityId;

    /**
     * 二维码类型
     */
    @Schema(description = "二维码类型(checkin/evaluation)", example = "checkin")
    private String type;

    /**
     * 二维码状态
     */
    @Schema(description = "二维码状态(enabled/disabled/deleted)", example = "enabled")
    private String status;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Long current = 1L;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Long size = 10L;
}

