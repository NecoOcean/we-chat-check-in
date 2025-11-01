package com.wechat.checkin.teachingpoint.controller;

import com.wechat.checkin.auth.annotation.RequireRole;
import com.wechat.checkin.common.response.PageResult;
import com.wechat.checkin.common.response.Result;
import com.wechat.checkin.teachingpoint.dto.CreateTeachingPointRequest;
import com.wechat.checkin.teachingpoint.dto.TeachingPointQueryRequest;
import com.wechat.checkin.teachingpoint.dto.UpdateTeachingPointRequest;
import com.wechat.checkin.teachingpoint.service.TeachingPointService;
import com.wechat.checkin.teachingpoint.vo.TeachingPointVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 教学点管理控制器
 * 提供教学点的增删改查等操作接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/teaching-points")
@RequiredArgsConstructor
@Tag(name = "教学点管理", description = "教学点的创建、查询、更新、删除等操作")
public class TeachingPointController {

    private final TeachingPointService teachingPointService;

    /**
     * 创建教学点
     * 市级和县级管理员均可创建教学点
     *
     * @param request 创建请求
     * @return 新创建的教学点ID
     */
    @PostMapping
    @Operation(summary = "创建教学点", description = "市级或县级管理员创建教学点")
    @RequireRole({"city", "county"})
    public Result<Long> createTeachingPoint(@Valid @RequestBody CreateTeachingPointRequest request) {
        log.info("创建教学点: name={}, countyCode={}", request.getName(), request.getCountyCode());
        
        Long teachingPointId = teachingPointService.createTeachingPoint(request);
        return Result.success(teachingPointId);
    }

    /**
     * 更新教学点信息
     * 市级管理员可以更新所有教学点，县级管理员只能更新本县教学点
     *
     * @param id 教学点ID
     * @param request 更新请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新教学点", description = "更新教学点的名称或县域信息")
    @RequireRole({"city", "county"})
    public Result<Void> updateTeachingPoint(
            @Parameter(description = "教学点ID", required = true) 
            @PathVariable Long id,
            @Valid @RequestBody UpdateTeachingPointRequest request) {
        log.info("更新教学点: id={}, name={}, countyCode={}", 
                id, request.getName(), request.getCountyCode());
        
        teachingPointService.updateTeachingPoint(id, request);
        return Result.success();
    }

    /**
     * 删除教学点（软删除）
     * 市级管理员可以删除所有教学点，县级管理员只能删除本县教学点
     *
     * @param id 教学点ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除教学点", description = "软删除教学点，数据永久保留仅隐藏")
    @RequireRole({"city", "county"})
    public Result<Void> deleteTeachingPoint(
            @Parameter(description = "教学点ID", required = true) 
            @PathVariable Long id) {
        log.info("删除教学点: id={}", id);
        
        teachingPointService.deleteTeachingPoint(id);
        return Result.success();
    }

    /**
     * 启用教学点
     * 市级管理员可以启用所有教学点，县级管理员只能启用本县教学点
     *
     * @param id 教学点ID
     * @return 操作结果
     */
    @PutMapping("/{id}/enable")
    @Operation(summary = "启用教学点", description = "将教学点状态设置为启用")
    @RequireRole({"city", "county"})
    public Result<Void> enableTeachingPoint(
            @Parameter(description = "教学点ID", required = true) 
            @PathVariable Long id) {
        log.info("启用教学点: id={}", id);
        
        teachingPointService.enableTeachingPoint(id);
        return Result.success();
    }

    /**
     * 禁用教学点
     * 市级管理员可以禁用所有教学点，县级管理员只能禁用本县教学点
     *
     * @param id 教学点ID
     * @return 操作结果
     */
    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用教学点", description = "将教学点状态设置为禁用")
    @RequireRole({"city", "county"})
    public Result<Void> disableTeachingPoint(
            @Parameter(description = "教学点ID", required = true) 
            @PathVariable Long id) {
        log.info("禁用教学点: id={}", id);
        
        teachingPointService.disableTeachingPoint(id);
        return Result.success();
    }

    /**
     * 查询教学点详情
     * 市级管理员可以查看所有教学点，县级管理员只能查看本县教学点
     *
     * @param id 教学点ID
     * @return 教学点详细信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询教学点详情", description = "根据ID查询教学点的详细信息")
    @RequireRole({"city", "county"})
    public Result<TeachingPointVO> getTeachingPointDetail(
            @Parameter(description = "教学点ID", required = true) 
            @PathVariable Long id) {
        log.info("查询教学点详情: id={}", id);
        
        TeachingPointVO teachingPoint = teachingPointService.getTeachingPointDetail(id);
        return Result.success(teachingPoint);
    }

    /**
     * 分页查询教学点列表
     * 市级管理员可以查看所有教学点，县级管理员只能查看本县教学点
     * 支持按名称、县域、状态过滤
     *
     * @param request 查询请求参数
     * @return 教学点分页列表
     */
    @GetMapping
    @Operation(summary = "查询教学点列表", 
            description = "分页查询教学点列表，支持按名称、县域、状态过滤")
    @RequireRole({"city", "county"})
    public Result<PageResult<TeachingPointVO>> listTeachingPoints(@ModelAttribute TeachingPointQueryRequest request) {
        log.info("查询教学点列表: countyCode={}, status={}, name={}", 
                request.getCountyCode(), request.getStatus(), request.getName());
        
        PageResult<TeachingPointVO> result = teachingPointService.listTeachingPoints(request);
        
        return Result.success(result);
    }
}

