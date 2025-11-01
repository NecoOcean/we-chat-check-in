package com.wechat.checkin.admin.controller;

import com.wechat.checkin.admin.dto.AdminQueryRequest;
import com.wechat.checkin.admin.dto.CreateAdminRequest;
import com.wechat.checkin.admin.dto.UpdateAdminPasswordRequest;
import com.wechat.checkin.admin.dto.UpdateAdminRequest;
import com.wechat.checkin.admin.service.AdminService;
import com.wechat.checkin.admin.vo.AdminVO;
import com.wechat.checkin.auth.annotation.RequireRole;
import com.wechat.checkin.common.response.PageResult;
import com.wechat.checkin.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员管理控制器
 * 提供管理员账号的增删改查等操作接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@Tag(name = "管理员管理", description = "管理员账号的创建、查询、更新、删除等操作")
public class AdminController {

    private final AdminService adminService;

    /**
     * 创建管理员账号
     * 只有市级管理员可以创建县级管理员账号
     *
     * @param request 创建请求
     * @return 新创建的管理员ID
     */
    @PostMapping
    @Operation(summary = "创建管理员账号", description = "市级管理员创建县级管理员账号")
    @RequireRole("city")
    public Result<Long> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        log.info("创建管理员: username={}, role={}, countyCode={}", 
                request.getUsername(), request.getRole(), request.getCountyCode());
        
        Long adminId = adminService.createAdmin(request);
        
        return Result.success(adminId);
    }

    /**
     * 更新管理员信息
     *
     * @param adminId 管理员ID
     * @param request 更新请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新管理员信息", description = "更新管理员的用户名或县域代码")
    @RequireRole("city")
    public Result<Void> updateAdmin(
            @Parameter(description = "管理员ID", required = true) @PathVariable("id") Long adminId,
            @Valid @RequestBody UpdateAdminRequest request) {
        log.info("更新管理员: adminId={}, username={}, countyCode={}", 
                adminId, request.getUsername(), request.getCountyCode());
        
        adminService.updateAdmin(adminId, request);
        
        return Result.success();
    }

    /**
     * 重置管理员密码
     *
     * @param adminId 管理员ID
     * @param request 密码更新请求
     * @return 操作结果
     */
    @PutMapping("/{id}/password")
    @Operation(summary = "重置管理员密码", description = "市级管理员重置县级管理员密码")
    @RequireRole("city")
    public Result<Void> updateAdminPassword(
            @Parameter(description = "管理员ID", required = true) @PathVariable("id") Long adminId,
            @Valid @RequestBody UpdateAdminPasswordRequest request) {
        log.info("重置管理员密码: adminId={}", adminId);
        
        adminService.updateAdminPassword(adminId, request);
        
        return Result.success();
    }

    /**
     * 启用管理员账号
     *
     * @param adminId 管理员ID
     * @return 操作结果
     */
    @PutMapping("/{id}/enable")
    @Operation(summary = "启用管理员账号", description = "启用被禁用的管理员账号")
    @RequireRole("city")
    public Result<Void> enableAdmin(
            @Parameter(description = "管理员ID", required = true) @PathVariable("id") Long adminId) {
        log.info("启用管理员: adminId={}", adminId);
        
        adminService.enableAdmin(adminId);
        
        return Result.success();
    }

    /**
     * 禁用管理员账号
     *
     * @param adminId 管理员ID
     * @return 操作结果
     */
    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用管理员账号", description = "禁用管理员账号，禁用后无法登录")
    @RequireRole("city")
    public Result<Void> disableAdmin(
            @Parameter(description = "管理员ID", required = true) @PathVariable("id") Long adminId) {
        log.info("禁用管理员: adminId={}", adminId);
        
        adminService.disableAdmin(adminId);
        
        return Result.success();
    }

    /**
     * 删除管理员账号
     * 软删除，数据保留但不可登录
     *
     * @param adminId 管理员ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除管理员账号", description = "软删除管理员账号，数据保留但不可登录")
    @RequireRole("city")
    public Result<Void> deleteAdmin(
            @Parameter(description = "管理员ID", required = true) @PathVariable("id") Long adminId) {
        log.info("删除管理员: adminId={}", adminId);
        
        adminService.deleteAdmin(adminId);
        
        return Result.success();
    }

    /**
     * 查询管理员详情
     *
     * @param adminId 管理员ID
     * @return 管理员信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询管理员详情", description = "根据ID查询管理员详细信息")
    @RequireRole("city")
    public Result<AdminVO> getAdminDetail(
            @Parameter(description = "管理员ID", required = true) @PathVariable("id") Long adminId) {
        log.info("查询管理员详情: adminId={}", adminId);
        
        AdminVO admin = adminService.getAdminDetail(adminId);
        
        return Result.success(admin);
    }

    /**
     * 分页查询管理员列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @GetMapping
    @Operation(summary = "分页查询管理员列表", description = "支持按用户名、角色、县域、状态等条件查询")
    @RequireRole("city")
    public Result<PageResult<AdminVO>> listAdmins(@ModelAttribute AdminQueryRequest request) {
        log.info("查询管理员列表: page={}, size={}, username={}, role={}, countyCode={}, status={}", 
                request.getCurrent(), request.getSize(), request.getUsername(), 
                request.getRole(), request.getCountyCode(), request.getStatus());
        
        PageResult<AdminVO> result = adminService.listAdmins(request);
        
        return Result.success(result);
    }
}

