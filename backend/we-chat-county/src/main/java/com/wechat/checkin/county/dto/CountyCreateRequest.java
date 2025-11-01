package com.wechat.checkin.county.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建县域请求
 * 
 * @author WeChat Check-in System
 * @since 1.3.0
 */
@Data
@Schema(description = "创建县域请求")
public class CountyCreateRequest {

    /**
     * 县域编码
     */
    @NotBlank(message = "县域编码不能为空")
    @Size(max = 16, message = "县域编码长度不能超过16个字符")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "县域编码只能包含大写字母和数字")
    @Schema(description = "县域编码（如：PX、LC等）", example = "PX", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    /**
     * 县域名称
     */
    @NotBlank(message = "县域名称不能为空")
    @Size(max = 64, message = "县域名称长度不能超过64个字符")
    @Schema(description = "县域名称", example = "萍乡县", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}

