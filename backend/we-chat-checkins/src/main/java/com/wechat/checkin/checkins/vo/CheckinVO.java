package com.wechat.checkin.checkins.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 打卡响应VO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "打卡响应对象")
public class CheckinVO {

    /**
     * 打卡ID
     */
    @Schema(description = "打卡ID", example = "1")
    private Long id;

    /**
     * 活动ID
     */
    @Schema(description = "活动ID", example = "1")
    private Long activityId;

    /**
     * 活动名称
     */
    @Schema(description = "活动名称", example = "2024年春季教学活动")
    private String activityName;

    /**
     * 教学点ID
     */
    @Schema(description = "教学点ID", example = "1")
    private Long teachingPointId;

    /**
     * 教学点名称
     */
    @Schema(description = "教学点名称", example = "第一小学教学点")
    private String teachingPointName;

    /**
     * 实到人数
     */
    @Schema(description = "实到人数", example = "30")
    private Integer attendeeCount;

    /**
     * 提交时间
     */
    @Schema(description = "提交时间", example = "2024-03-15T14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedTime;

    /**
     * 来源二维码ID
     */
    @Schema(description = "来源二维码ID", example = "1")
    private Long sourceQrcodeId;
}
