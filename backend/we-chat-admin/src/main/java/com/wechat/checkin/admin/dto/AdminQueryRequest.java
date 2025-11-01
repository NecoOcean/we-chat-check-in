package com.wechat.checkin.admin.dto;

import com.wechat.checkin.common.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理员查询请求
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Schema(description = "管理员查询请求")
public class AdminQueryRequest {

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
     * 用户名（模糊查询）
     */
    @Schema(description = "用户名（模糊查询）", example = "admin")
    private String username;

    /**
     * 角色类型
     */
    @Schema(description = "角色类型（city-市级，county-县级）", example = "county", allowableValues = {"city", "county"})
    private String role;

    /**
     * 县域代码
     */
    @Schema(description = "县域编码", example = "PX")
    private String countyCode;

    /**
     * 账号状态
     */
    @Schema(description = "账号状态（ENABLED-启用，DISABLED-禁用，DELETED-已删除）", example = "ENABLED")
    private StatusEnum status;
}

