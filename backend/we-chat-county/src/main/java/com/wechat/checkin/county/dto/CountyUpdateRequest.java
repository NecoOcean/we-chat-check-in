package com.wechat.checkin.county.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新县域请求
 * 
 * @author WeChat Check-in System
 * @since 1.3.0
 */
@Data
@Schema(description = "更新县域请求")
public class CountyUpdateRequest {

    /**
     * 县域名称
     */
    @Size(max = 64, message = "县域名称长度不能超过64个字符")
    @Schema(description = "县域名称（可选，不传则不更新）", example = "萍乡县")
    private String name;
}

