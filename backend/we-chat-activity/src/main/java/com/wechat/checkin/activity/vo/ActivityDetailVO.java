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
}
