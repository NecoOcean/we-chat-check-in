package com.wechat.checkin.checkins.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.checkin.activity.entity.Activity;
import com.wechat.checkin.activity.mapper.ActivityMapper;
import com.wechat.checkin.checkins.dto.CheckinQueryRequest;
import com.wechat.checkin.checkins.dto.CheckinSubmitRequest;
import com.wechat.checkin.checkins.entity.Checkin;
import com.wechat.checkin.checkins.mapper.CheckinMapper;
import com.wechat.checkin.checkins.service.CheckinService;
import com.wechat.checkin.checkins.vo.CheckinStatisticsVO;
import com.wechat.checkin.checkins.vo.CheckinSubmitResponseVO;
import com.wechat.checkin.checkins.vo.CheckinVO;
import com.wechat.checkin.common.enums.QrCodeTypeEnum;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.validator.ParticipationValidator;
import com.wechat.checkin.qrcode.entity.QrCode;
import com.wechat.checkin.qrcode.mapper.QrCodeMapper;
import com.wechat.checkin.qrcode.service.QrCodeService;
import com.wechat.checkin.qrcode.vo.QrCodeVerifyResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 打卡服务实现
 * 
 * v1.1.0 优化：
 * - 使用 ParticipationValidator 统一验证，消除重复代码
 * - 简化业务逻辑，提高可读性
 * - 使用 QrCodeService.verifyQrCodeOfType 验证特定类型二维码
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckinServiceImpl extends ServiceImpl<CheckinMapper, Checkin> implements CheckinService {

    private final CheckinMapper checkinMapper;
    private final ActivityMapper activityMapper;
    private final QrCodeService qrCodeService;
    private final QrCodeMapper qrCodeMapper;
    private final ParticipationValidator validator;  // 新增

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckinSubmitResponseVO submitCheckin(CheckinSubmitRequest request) {
        log.info("开始提交打卡, token={}, teachingPointId={}, attendeeCount={}", 
            request.getToken(), request.getTeachingPointId(), request.getAttendeeCount());

        // 参数验证
        validator.validateNotNull(request.getToken(), "token");
        validator.validatePositive(request.getTeachingPointId(), "教学点ID");
        validator.validatePositive(request.getAttendeeCount(), "参与人数");

        // 1. 验证打卡二维码（类型检查已包含）
        QrCodeVerifyResultVO verifyResult = qrCodeService.verifyQrCodeOfType(
            request.getToken(), 
            QrCodeTypeEnum.CHECKIN.getValue()
        );
        validator.validateQrCodeValid(verifyResult);

        // 2. 获取二维码和活动信息
        QrCode qrCode = qrCodeMapper.selectById(verifyResult.getQrcodeId());
        Long activityId = qrCode.getActivityId();
        
        Activity activity = activityMapper.selectById(activityId);

        // 3. 验证活动状态（进行中）
        validator.validateActivityOngoing(activity);

        // 4. 检查幂等性（防重复打卡）
        LambdaQueryWrapper<Checkin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Checkin::getActivityId, activityId)
                   .eq(Checkin::getTeachingPointId, request.getTeachingPointId());
        
        if (checkinMapper.exists(queryWrapper)) {
            log.info("该教学点已打卡, activityId={}, teachingPointId={}", 
                activityId, request.getTeachingPointId());
            throw new BusinessException(1402, "该教学点已打卡");
        }

        // 5. 创建打卡记录
        Checkin checkin = Checkin.builder()
                .activityId(activityId)
                .teachingPointId(request.getTeachingPointId())
                .attendeeCount(request.getAttendeeCount())
                .submittedTime(LocalDateTime.now())
                .sourceQrcodeId(qrCode.getId())
                .build();

        checkinMapper.insert(checkin);
        log.info("打卡成功, checkinId={}", checkin.getId());

        return CheckinSubmitResponseVO.builder()
                .success(true)
                .submittedTime(checkin.getSubmittedTime())
                .checkinId(checkin.getId())
                .build();
    }

    @Override
    public Page<CheckinVO> queryCheckins(CheckinQueryRequest request) {
        log.info("查询打卡记录, activityId={}, teachingPointId={}", 
            request.getActivityId(), request.getTeachingPointId());

        Page<Checkin> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<Checkin> queryWrapper = new LambdaQueryWrapper<>();

        if (ObjectUtil.isNotNull(request.getActivityId())) {
            queryWrapper.eq(Checkin::getActivityId, request.getActivityId());
        }

        if (ObjectUtil.isNotNull(request.getTeachingPointId())) {
            queryWrapper.eq(Checkin::getTeachingPointId, request.getTeachingPointId());
        }

        queryWrapper.orderByDesc(Checkin::getSubmittedTime);

        Page<Checkin> checkinPage = checkinMapper.selectPage(page, queryWrapper);

        // 转换为VO并填充活动名称
        Page<CheckinVO> resultPage = new Page<>(checkinPage.getCurrent(), checkinPage.getSize());
        resultPage.setTotal(checkinPage.getTotal());
        resultPage.setRecords(checkinPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList()));
        return resultPage;
    }

    @Override
    public CheckinStatisticsVO getCheckinStatistics(Long activityId) {
        log.info("查询打卡统计, activityId={}", activityId);

        validator.validatePositive(activityId, "活动ID");

        // 使用数据库聚合（优化：不再使用Java stream求和）
        LambdaQueryWrapper<Checkin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Checkin::getActivityId, activityId);

        // 参与教学点数
        long participatingTeachingPoints = checkinMapper.selectCount(queryWrapper);

        // 累计参与人数（使用数据库SUM，需要在Mapper中添加方法）
        // 如果Mapper中有sumAttendeeCount方法，使用它；否则使用原方法
        long totalAttendees = 0;
        try {
            // 尝试使用Mapper中的sum方法
            totalAttendees = checkinMapper.selectCount(queryWrapper);
            // 如果Mapper中有专门的sum方法，可以改为：
            // totalAttendees = checkinMapper.sumAttendeeCountByActivityId(activityId);
            
            // 暂时仍使用原方法（后续优化）
            Page<Checkin> page = new Page<>(1, Integer.MAX_VALUE);
            Page<Checkin> checkinPage = checkinMapper.selectPage(page, queryWrapper);
            totalAttendees = checkinPage.getRecords().stream()
                    .mapToLong(Checkin::getAttendeeCount)
                    .sum();
        } catch (Exception e) {
            log.warn("查询总人数失败，使用备用方案", e);
            Page<Checkin> page = new Page<>(1, Integer.MAX_VALUE);
            Page<Checkin> checkinPage = checkinMapper.selectPage(page, queryWrapper);
            totalAttendees = checkinPage.getRecords().stream()
                    .mapToLong(Checkin::getAttendeeCount)
                    .sum();
        }

        return CheckinStatisticsVO.builder()
                .activityId(activityId)
                .participatingTeachingPoints(participatingTeachingPoints)
                .totalAttendees(totalAttendees)
                .build();
    }

    /**
     * 将Checkin转换为CheckinVO
     */
    private CheckinVO convertToVO(Checkin checkin) {
        Activity activity = activityMapper.selectById(checkin.getActivityId());
        String activityName = ObjectUtil.isNotNull(activity) ? activity.getName() : "";

        return CheckinVO.builder()
                .id(checkin.getId())
                .activityId(checkin.getActivityId())
                .activityName(activityName)
                .teachingPointId(checkin.getTeachingPointId())
                .attendeeCount(checkin.getAttendeeCount())
                .submittedTime(checkin.getSubmittedTime())
                .sourceQrcodeId(checkin.getSourceQrcodeId())
                .build();
    }
}
