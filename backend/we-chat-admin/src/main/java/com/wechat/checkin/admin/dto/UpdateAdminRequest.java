package com.wechat.checkin.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新管理员信息请求
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Schema(description = "更新管理员信息请求")
public class UpdateAdminRequest {

    /**
     * 用户名（可选）
     */
    @Schema(description = "用户名（4-20位字母、数字、下划线）", example = "PXadmin")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20位之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 县域代码（可选）
     */
    @Schema(description = "县域编码（2-10位字母数字）", example = "PX")
    @Size(min = 2, max = 10, message = "县域代码长度必须在2-10位之间")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "县域代码只能包含大写字母和数字")
    private String countyCode;
}

