package com.wechat.checkin.activity.vo;

import com.wechat.checkin.qrcode.vo.QrCodeVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 活动详情响应VO（包含统计信息）
 * 
 * v1.1.0 更新：
 * - 添加 countyStatistics 字段（县域统计）
 * - 添加 checkinDetails 字段（打卡详情列表）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "活动详情响应")
public class ActivityDetailVO {

    @Schema(description = "活动基本信息")
    private ActivityVO activity;

    @Schema(description = "参与教学点数量")
    private Integer participatedCount;

    @Schema(description = "总参与人数")
    private Integer totalAttendees;

    @Schema(description = "评价数量")
    private Integer evaluationCount;

    @Schema(description = "二维码列表（包含打卡和评价二维码）")
    private List<QrCodeVO> qrCodes;

    /**
     * 县域打卡统计列表（仅市级管理员可见）
     * 包含各县域的参与教学点数、累计人数等统计信息
     */
    @Schema(description = "县域打卡统计列表（市级管理员专用）")
    private List<CountyCheckinStatisticsVO> countyStatistics;

    /**
     * 打卡详情列表（所有教学点的打卡信息）
     * 可按县域分组查看
     */
    @Schema(description = "打卡详情列表")
    private List<CheckinDetailVO> checkinDetails;
}
