package com.wechat.checkin.activity.controller;

import com.wechat.checkin.activity.dto.ActivityQueryRequest;
import com.wechat.checkin.activity.dto.CreateActivityRequest;
import com.wechat.checkin.activity.service.ActivityService;
import com.wechat.checkin.activity.vo.ActivityDetailVO;
import com.wechat.checkin.activity.vo.ActivityVO;
import com.wechat.checkin.auth.annotation.RequireRole;
import com.wechat.checkin.auth.security.UserPrincipal;
import com.wechat.checkin.common.response.PageResult;
import com.wechat.checkin.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 活动管理控制器
 */
@Tag(name = "活动管理", description = "活动的创建、查询、结束等操作")
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @Operation(summary = "创建活动", description = "创建新的打卡活动，并自动生成打卡二维码")
    @PostMapping
    @RequireRole({"city", "county"})
    public Result<Long> createActivity(
            @Valid @RequestBody CreateActivityRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {

        Long activityId = activityService.createActivity(
                request,
                principal.getId(),
                principal.getRole(),
                principal.getCountyCode()
        );

        return Result.success(activityId);
    }

    @Operation(summary = "查询活动列表", description = "分页查询活动列表，支持按状态、县域过滤")
    @GetMapping
    @RequireRole({"city", "county"})
    public Result<PageResult<ActivityVO>> listActivities(
            @ModelAttribute ActivityQueryRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {

        PageResult<ActivityVO> result = activityService.listActivities(
                request,
                principal.getId(),
                principal.getRole(),
                principal.getCountyCode()
        );

        return Result.success(result);
    }

    @Operation(summary = "查询活动详情", description = "查询活动详细信息及参与统计数据")
    @GetMapping("/{id}")
    @RequireRole({"city", "county"})
    public Result<ActivityDetailVO> getActivityDetail(
            @Parameter(description = "活动ID") @PathVariable("id") Long activityId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {

        ActivityDetailVO detail = activityService.getActivityDetail(
                activityId,
                principal.getRole(),
                principal.getCountyCode()
        );

        return Result.success(detail);
    }

    @Operation(summary = "结束活动", description = "手动结束活动，关联的打卡二维码将失效")
    @PostMapping("/{id}/finish")
    @RequireRole({"city", "county"})
    public Result<Void> finishActivity(
            @Parameter(description = "活动ID") @PathVariable("id") Long activityId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {

        activityService.finishActivity(
                activityId,
                principal.getId(),
                principal.getRole(),
                principal.getCountyCode()
        );

        return Result.success();
    }
}
