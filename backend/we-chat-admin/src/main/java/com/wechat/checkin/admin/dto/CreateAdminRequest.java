package com.wechat.checkin.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建管理员请求
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Schema(description = "创建管理员请求")
public class CreateAdminRequest {

    /**
     * 用户名
     */
    @Schema(description = "用户名（4-20位字母、数字、下划线）", example = "PXadmin")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20位之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码（6-64位）", example = "password123")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度必须在6-64位之间")
    private String password;

    /**
     * 角色
     */
    @Schema(description = "角色类型（county-县级管理员）", example = "county", allowableValues = {"county"})
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^county$", message = "只能创建县级管理员账号")
    private String role;

    /**
     * 县域代码
     */
    @Schema(description = "县域编码（县级管理员必填，2-10位字母数字）", example = "PX")
    @NotBlank(message = "县域代码不能为空")
    @Size(min = 2, max = 10, message = "县域代码长度必须在2-10位之间")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "县域代码只能包含大写字母和数字")
    private String countyCode;
}

