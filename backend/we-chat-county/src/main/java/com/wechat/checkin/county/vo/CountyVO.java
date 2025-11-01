package com.wechat.checkin.county.vo;

import com.wechat.checkin.common.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 县域视图对象
 * 
 * @author WeChat Check-in System
 * @since 1.3.0
 */
@Data
@Builder
@Schema(description = "县域视图对象")
public class CountyVO {

    /**
     * 县域编码
     */
    @Schema(description = "县域编码", example = "PX")
    private String code;

    /**
     * 县域名称
     */
    @Schema(description = "县域名称", example = "萍乡县")
    private String name;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "enabled")
    private StatusEnum status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
}

