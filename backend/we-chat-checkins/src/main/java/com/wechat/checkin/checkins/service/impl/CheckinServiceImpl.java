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
import com.wechat.checkin.common.enums.ActivityStatusEnum;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.enums.QrCodeTypeEnum;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckinSubmitResponseVO submitCheckin(CheckinSubmitRequest request) {
        log.info("开始提交打卡, token={}, teachingPointId={}, attendeeCount={}", 
            request.getToken(), request.getTeachingPointId(), request.getAttendeeCount());

        // 1. 验证二维码
        QrCodeVerifyResultVO verifyResult = qrCodeService.verifyQrCode(request.getToken());
        if (!verifyResult.getValid()) {
            log.warn("二维码验证失败: {}", verifyResult.getReason());
            throw new BusinessException(1501, "二维码无效");
        }

        // 2. 获取二维码信息，确保是打卡二维码
        QrCode qrCode = qrCodeMapper.selectById(verifyResult.getQrcodeId());
        if (ObjectUtil.isNull(qrCode) || !QrCodeTypeEnum.CHECKIN.equals(qrCode.getType())) {
            log.warn("二维码类型错误");
            throw new BusinessException(1501, "二维码无效");
        }

        Long activityId = qrCode.getActivityId();

        // 3. 验证活动状态
        Activity activity = activityMapper.selectById(activityId);
        if (ObjectUtil.isNull(activity)) {
            log.warn("活动不存在, activityId={}", activityId);
            throw new BusinessException(1301, "活动不存在");
        }

        if (!ActivityStatusEnum.ONGOING.equals(activity.getStatus())) {
            log.warn("活动状态错误, status={}", activity.getStatus());
            throw new BusinessException(1303, "活动已结束");
        }

        if (LocalDateTime.now().isBefore(activity.getStartTime())) {
            log.warn("活动未开始");
            throw new BusinessException(1302, "活动未开始");
        }

        if (LocalDateTime.now().isAfter(activity.getEndTime())) {
            log.warn("活动已结束");
            throw new BusinessException(1303, "活动已结束");
        }

        // 4. 检查是否已打卡（幂等性）
        LambdaQueryWrapper<Checkin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Checkin::getActivityId, activityId)
                   .eq(Checkin::getTeachingPointId, request.getTeachingPointId());
        Checkin existingCheckin = checkinMapper.selectOne(queryWrapper);

        if (ObjectUtil.isNotNull(existingCheckin)) {
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

        // 转换为VO并填充活动名称和教学点名称
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

        // 查询参与教学点数和累计人数
        LambdaQueryWrapper<Checkin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Checkin::getActivityId, activityId);

        // 参与教学点数 = 不同的teaching_point_id数量
        long participatingTeachingPoints = checkinMapper.selectCount(queryWrapper);

        // 累计参与人数 = 所有attendee_count的总和
        Page<Checkin> page = new Page<>(1, Integer.MAX_VALUE);
        Page<Checkin> checkinPage = checkinMapper.selectPage(page, queryWrapper);

        long totalAttendees = checkinPage.getRecords().stream()
                .mapToLong(Checkin::getAttendeeCount)
                .sum();

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
