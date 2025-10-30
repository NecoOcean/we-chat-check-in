package com.wechat.checkin.qrcode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.checkin.auth.annotation.RequireRole;
import com.wechat.checkin.common.response.Result;
import com.wechat.checkin.qrcode.dto.GenerateQrCodeRequest;
import com.wechat.checkin.qrcode.dto.QrCodeQueryRequest;
import com.wechat.checkin.qrcode.service.QrCodeService;
import com.wechat.checkin.qrcode.vo.QrCodeVO;
import com.wechat.checkin.qrcode.vo.QrCodeVerifyResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 二维码管理控制器
 * 提供二维码生成、查询、禁用和验证等接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@Tag(name = "二维码管理", description = "二维码生成、查询、禁用和验证等接口")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    /**
     * 为活动生成二维码
     *
     * @param activityId 活动ID
     * @param request 生成请求
     * @return 二维码信息
     */
    @PostMapping("/activities/{id}/qrcodes")
    @RequireRole({"city", "county"})
    @Operation(summary = "为活动生成二维码", description = "市级或县级管理员为活动生成打卡或评价二维码")
    public Result<QrCodeVO> generateQrCode(
            @Parameter(description = "活动ID", required = true)
            @PathVariable("id") Long activityId,
            @Validated @RequestBody GenerateQrCodeRequest request) {
        
        log.info("为活动生成二维码: activityId={}, type={}", activityId, request.getType());
        QrCodeVO qrCode = qrCodeService.generateQrCode(activityId, request);
        
        return Result.success("二维码生成成功", qrCode);
    }

    /**
     * 分页查询二维码列表
     *
     * @param request 查询请求
     * @return 二维码列表
     */
    @GetMapping("/qrcodes")
    @RequireRole({"city", "county"})
    @Operation(summary = "查询二维码列表", description = "分页查询二维码列表，支持按活动、类型、状态过滤")
    public Result<Page<QrCodeVO>> listQrCodes(@Validated QrCodeQueryRequest request) {
        log.info("查询二维码列表: activityId={}, type={}, status={}, current={}, size={}", 
                request.getActivityId(), request.getType(), request.getStatus(), 
                request.getCurrent(), request.getSize());
        
        Page<QrCodeVO> page = qrCodeService.listQrCodes(request);
        
        return Result.success(page);
    }

    /**
     * 查询二维码详情
     *
     * @param id 二维码ID
     * @return 二维码详情
     */
    @GetMapping("/qrcodes/{id}")
    @RequireRole({"city", "county"})
    @Operation(summary = "查询二维码详情", description = "根据ID查询二维码详细信息")
    public Result<QrCodeVO> getQrCodeById(
            @Parameter(description = "二维码ID", required = true)
            @PathVariable("id") Long id) {
        
        log.info("查询二维码详情: id={}", id);
        QrCodeVO qrCode = qrCodeService.getQrCodeById(id);
        
        return Result.success(qrCode);
    }

    /**
     * 禁用二维码
     *
     * @param id 二维码ID
     * @return 操作结果
     */
    @PatchMapping("/qrcodes/{id}/disable")
    @RequireRole({"city", "county"})
    @Operation(summary = "禁用二维码", description = "禁用指定的二维码，禁用后该二维码无法使用")
    public Result<Void> disableQrCode(
            @Parameter(description = "二维码ID", required = true)
            @PathVariable("id") Long id) {
        
        log.info("禁用二维码: id={}", id);
        qrCodeService.disableQrCode(id);
        
        return Result.success("二维码已禁用");
    }

    /**
     * 验证二维码（参与端使用，无需登录）
     *
     * @param token 二维码令牌
     * @return 验证结果
     */
    @GetMapping("/qrcodes/verify")
    @Operation(summary = "验证二维码", description = "参与端验证二维码是否有效（无需登录）")
    public Result<QrCodeVerifyResultVO> verifyQrCode(
            @Parameter(description = "二维码令牌", required = true)
            @NotBlank(message = "二维码令牌不能为空")
            @RequestParam("token") String token) {
        
        log.info("验证二维码");
        QrCodeVerifyResultVO result = qrCodeService.verifyQrCode(token);
        
        if (result.getValid()) {
            return Result.success("二维码验证成功", result);
        } else {
            Result<QrCodeVerifyResultVO> errorResult = Result.error(400, result.getReason());
            errorResult.setData(result);
            return errorResult;
        }
    }

    /**
     * 根据令牌获取二维码信息（参与端使用）
     *
     * @param token 二维码令牌
     * @return 二维码信息
     */
    @GetMapping("/qrcodes/info")
    @Operation(summary = "获取二维码信息", description = "根据令牌获取二维码详细信息（无需登录）")
    public Result<QrCodeVO> getQrCodeByToken(
            @Parameter(description = "二维码令牌", required = true)
            @NotBlank(message = "二维码令牌不能为空")
            @RequestParam("token") String token) {
        
        log.info("根据令牌获取二维码信息");
        QrCodeVO qrCode = qrCodeService.getQrCodeByToken(token);
        
        return Result.success(qrCode);
    }
}

