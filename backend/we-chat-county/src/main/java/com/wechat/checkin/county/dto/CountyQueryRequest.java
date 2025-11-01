package com.wechat.checkin.county.dto;

import com.wechat.checkin.common.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询县域请求
 * 
 * @author WeChat Check-in System
 * @since 1.3.0
 */
@Data
@Schema(description = "查询县域请求")
public class CountyQueryRequest {

    /**
     * 县域名称（模糊查询）
     */
    @Schema(description = "县域名称（模糊查询）", example = "萍乡")
    private String name;

    /**
     * 县域编码
     */
    @Schema(description = "县域编码", example = "PX")
    private String code;

    /**
     * 状态
     */
    @Schema(description = "状态（enabled-启用，disabled-禁用，deleted-已删除）", example = "enabled")
    private StatusEnum status;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Long current = 1L;

    /**
     * 每页数量
     */
    @Schema(description = "每页数量", example = "10")
    private Long size = 10L;
}

