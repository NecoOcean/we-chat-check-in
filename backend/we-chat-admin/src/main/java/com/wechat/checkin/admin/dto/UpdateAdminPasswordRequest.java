package com.wechat.checkin.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新管理员密码请求（管理员重置他人密码）
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Schema(description = "更新管理员密码请求")
public class UpdateAdminPasswordRequest {

    /**
     * 新密码
     */
    @Schema(description = "新密码（6-64位）", example = "newPassword123")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 64, message = "新密码长度必须在6-64位之间")
    private String newPassword;
}

