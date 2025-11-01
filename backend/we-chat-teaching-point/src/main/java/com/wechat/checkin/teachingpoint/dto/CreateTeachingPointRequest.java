package com.wechat.checkin.teachingpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建教学点请求DTO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Schema(description = "创建教学点请求")
public class CreateTeachingPointRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 教学点名称
     */
    @NotBlank(message = "教学点名称不能为空")
    @Size(min = 2, max = 128, message = "教学点名称长度必须在2-128位之间")
    @Schema(description = "教学点名称", example = "第一小学教学点", required = true)
    private String name;

    /**
     * 归属县域编码
     */
    @NotBlank(message = "县域编码不能为空")
    @Size(min = 2, max = 16, message = "县域编码长度必须在2-16位之间")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "县域编码只能包含大写字母、数字和下划线")
    @Schema(description = "归属县域编码", example = "PX", required = true)
    private String countyCode;
}

