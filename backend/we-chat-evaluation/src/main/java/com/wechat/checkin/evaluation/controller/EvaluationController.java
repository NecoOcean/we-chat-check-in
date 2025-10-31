package com.wechat.checkin.evaluation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.checkin.auth.annotation.RequireDataPermission;
import com.wechat.checkin.auth.annotation.RequireRole;
import com.wechat.checkin.auth.security.JwtTokenProvider;
import com.wechat.checkin.common.response.Result;
import com.wechat.checkin.evaluation.dto.EvaluationQueryRequest;
import com.wechat.checkin.evaluation.dto.EvaluationSubmitRequest;
import com.wechat.checkin.evaluation.service.EvaluationService;
import com.wechat.checkin.evaluation.vo.EvaluationStatisticsVO;
import com.wechat.checkin.evaluation.vo.EvaluationSubmitResponseVO;
import com.wechat.checkin.evaluation.vo.EvaluationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 评价控制器
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@Tag(name = "评价管理", description = "评价提交与查询接口")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 提交评价（参与端接口，无需登录）
     * 
     * @param request 评价提交请求
     * @return 评价结果
     */
    @PostMapping("/evaluation")
    @Operation(
        summary = "提交评价",
        description = "教学点扫描评价二维码后提交评价内容，包括满意度、实用性、质量评分和建议。" +
                     "二维码必须是评价类型且未过期，活动必须已结束，教学点必须已参与该活动，同一活动同一教学点只能评价一次。"
    )
    @ApiResponse(
        responseCode = "200",
        description = "评价提交成功",
        content = @Content(schema = @Schema(implementation = Result.class))
    )
    public Result<EvaluationSubmitResponseVO> submitEvaluation(@Valid @RequestBody EvaluationSubmitRequest request) {
        log.info("接收评价提交请求: {}", request);
        EvaluationSubmitResponseVO result = evaluationService.submitEvaluation(request);
        return Result.success("评价提交成功", result);
    }

    /**
     * 查询评价列表（管理端接口，需要登录）
     * 
     * @param request 查询请求
     * @return 评价列表
     */
    @GetMapping("")
    @RequireRole({"city", "county"})
    @RequireDataPermission
    @Operation(
        summary = "查询评价列表",
        description = "分页查询评价记录，支持按活动、教学点过滤。" +
                     "市级管理员可查看全部，县级管理员仅可查看本县数据。"
    )
    @ApiResponse(
        responseCode = "200",
        description = "查询成功",
        content = @Content(schema = @Schema(implementation = Result.class))
    )
    public Result<Page<EvaluationVO>> queryEvaluationList(
            @Valid EvaluationQueryRequest request,
            @RequestHeader("Authorization") String token) {
        log.info("接收评价列表查询请求: {}", request);
        
        // 从Token中获取县域编码
        String countyCode = jwtTokenProvider.getCountyCodeFromToken(token.replace("Bearer ", ""));
        
        Page<EvaluationVO> result = evaluationService.queryEvaluationList(request, countyCode);
        return Result.success("查询成功", result);
    }

    /**
     * 查询评价统计（管理端接口，需要登录）
     * 
     * @param activityId 活动ID
     * @return 评价统计信息
     */
    @GetMapping("/statistics")
    @RequireRole({"city", "county"})
    @RequireDataPermission
    @Operation(
        summary = "查询评价统计",
        description = "查询某个活动的评价汇总统计信息，包括平均分、等级分布、包含建议的数量等。" +
                     "市级管理员可查看全部，县级管理员仅可查看本县活动的统计。"
    )
    @ApiResponse(
        responseCode = "200",
        description = "查询成功",
        content = @Content(schema = @Schema(implementation = Result.class))
    )
    public Result<EvaluationStatisticsVO> queryEvaluationStatistics(
            @RequestParam(name = "activityId") Long activityId) {
        log.info("接收评价统计查询请求: activityId={}", activityId);
        
        EvaluationStatisticsVO result = evaluationService.queryEvaluationStatistics(activityId);
        return Result.success("查询成功", result);
    }
}
