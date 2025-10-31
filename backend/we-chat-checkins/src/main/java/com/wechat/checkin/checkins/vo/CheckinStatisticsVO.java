package com.wechat.checkin.checkins.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 打卡统计响应VO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "打卡统计信息")
public class CheckinStatisticsVO {

    /**
     * 活动ID
     */
    @Schema(description = "活动ID", example = "1")
    private Long activityId;

    /**
     * 参与教学点数
     */
    @Schema(description = "参与教学点数", example = "5")
    private Long participatingTeachingPoints;

    /**
     * 累计参与人数
     */
    @Schema(description = "累计参与人数", example = "150")
    private Long totalAttendees;
}
