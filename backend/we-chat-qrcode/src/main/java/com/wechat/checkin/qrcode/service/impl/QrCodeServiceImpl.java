package com.wechat.checkin.qrcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.checkin.common.enums.QrCodeStatusEnum;
import com.wechat.checkin.common.enums.QrCodeTypeEnum;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.ResultCode;
import com.wechat.checkin.qrcode.dto.GenerateQrCodeRequest;
import com.wechat.checkin.qrcode.dto.QrCodeQueryRequest;
import com.wechat.checkin.qrcode.entity.QrCode;
import com.wechat.checkin.qrcode.mapper.QrCodeMapper;
import com.wechat.checkin.qrcode.service.QrCodeService;
import com.wechat.checkin.qrcode.util.QrCodeGenerator;
import com.wechat.checkin.qrcode.util.QrCodeTokenProvider;
import com.wechat.checkin.qrcode.vo.QrCodeVO;
import com.wechat.checkin.qrcode.vo.QrCodeVerifyResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 二维码服务实现类
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {

    private final QrCodeMapper qrCodeMapper;
    private final QrCodeTokenProvider tokenProvider;

    @Value("${qrcode.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${qrcode.default-expiration-days:7}")
    private int defaultExpirationDays;

    @Override
    @Transactional
    public QrCodeVO generateQrCode(Long activityId, GenerateQrCodeRequest request) {
        log.info("为活动生成二维码: activityId={}, type={}", activityId, request.getType());

        // 1. 验证二维码类型
        QrCodeTypeEnum typeEnum;
        try {
            typeEnum = QrCodeTypeEnum.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "无效的二维码类型: " + request.getType());
        }

        // 2. 计算过期时间（如果未指定，则默认7天）
        LocalDateTime expireTime = request.getExpireTime();
        if (expireTime == null) {
            expireTime = LocalDateTime.now().plusDays(defaultExpirationDays);
        }

        // 3. 生成临时token（使用activityId作为临时ID，稍后会重新生成）
        Date expireDate = Date.from(expireTime.atZone(ZoneId.systemDefault()).toInstant());
        String tempToken = tokenProvider.generateToken(
                0L,  // 临时ID，稍后会更新
                activityId,
                typeEnum.getValue(),
                expireDate
        );

        // 4. 创建二维码记录（包含token）
        QrCode qrCode = new QrCode();
        qrCode.setActivityId(activityId);
        qrCode.setType(typeEnum);
        qrCode.setToken(tempToken);  // 先设置临时token
        qrCode.setExpireTime(expireTime);
        qrCode.setStatus(QrCodeStatusEnum.ENABLED);
        
        // 插入数据库获取ID
        qrCodeMapper.insert(qrCode);

        // 5. 使用真实的ID重新生成token
        String finalToken = tokenProvider.generateToken(
                qrCode.getId(),
                activityId,
                typeEnum.getValue(),
                expireDate
        );

        // 6. 更新为最终的token
        LambdaUpdateWrapper<QrCode> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QrCode::getId, qrCode.getId())
                .set(QrCode::getToken, finalToken);
        qrCodeMapper.update(null, updateWrapper);
        
        // 更新对象中的token值（用于后续返回）
        qrCode.setToken(finalToken);

        // 7. 生成二维码URL
        String qrCodeUrl = baseUrl + "/api/qrcodes/verify?token=" + finalToken;

        // 8. 生成二维码图片（Base64）
        String qrCodeImage = QrCodeGenerator.generateQrCodeBase64(qrCodeUrl);

        log.info("二维码生成成功: id={}, activityId={}, type={}", qrCode.getId(), activityId, typeEnum.getValue());

        // 9. 构建响应
        return QrCodeVO.builder()
                .id(qrCode.getId())
                .activityId(activityId)
                .type(typeEnum.getValue())
                .token(finalToken)
                .url(qrCodeUrl)
                .qrCodeImage(qrCodeImage)
                .expireTime(expireTime)
                .status(qrCode.getStatus().getValue())
                .createdTime(qrCode.getCreatedTime())
                .updatedTime(qrCode.getUpdatedTime())
                .build();
    }

    @Override
    public Page<QrCodeVO> listQrCodes(QrCodeQueryRequest request) {
        log.info("分页查询二维码列表: activityId={}, type={}, status={}, current={}, size={}", 
                request.getActivityId(), request.getType(), request.getStatus(), 
                request.getCurrent(), request.getSize());

        // 1. 构建查询条件
        LambdaQueryWrapper<QrCode> queryWrapper = new LambdaQueryWrapper<>();
        
        if (request.getActivityId() != null) {
            queryWrapper.eq(QrCode::getActivityId, request.getActivityId());
        }
        
        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            try {
                QrCodeTypeEnum typeEnum = QrCodeTypeEnum.valueOf(request.getType().toUpperCase());
                queryWrapper.eq(QrCode::getType, typeEnum);
            } catch (IllegalArgumentException e) {
                log.warn("无效的二维码类型: {}", request.getType());
            }
        }
        
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            try {
                QrCodeStatusEnum statusEnum = QrCodeStatusEnum.valueOf(request.getStatus().toUpperCase());
                queryWrapper.eq(QrCode::getStatus, statusEnum);
            } catch (IllegalArgumentException e) {
                log.warn("无效的二维码状态: {}", request.getStatus());
            }
        }

        queryWrapper.orderByDesc(QrCode::getCreatedTime);

        // 2. 执行分页查询
        Page<QrCode> page = new Page<>(request.getCurrent(), request.getSize());
        Page<QrCode> qrCodePage = qrCodeMapper.selectPage(page, queryWrapper);

        // 3. 转换为VO
        Page<QrCodeVO> voPage = new Page<>(qrCodePage.getCurrent(), qrCodePage.getSize(), qrCodePage.getTotal());
        voPage.setRecords(qrCodePage.getRecords().stream().map(this::convertToVO).toList());

        return voPage;
    }

    @Override
    public QrCodeVO getQrCodeById(Long id) {
        log.info("查询二维码详情: id={}", id);

        QrCode qrCode = qrCodeMapper.selectById(id);
        if (qrCode == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "二维码不存在");
        }

        return convertToVO(qrCode);
    }

    @Override
    @Transactional
    public void disableQrCode(Long id) {
        log.info("禁用二维码: id={}", id);

        // 1. 查询二维码
        QrCode qrCode = qrCodeMapper.selectById(id);
        if (qrCode == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "二维码不存在");
        }

        // 2. 检查状态
        if (QrCodeStatusEnum.DISABLED.equals(qrCode.getStatus())) {
            log.warn("二维码已禁用: id={}", id);
            return;
        }

        // 3. 更新状态
        LambdaUpdateWrapper<QrCode> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QrCode::getId, id)
                .set(QrCode::getStatus, QrCodeStatusEnum.DISABLED)
                .set(QrCode::getDisabledTime, LocalDateTime.now());

        int updated = qrCodeMapper.update(null, updateWrapper);
        if (updated == 0) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "禁用二维码失败");
        }

        log.info("二维码禁用成功: id={}", id);
    }

    @Override
    public QrCodeVerifyResultVO verifyQrCode(String token) {
        log.info("验证二维码令牌");

        try {
            // 1. 验证令牌格式
            if (!tokenProvider.validateToken(token)) {
                return QrCodeVerifyResultVO.builder()
                        .valid(false)
                        .reason("二维码令牌无效")
                        .build();
            }

            // 2. 从令牌中提取信息
            Long qrcodeId = tokenProvider.getQrcodeIdFromToken(token);
            Long activityId = tokenProvider.getActivityIdFromToken(token);
            String type = tokenProvider.getTypeFromToken(token);

            // 3. 检查令牌是否过期
            if (tokenProvider.isTokenExpired(token)) {
                return QrCodeVerifyResultVO.builder()
                        .valid(false)
                        .qrcodeId(qrcodeId)
                        .activityId(activityId)
                        .type(type)
                        .reason("二维码已过期")
                        .build();
            }

            // 4. 查询二维码记录
            QrCode qrCode = qrCodeMapper.selectById(qrcodeId);
            if (qrCode == null) {
                return QrCodeVerifyResultVO.builder()
                        .valid(false)
                        .qrcodeId(qrcodeId)
                        .activityId(activityId)
                        .type(type)
                        .reason("二维码不存在")
                        .build();
            }

            // 5. 检查二维码状态
            if (!QrCodeStatusEnum.ENABLED.equals(qrCode.getStatus())) {
                return QrCodeVerifyResultVO.builder()
                        .valid(false)
                        .qrcodeId(qrcodeId)
                        .activityId(activityId)
                        .type(type)
                        .reason("二维码已被禁用")
                        .build();
            }

            // 6. 检查数据库中的过期时间
            if (qrCode.getExpireTime() != null && LocalDateTime.now().isAfter(qrCode.getExpireTime())) {
                return QrCodeVerifyResultVO.builder()
                        .valid(false)
                        .qrcodeId(qrcodeId)
                        .activityId(activityId)
                        .type(type)
                        .reason("二维码已过期")
                        .build();
            }

            // 7. 验证通过
            log.info("二维码验证成功: qrcodeId={}, activityId={}, type={}", qrcodeId, activityId, type);
            return QrCodeVerifyResultVO.builder()
                    .valid(true)
                    .qrcodeId(qrcodeId)
                    .activityId(activityId)
                    .type(type)
                    .build();

        } catch (BusinessException e) {
            log.warn("二维码验证失败: {}", e.getMessage());
            return QrCodeVerifyResultVO.builder()
                    .valid(false)
                    .reason(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("二维码验证异常", e);
            return QrCodeVerifyResultVO.builder()
                    .valid(false)
                    .reason("二维码验证失败: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public QrCodeVO getQrCodeByToken(String token) {
        log.info("根据令牌获取二维码信息");

        // 1. 从令牌中提取二维码ID
        Long qrcodeId = tokenProvider.getQrcodeIdFromToken(token);

        // 2. 查询二维码
        QrCode qrCode = qrCodeMapper.selectById(qrcodeId);
        if (qrCode == null) {
            throw new BusinessException(ResultCode.QRCODE_INVALID, "二维码不存在");
        }

        return convertToVO(qrCode);
    }

    /**
     * 转换为VO
     */
    private QrCodeVO convertToVO(QrCode qrCode) {
        String qrCodeUrl = baseUrl + "/api/qrcodes/verify?token=" + qrCode.getToken();

        return QrCodeVO.builder()
                .id(qrCode.getId())
                .activityId(qrCode.getActivityId())
                .type(qrCode.getType().getValue())
                .token(qrCode.getToken())
                .url(qrCodeUrl)
                .expireTime(qrCode.getExpireTime())
                .disabledTime(qrCode.getDisabledTime())
                .status(qrCode.getStatus().getValue())
                .createdTime(qrCode.getCreatedTime())
                .updatedTime(qrCode.getUpdatedTime())
                .build();
    }
}

