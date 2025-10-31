package com.wechat.checkin.evaluation.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.checkin.activity.entity.Activity;
import com.wechat.checkin.activity.mapper.ActivityMapper;
import com.wechat.checkin.checkins.entity.Checkin;
import com.wechat.checkin.checkins.mapper.CheckinMapper;
import com.wechat.checkin.common.enums.QrCodeTypeEnum;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.ResultCode;
import com.wechat.checkin.common.validator.ParticipationValidator;
import com.wechat.checkin.evaluation.dto.EvaluationQueryRequest;
import com.wechat.checkin.evaluation.dto.EvaluationSubmitRequest;
import com.wechat.checkin.evaluation.entity.Evaluation;
import com.wechat.checkin.evaluation.mapper.EvaluationMapper;
import com.wechat.checkin.evaluation.service.EvaluationService;
import com.wechat.checkin.evaluation.vo.EvaluationStatisticsVO;
import com.wechat.checkin.evaluation.vo.EvaluationSubmitResponseVO;
import com.wechat.checkin.evaluation.vo.EvaluationVO;
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
 * 评价服务实现
 * 
 * v1.1.0 优化：
 * - 使用 ParticipationValidator 统一验证，消除与Checkin模块重复的代码
 * - 简化业务逻辑，提高可读性
 * - 使用 QrCodeService.verifyQrCodeOfType 验证特定类型二维码
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl extends ServiceImpl<EvaluationMapper, Evaluation> implements EvaluationService {

    private final EvaluationMapper evaluationMapper;
    private final ActivityMapper activityMapper;
    private final CheckinMapper checkinMapper;
    private final QrCodeService qrCodeService;
    private final QrCodeMapper qrCodeMapper;
    private final ParticipationValidator validator;  // 新增

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EvaluationSubmitResponseVO submitEvaluation(EvaluationSubmitRequest request) {
        log.info("开始提交评价, token={}, teachingPointId={}", request.getToken(), request.getTeachingPointId());

        // 参数验证
        validator.validateNotNull(request.getToken(), "token");
        validator.validatePositive(request.getTeachingPointId(), "教学点ID");

        // 1. 验证评价二维码（类型检查已包含）
        QrCodeVerifyResultVO verifyResult = qrCodeService.verifyQrCodeOfType(
            request.getToken(),
            QrCodeTypeEnum.EVALUATION.getValue()
        );
        validator.validateQrCodeValid(verifyResult);

        // 2. 获取二维码和活动信息
        QrCode qrCode = qrCodeMapper.selectById(verifyResult.getQrcodeId());
        Long activityId = qrCode.getActivityId();
        
        Activity activity = activityMapper.selectById(activityId);

        // 3. 验证活动状态（已结束）
        validator.validateActivityEnded(activity);

        // 4. 检查教学点是否已参与过该活动
        LambdaQueryWrapper<Checkin> checkinQuery = new LambdaQueryWrapper<>();
        checkinQuery.eq(Checkin::getActivityId, activityId)
                    .eq(Checkin::getTeachingPointId, request.getTeachingPointId());
        Checkin checkin = checkinMapper.selectOne(checkinQuery);

        if (ObjectUtil.isNull(checkin)) {
            log.warn("教学点未参与过该活动, activityId={}, teachingPointId={}", 
                activityId, request.getTeachingPointId());
            throw new BusinessException(1601, "教学点未参与过此活动，无法进行评价");
        }

        // 5. 检查是否已评价（防止重复评价）
        LambdaQueryWrapper<Evaluation> evaluationQuery = new LambdaQueryWrapper<>();
        evaluationQuery.eq(Evaluation::getActivityId, activityId)
                       .eq(Evaluation::getTeachingPointId, request.getTeachingPointId());
        
        if (evaluationMapper.exists(evaluationQuery)) {
            log.info("该教学点已评价, activityId={}, teachingPointId={}", 
                activityId, request.getTeachingPointId());
            throw new BusinessException(1602, "该教学点已评价");
        }

        // 6. 创建评价记录
        Evaluation evaluation = Evaluation.builder()
                .activityId(activityId)
                .teachingPointId(request.getTeachingPointId())
                .q1Satisfaction(request.getQ1Satisfaction())
                .q2Practicality(request.getQ2Practicality())
                .q3Quality(request.getQ3Quality())
                .suggestionText(request.getSuggestionText())
                .submittedTime(LocalDateTime.now())
                .sourceQrcodeId(qrCode.getId())
                .build();

        evaluationMapper.insert(evaluation);
        log.info("评价提交成功, evaluationId={}, activityId={}, teachingPointId={}", 
            evaluation.getId(), activityId, request.getTeachingPointId());

        // 7. 返回成功响应
        return EvaluationSubmitResponseVO.builder()
                .id(evaluation.getId())
                .activityId(evaluation.getActivityId())
                .teachingPointId(evaluation.getTeachingPointId())
                .submittedTime(evaluation.getSubmittedTime())
                .message("评价提交成功")
                .build();
    }

    @Override
    public Page<EvaluationVO> queryEvaluationList(EvaluationQueryRequest request, String countyCode) {
        log.info("查询评价列表, activityId={}, teachingPointId={}, pageNum={}, pageSize={}, countyCode={}", 
            request.getActivityId(), request.getTeachingPointId(), request.getPageNum(), request.getPageSize(), countyCode);

        // 构建查询条件
        LambdaQueryWrapper<Evaluation> queryWrapper = new LambdaQueryWrapper<>();
        
        if (ObjectUtil.isNotNull(request.getActivityId())) {
            queryWrapper.eq(Evaluation::getActivityId, request.getActivityId());
        }
        
        if (ObjectUtil.isNotNull(request.getTeachingPointId())) {
            queryWrapper.eq(Evaluation::getTeachingPointId, request.getTeachingPointId());
        }
        
        queryWrapper.orderByDesc(Evaluation::getSubmittedTime);

        // 分页查询
        Page<Evaluation> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = evaluationMapper.selectPage(page, queryWrapper);

        // 转换为VO
        Page<EvaluationVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::convertToVO).toList());

        return voPage;
    }

    @Override
    public EvaluationStatisticsVO queryEvaluationStatistics(Long activityId) {
        log.info("查询评价统计, activityId={}", activityId);

        validator.validatePositive(activityId, "活动ID");

        // 验证活动是否存在
        Activity activity = activityMapper.selectById(activityId);
        if (ObjectUtil.isNull(activity)) {
            log.warn("活动不存在, activityId={}", activityId);
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }

        // 统计总数
        LambdaQueryWrapper<Evaluation> countQuery = new LambdaQueryWrapper<>();
        countQuery.eq(Evaluation::getActivityId, activityId);
        long totalCount = evaluationMapper.selectCount(countQuery);

        // 如果没有评价，返回空的统计信息
        if (totalCount == 0) {
            return EvaluationStatisticsVO.builder()
                    .activityId(activityId)
                    .totalCount(0L)
                    .avgSatisfaction(0.0)
                    .avgPracticality(0.0)
                    .avgQuality(0.0)
                    .satisfactionLevel3(0L)
                    .satisfactionLevel2(0L)
                    .satisfactionLevel1(0L)
                    .suggestionCount(0L)
                    .build();
        }

        // 查询各项平均分
        Double avgSatisfaction = evaluationMapper.queryAvgSatisfaction(activityId);
        Double avgPracticality = evaluationMapper.queryAvgPracticality(activityId);
        Double avgQuality = evaluationMapper.queryAvgQuality(activityId);

        // 查询各评分等级数量
        Long satisfactionLevel3 = evaluationMapper.querySatisfactionLevel3Count(activityId);
        Long satisfactionLevel2 = evaluationMapper.querySatisfactionLevel2Count(activityId);
        Long satisfactionLevel1 = evaluationMapper.querySatisfactionLevel1Count(activityId);

        // 查询包含建议的评价数量
        Long suggestionCount = evaluationMapper.querySuggestionCount(activityId);

        log.info("评价统计查询完成, totalCount={}, avgSatisfaction={}, avgPracticality={}, avgQuality={}", 
            totalCount, avgSatisfaction, avgPracticality, avgQuality);

        return EvaluationStatisticsVO.builder()
                .activityId(activityId)
                .totalCount(totalCount)
                .avgSatisfaction(avgSatisfaction != null ? avgSatisfaction : 0.0)
                .avgPracticality(avgPracticality != null ? avgPracticality : 0.0)
                .avgQuality(avgQuality != null ? avgQuality : 0.0)
                .satisfactionLevel3(satisfactionLevel3 != null ? satisfactionLevel3 : 0L)
                .satisfactionLevel2(satisfactionLevel2 != null ? satisfactionLevel2 : 0L)
                .satisfactionLevel1(satisfactionLevel1 != null ? satisfactionLevel1 : 0L)
                .suggestionCount(suggestionCount != null ? suggestionCount : 0L)
                .build();
    }

    /**
     * 将评价实体转换为VO
     */
    private EvaluationVO convertToVO(Evaluation evaluation) {
        return EvaluationVO.builder()
                .id(evaluation.getId())
                .activityId(evaluation.getActivityId())
                .teachingPointId(evaluation.getTeachingPointId())
                .q1Satisfaction(evaluation.getQ1Satisfaction())
                .q2Practicality(evaluation.getQ2Practicality())
                .q3Quality(evaluation.getQ3Quality())
                .suggestionText(evaluation.getSuggestionText())
                .submittedTime(evaluation.getSubmittedTime())
                .build();
    }
}
