package com.wechat.checkin.checkins.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 打卡成功响应VO
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "打卡成功响应")
public class CheckinSubmitResponseVO {

    /**
     * 是否成功
     */
    @Schema(description = "是否成功", example = "true")
    private Boolean success;

    /**
     * 提交时间
     */
    @Schema(description = "提交时间", example = "2024-03-15T14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedTime;

    /**
     * 打卡ID
     */
    @Schema(description = "打卡ID", example = "1")
    private Long checkinId;
}
