package com.wechat.checkin.activity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 打卡详情VO
 * 用于展示活动中所有已参与打卡的教学点信息
 * 
 * @author WeChat Check-in System
 * @since 1.1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "打卡详情")
public class CheckinDetailVO {

    /**
     * 打卡ID
     */
    @Schema(description = "打卡ID", example = "1")
    private Long id;

    /**
     * 教学点ID
     */
    @Schema(description = "教学点ID", example = "1")
    private Long teachingPointId;

    /**
     * 教学点名称
     */
    @Schema(description = "教学点名称", example = "第一小学")
    private String teachingPointName;

    /**
     * 县域编码
     */
    @Schema(description = "县域编码", example = "001")
    private String countyCode;

    /**
     * 县域名称
     */
    @Schema(description = "县域名称", example = "朝阳区")
    private String countyName;

    /**
     * 参与人数
     */
    @Schema(description = "参与人数", example = "30")
    private Integer attendeeCount;

    /**
     * 打卡时间
     */
    @Schema(description = "打卡时间", example = "2024-03-15T14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedTime;
}
