package com.wechat.checkin.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 活动查询请求DTO
 */
@Data
@Schema(description = "活动查询请求")
public class ActivityQueryRequest {

    @Schema(description = "活动状态", example = "ongoing", allowableValues = {"draft", "ongoing", "ended"})
    private String status;

    @Schema(description = "县域编码", example = "510100")
    private String countyCode;

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
}
