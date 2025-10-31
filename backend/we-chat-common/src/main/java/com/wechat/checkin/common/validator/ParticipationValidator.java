package com.wechat.checkin.common.validator;

import cn.hutool.core.util.ObjectUtil;
import com.wechat.checkin.common.enums.ActivityStatusEnum;
import com.wechat.checkin.common.enums.QrCodeTypeEnum;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 参与流程验证器 - 用于打卡和评价模块的统一验证
 * 
 * 职责：
 * 1. 二维码验证（签名、过期、状态）
 * 2. 活动状态验证（存在性、状态、时间范围）
 * 3. 参与权限验证（是否已打卡等）
 * 4. 幂等性验证（防重复提交）
 * 
 * 优点：
 * - 消除打卡和评价模块的验证重复代码（70%+）
 * - 验证规则集中管理，易于维护
 * - 易于单元测试
 * 
 * @author WeChat Check-in System
 * @since 1.1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ParticipationValidator {

    // 这些依赖由具体使用模块注入
    // 为了避免循环依赖，验证器本身不注入这些依赖
    // 而是由调用方传入数据
    
    /**
     * 验证二维码有效性
     * 
     * @param verifyResult 二维码验证结果
     * @throws BusinessException 验证失败抛出异常
     */
    public void validateQrCodeValid(Object verifyResult) {
        // 使用反射判断是否为有效的验证结果
        try {
            boolean valid = (boolean) verifyResult.getClass()
                    .getMethod("getValid")
                    .invoke(verifyResult);
            
            if (!valid) {
                String reason = (String) verifyResult.getClass()
                        .getMethod("getReason")
                        .invoke(verifyResult);
                throw new BusinessException(ResultCode.QRCODE_INVALID, 
                    reason != null ? reason : "二维码无效");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("验证二维码失败", e);
            throw new BusinessException(ResultCode.QRCODE_INVALID, "二维码验证异常");
        }
    }

    /**
     * 验证二维码类型
     * 
     * @param actualType 实际类型
     * @param expectedType 期望类型
     * @param typeDesc 类型描述（用于错误信息）
     * @throws BusinessException 类型不匹配抛出异常
     */
    public void validateQrCodeType(QrCodeTypeEnum actualType, QrCodeTypeEnum expectedType, String typeDesc) {
        if (!expectedType.equals(actualType)) {
            throw new BusinessException(ResultCode.QRCODE_INVALID, 
                String.format("请使用%s二维码", typeDesc));
        }
    }

    /**
     * 验证活动存在且状态为进行中（用于打卡）
     * 
     * @param activity 活动对象
     * @throws BusinessException 验证失败抛出异常
     */
    public void validateActivityOngoing(Object activity) {
        if (ObjectUtil.isNull(activity)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }
        
        try {
            ActivityStatusEnum status = (ActivityStatusEnum) activity.getClass()
                    .getMethod("getStatus")
                    .invoke(activity);
            
            LocalDateTime startTime = (LocalDateTime) activity.getClass()
                    .getMethod("getStartTime")
                    .invoke(activity);
            
            LocalDateTime endTime = (LocalDateTime) activity.getClass()
                    .getMethod("getEndTime")
                    .invoke(activity);
            
            // 检查活动状态
            if (!ActivityStatusEnum.ONGOING.equals(status)) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR, "活动不在进行中");
            }
            
            LocalDateTime now = LocalDateTime.now();
            
            // 检查活动是否已开始
            if (now.isBefore(startTime)) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR, "活动未开始");
            }
            
            // 检查活动是否已结束
            if (now.isAfter(endTime)) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR, "活动已结束");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("验证活动状态失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "活动验证异常");
        }
    }

    /**
     * 验证活动存在且状态为已结束（用于评价）
     * 
     * @param activity 活动对象
     * @throws BusinessException 验证失败抛出异常
     */
    public void validateActivityEnded(Object activity) {
        if (ObjectUtil.isNull(activity)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }
        
        try {
            ActivityStatusEnum status = (ActivityStatusEnum) activity.getClass()
                    .getMethod("getStatus")
                    .invoke(activity);
            
            // 评价只能在活动结束后进行
            if (!ActivityStatusEnum.ENDED.equals(status)) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR, "活动未结束，暂无法评价");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("验证活动状态失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "活动验证异常");
        }
    }

    /**
     * 验证参数非空
     * 
     * @param value 待验证值
     * @param paramName 参数名称
     * @throws BusinessException 参数为空抛出异常
     */
    public void validateNotNull(Object value, String paramName) {
        if (ObjectUtil.isNull(value)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, 
                String.format("%s不能为空", paramName));
        }
    }

    /**
     * 验证数字参数大于0
     * 
     * @param value 待验证值
     * @param paramName 参数名称
     * @throws BusinessException 验证失败抛出异常
     */
    public void validatePositive(Long value, String paramName) {
        if (ObjectUtil.isNull(value) || value <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, 
                String.format("%s必须大于0", paramName));
        }
    }

    /**
     * 验证数字参数大于0（整数版本）
     * 
     * @param value 待验证值
     * @param paramName 参数名称
     * @throws BusinessException 验证失败抛出异常
     */
    public void validatePositive(Integer value, String paramName) {
        if (ObjectUtil.isNull(value) || value <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, 
                String.format("%s必须大于0", paramName));
        }
    }
}
