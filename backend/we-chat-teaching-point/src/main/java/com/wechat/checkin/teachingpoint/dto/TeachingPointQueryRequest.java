package com.wechat.checkin.teachingpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 教学点查询请求DTO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Schema(description = "教学点查询请求")
public class TeachingPointQueryRequest {

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Integer current = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    /**
     * 教学点名称（模糊查询）
     */
    @Schema(description = "教学点名称（模糊查询）", example = "第一小学")
    private String name;

    /**
     * 县域编码（市级管理员可用）
     */
    @Schema(description = "县域编码", example = "PX")
    private String countyCode;

    /**
     * 状态：enabled/disabled/deleted
     */
    @Schema(description = "状态", example = "enabled", 
            allowableValues = {"enabled", "disabled", "deleted"})
    private String status;
}

