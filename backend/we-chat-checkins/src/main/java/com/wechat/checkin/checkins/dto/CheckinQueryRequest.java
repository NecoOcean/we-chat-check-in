package com.wechat.checkin.checkins.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 打卡查询请求DTO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "打卡查询请求")
public class CheckinQueryRequest {

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
     * 当前页码
     */
    @Schema(description = "当前页码，默认1", example = "1")
    private Long current = 1L;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小，默认10，最大100", example = "10")
    private Long size = 10L;
}
