package com.wechat.checkin.checkins.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.checkin.auth.annotation.RequireDataPermission;
import com.wechat.checkin.auth.annotation.RequireRole;
import com.wechat.checkin.checkins.dto.CheckinQueryRequest;
import com.wechat.checkin.checkins.dto.CheckinSubmitRequest;
import com.wechat.checkin.checkins.service.CheckinService;
import com.wechat.checkin.checkins.vo.CheckinStatisticsVO;
import com.wechat.checkin.checkins.vo.CheckinSubmitResponseVO;
import com.wechat.checkin.checkins.vo.CheckinVO;
import com.wechat.checkin.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 打卡控制器
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/checkins")
@RequiredArgsConstructor
@Tag(name = "打卡管理", description = "打卡提交与查询接口")
public class CheckinController {

    private final CheckinService checkinService;

    /**
     * 提交打卡（参与端接口，无需登录）
     * 
     * @param request 打卡提交请求
     * @return 打卡结果
     */
    @PostMapping("/checkin")
    @Operation(
        summary = "提交打卡",
        description = "教学点扫描打卡二维码后提交打卡信息，包括教学点和实到人数。" +
                     "二维码必须是打卡类型且未过期，活动必须在进行中，同一活动同一教学点只能打卡一次（幂等）。"
    )
    @ApiResponse(
        responseCode = "200",
        description = "打卡成功",
        content = @Content(schema = @Schema(implementation = Result.class))
    )
    public Result<CheckinSubmitResponseVO> submitCheckin(@Valid @RequestBody CheckinSubmitRequest request) {
        log.info("接收打卡提交请求: {}", request);
        CheckinSubmitResponseVO result = checkinService.submitCheckin(request);
        return Result.success("打卡成功", result);
    }

    /**
     * 查询打卡记录（管理端接口，需要登录）
     * 
     * @param request 查询请求
     * @return 打卡记录列表
     */
    @GetMapping("")
    @RequireRole({"city", "county"})
    @RequireDataPermission
    @Operation(
        summary = "查询打卡记录列表",
        description = "分页查询打卡记录，支持按活动、教学点过滤。" +
                     "市级管理员可查看全部，县级管理员仅可查看本县数据。"
    )
    @ApiResponse(
        responseCode = "200",
        description = "查询成功",
        content = @Content(schema = @Schema(implementation = Result.class))
    )
    public Result<Page<CheckinVO>> queryCheckins(CheckinQueryRequest request) {
        log.info("查询打卡记录: {}", request);
        Page<CheckinVO> result = checkinService.queryCheckins(request);
        return Result.success("查询成功", result);
    }

    /**
     * 查询活动的打卡统计信息
     * 
     * @param activityId 活动ID
     * @return 打卡统计信息
     */
    @GetMapping("/statistics/{activityId}")
    @RequireRole({"city", "county"})
    @RequireDataPermission
    @Operation(
        summary = "查询打卡统计信息",
        description = "查询活动的打卡统计信息，包括参与教学点数和累计参与人数。"
    )
    @ApiResponse(
        responseCode = "200",
        description = "查询成功",
        content = @Content(schema = @Schema(implementation = Result.class))
    )
    @Parameter(name = "activityId", description = "活动ID", example = "1", required = true)
    public Result<CheckinStatisticsVO> getCheckinStatistics(
            @PathVariable Long activityId) {
        log.info("查询打卡统计: activityId={}", activityId);
        CheckinStatisticsVO result = checkinService.getCheckinStatistics(activityId);
        return Result.success("查询成功", result);
    }
}
