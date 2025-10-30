package com.wechat.checkin.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建活动请求DTO
 */
@Data
@Schema(description = "创建活动请求")
public class CreateActivityRequest {

    @NotBlank(message = "活动名称不能为空")
    @Schema(description = "活动名称", example = "2024年春季教学活动")
    private String name;

    @Schema(description = "活动说明", example = "本次活动旨在提升教学质量")
    private String description;

    @NotNull(message = "打卡开始时间不能为空")
    @Schema(description = "打卡开始时间", example = "2024-03-01T08:00:00")
    private LocalDateTime startTime;

    @NotNull(message = "打卡结束时间不能为空")
    @Schema(description = "打卡结束时间", example = "2024-03-31T18:00:00")
    private LocalDateTime endTime;
}
