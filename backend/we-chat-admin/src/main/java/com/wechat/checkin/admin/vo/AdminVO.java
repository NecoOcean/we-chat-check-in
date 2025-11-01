package com.wechat.checkin.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wechat.checkin.common.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员信息响应对象
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Schema(description = "管理员信息")
public class AdminVO {

    /**
     * 管理员ID
     */
    @Schema(description = "管理员ID", example = "1")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "PXadmin")
    private String username;

    /**
     * 角色类型
     */
    @Schema(description = "角色类型（city-市级管理员，county-县级管理员）", example = "county")
    private String role;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", example = "县级管理员")
    private String roleName;

    /**
     * 县域代码
     */
    @Schema(description = "县域编码", example = "PX")
    private String countyCode;

    /**
     * 账号状态
     */
    @Schema(description = "账号状态", example = "ENABLED")
    private StatusEnum status;

    /**
     * 状态描述
     */
    @Schema(description = "状态描述", example = "启用")
    private String statusDesc;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedTime;
}

