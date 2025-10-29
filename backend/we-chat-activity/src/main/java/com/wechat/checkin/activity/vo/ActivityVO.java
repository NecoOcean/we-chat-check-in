package com.wechat.checkin.activity.vo;

import com.wechat.checkin.common.enums.ActivityStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动响应VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "活动响应")
public class ActivityVO {

    @Schema(description = "活动ID")
    private Long id;

    @Schema(description = "活动名称")
    private String name;

    @Schema(description = "活动说明")
    private String description;

    @Schema(description = "县域范围编码")
    private String scopeCountyCode;

    @Schema(description = "县域范围名称")
    private String scopeCountyName;

    @Schema(description = "打卡开始时间")
    private LocalDateTime startTime;

    @Schema(description = "打卡结束时间")
    private LocalDateTime endTime;

    @Schema(description = "结束操作时间")
    private LocalDateTime endedTime;

    @Schema(description = "发起管理员ID")
    private Long createdId;

    @Schema(description = "发起管理员用户名")
    private String createdUsername;

    @Schema(description = "活动状态")
    private ActivityStatusEnum status;

    @Schema(description = "活动状态描述")
    private String statusDesc;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
}
