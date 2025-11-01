package com.wechat.checkin.county.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.checkin.auth.annotation.RequireRole;
import com.wechat.checkin.common.response.Result;
import com.wechat.checkin.county.dto.CountyCreateRequest;
import com.wechat.checkin.county.dto.CountyQueryRequest;
import com.wechat.checkin.county.dto.CountyUpdateRequest;
import com.wechat.checkin.county.service.CountyService;
import com.wechat.checkin.county.vo.CountyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 县域管理控制器
 * 提供县域的增删改查等操作接口（仅市级管理员可用）
 * 
 * @author WeChat Check-in System
 * @since 1.3.0
 */
@Slf4j
@RestController
@RequestMapping("/api/counties")
@RequiredArgsConstructor
@Tag(name = "县域管理", description = "县域的创建、查询、更新、删除和状态管理（仅市级管理员）")
public class CountyController {

    private final CountyService countyService;

    /**
     * 创建县域
     * 仅市级管理员可操作
     *
     * @param request 创建请求
     * @return 新创建的县域信息
     */
    @PostMapping
    @Operation(summary = "创建县域", description = "市级管理员创建新的县域")
    @RequireRole("city")
    public Result<CountyVO> createCounty(@Valid @RequestBody CountyCreateRequest request) {
        log.info("创建县域: code={}, name={}", request.getCode(), request.getName());
        
        CountyVO county = countyService.createCounty(request);
        return Result.success(county);
    }

    /**
     * 更新县域信息
     * 仅市级管理员可操作
     *
     * @param code 县域编码
     * @param request 更新请求
     * @return 更新后的县域信息
     */
    @PutMapping("/{code}")
    @Operation(summary = "更新县域", description = "更新县域的名称等信息")
    @RequireRole("city")
    public Result<CountyVO> updateCounty(
            @Parameter(description = "县域编码", required = true) 
            @PathVariable String code,
            @Valid @RequestBody CountyUpdateRequest request) {
        log.info("更新县域: code={}, name={}", code, request.getName());
        
        CountyVO county = countyService.updateCounty(code, request);
        return Result.success(county);
    }

    /**
     * 删除县域（软删除）
     * 仅市级管理员可操作
     *
     * @param code 县域编码
     * @return 操作结果
     */
    @DeleteMapping("/{code}")
    @Operation(summary = "删除县域", description = "软删除县域（数据保留，仅标记为已删除状态）")
    @RequireRole("city")
    public Result<Void> deleteCounty(
            @Parameter(description = "县域编码", required = true) 
            @PathVariable String code) {
        log.info("删除县域: code={}", code);
        
        countyService.deleteCounty(code);
        return Result.success();
    }

    /**
     * 启用县域
     * 仅市级管理员可操作
     *
     * @param code 县域编码
     * @return 更新后的县域信息
     */
    @PutMapping("/{code}/enable")
    @Operation(summary = "启用县域", description = "将县域状态设置为启用")
    @RequireRole("city")
    public Result<CountyVO> enableCounty(
            @Parameter(description = "县域编码", required = true) 
            @PathVariable String code) {
        log.info("启用县域: code={}", code);
        
        CountyVO county = countyService.enableCounty(code);
        return Result.success(county);
    }

    /**
     * 禁用县域
     * 仅市级管理员可操作
     *
     * @param code 县域编码
     * @return 更新后的县域信息
     */
    @PutMapping("/{code}/disable")
    @Operation(summary = "禁用县域", description = "将县域状态设置为禁用")
    @RequireRole("city")
    public Result<CountyVO> disableCounty(
            @Parameter(description = "县域编码", required = true) 
            @PathVariable String code) {
        log.info("禁用县域: code={}", code);
        
        CountyVO county = countyService.disableCounty(code);
        return Result.success(county);
    }

    /**
     * 查询县域详情
     *
     * @param code 县域编码
     * @return 县域详情
     */
    @GetMapping("/{code}")
    @Operation(summary = "查询县域详情", description = "根据县域编码查询详细信息")
    public Result<CountyVO> getCounty(
            @Parameter(description = "县域编码", required = true) 
            @PathVariable String code) {
        log.info("查询县域详情: code={}", code);
        
        CountyVO county = countyService.getCountyByCode(code);
        return Result.success(county);
    }

    /**
     * 分页查询县域列表
     *
     * @param request 查询请求（包含分页参数）
     * @return 分页结果
     */
    @GetMapping
    @Operation(summary = "查询县域列表", description = "分页查询县域列表，支持名称模糊查询和状态过滤")
    public Result<Page<CountyVO>> listCounties(CountyQueryRequest request) {
        log.info("查询县域列表: code={}, name={}, status={}, current={}, size={}", 
                request.getCode(), request.getName(), request.getStatus(),
                request.getCurrent(), request.getSize());
        
        Page<CountyVO> page = countyService.listCounties(request);
        return Result.success(page);
    }
}

