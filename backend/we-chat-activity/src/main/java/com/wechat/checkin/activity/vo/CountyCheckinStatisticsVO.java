package com.wechat.checkin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 县域打卡统计VO
 * 用于市级管理员查看各县域的打卡情况统计
 * 
 * @author WeChat Check-in System
 * @since 1.1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "县域打卡统计")
public class CountyCheckinStatisticsVO {

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
     * 参与打卡的教学点数
     */
    @Schema(description = "参与打卡的教学点数", example = "15")
    private Integer participatingPoints;

    /**
     * 累计参与人数
     */
    @Schema(description = "累计参与人数", example = "150")
    private Integer totalAttendees;

    /**
     * 打卡记录总数
     */
    @Schema(description = "打卡记录总数", example = "15")
    private Integer totalCheckins;
}
