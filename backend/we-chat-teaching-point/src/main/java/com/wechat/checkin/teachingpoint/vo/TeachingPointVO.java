package com.wechat.checkin.teachingpoint.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教学点响应VO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Schema(description = "教学点信息")
public class TeachingPointVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 教学点ID
     */
    @Schema(description = "教学点ID", example = "1")
    private Long id;

    /**
     * 教学点名称
     */
    @Schema(description = "教学点名称", example = "第一小学教学点")
    private String name;

    /**
     * 归属县域编码
     */
    @Schema(description = "归属县域编码", example = "PX")
    private String countyCode;

    /**
     * 归属县域名称
     */
    @Schema(description = "归属县域名称", example = "屏县")
    private String countyName;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "enabled", 
            allowableValues = {"enabled", "disabled", "deleted"})
    private String status;

    /**
     * 状态描述
     */
    @Schema(description = "状态描述", example = "启用")
    private String statusDesc;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "创建时间", example = "2024-11-01T12:00:00")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "更新时间", example = "2024-11-01T12:00:00")
    private LocalDateTime updatedTime;
}

