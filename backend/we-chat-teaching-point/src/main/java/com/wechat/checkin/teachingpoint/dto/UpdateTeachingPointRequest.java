package com.wechat.checkin.teachingpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新教学点请求DTO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Schema(description = "更新教学点请求")
public class UpdateTeachingPointRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 教学点名称（可选）
     */
    @Size(min = 2, max = 128, message = "教学点名称长度必须在2-128位之间")
    @Schema(description = "教学点名称", example = "第一小学教学点")
    private String name;

    /**
     * 归属县域编码（可选）
     */
    @Size(min = 2, max = 16, message = "县域编码长度必须在2-16位之间")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "县域编码只能包含大写字母、数字和下划线")
    @Schema(description = "归属县域编码", example = "PX")
    private String countyCode;
}

