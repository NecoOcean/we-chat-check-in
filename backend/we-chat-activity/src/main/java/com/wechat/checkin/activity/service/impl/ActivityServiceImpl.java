package com.wechat.checkin.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.checkin.activity.dto.ActivityQueryRequest;
import com.wechat.checkin.activity.dto.CreateActivityRequest;
import com.wechat.checkin.activity.entity.Activity;
import com.wechat.checkin.activity.mapper.ActivityMapper;
import com.wechat.checkin.activity.service.ActivityService;
import com.wechat.checkin.activity.vo.ActivityDetailVO;
import com.wechat.checkin.activity.vo.ActivityVO;
import com.wechat.checkin.auth.entity.Admin;
import com.wechat.checkin.auth.mapper.AdminMapper;
import com.wechat.checkin.common.enums.ActivityStatusEnum;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.PageResult;
import com.wechat.checkin.common.response.ResultCode;
import com.wechat.checkin.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;
    private final AdminMapper adminMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createActivity(CreateActivityRequest request, Long adminId, String adminRole, String countyCode) {
        // 参数校验
        validateCreateRequest(request, adminRole, countyCode);

        // 构建活动实体（createdTime和updatedTime由MetaObjectHandler自动填充）
        Activity activity = Activity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .scopeCountyCode(determineScopeCountyCode(adminRole, countyCode))  // 从登录用户获取县域
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .createdId(adminId)
                .status(ActivityStatusEnum.ONGOING)
                .build();

        // 插入数据库（使用MyBatis Plus的insert方法）
        activityMapper.insert(activity);

        log.info("活动创建成功，活动ID: {}, 创建人: {}, 角色: {}, 县域: {}", 
                activity.getId(), adminId, adminRole, activity.getScopeCountyCode());

        return activity.getId();
    }

    @Override
    public PageResult<ActivityVO> listActivities(ActivityQueryRequest request, Long adminId, String adminRole, String countyCode) {
        log.info("查询活动列表，操作员ID: {}, 操作员Role: {}, 县域: {}", adminId, adminRole, countyCode);
        
        // 权限过滤：县级管理员只能查看本区活动
        String filterCountyCode = "county".equals(adminRole) ? countyCode : request.getCountyCode();

        // 构建查询条件（使用LambdaQueryWrapper）
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(request.getStatus()), Activity::getStatus, request.getStatus())
               .and(StringUtils.isNotEmpty(filterCountyCode), w -> 
                   w.eq(Activity::getScopeCountyCode, filterCountyCode)
                    .or()
                    .isNull(Activity::getScopeCountyCode))  // 全市活动（scopeCountyCode为null）对县级可见
               .orderByDesc(Activity::getCreatedTime);

        // 使用MyBatis Plus的Page对象进行分页查询
        Page<Activity> page = new Page<>(request.getPage(), request.getSize());
        Page<Activity> result = activityMapper.selectPage(page, wrapper);

        // 转换为VO
        List<ActivityVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.success((long) request.getPage(), (long) request.getSize(), result.getTotal(), voList);
    }

    @Override
    public ActivityDetailVO getActivityDetail(Long activityId, String adminRole, String countyCode) {
        // 查询活动
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }

        // 权限校验：县级管理员只能查看本县活动
        validateActivityPermission(activity, adminRole, countyCode);

        // 查询统计数据
        int participatedCount = activityMapper.countParticipatedTeachingPoints(activityId);
        int totalAttendees = activityMapper.countTotalAttendees(activityId);
        int evaluationCount = activityMapper.countEvaluations(activityId);

        // 构建返回结果
        return ActivityDetailVO.builder()
                .activity(convertToVO(activity))
                .participatedCount(participatedCount)
                .totalAttendees(totalAttendees)
                .evaluationCount(evaluationCount)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishActivity(Long activityId, Long adminId, String adminRole, String countyCode) {
        // 查询活动
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }

        // 权限校验
        validateActivityPermission(activity, adminRole, countyCode);

        // 状态校验
        if (ActivityStatusEnum.ENDED.equals(activity.getStatus())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "活动已结束，无需重复操作");
        }

        // 使用LambdaUpdateWrapper更新活动状态（updatedTime由MetaObjectHandler自动填充）
        LambdaUpdateWrapper<Activity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Activity::getId, activityId)
                    .set(Activity::getStatus, ActivityStatusEnum.ENDED)
                    .set(Activity::getEndedTime, LocalDateTime.now());
        activityMapper.update(null, updateWrapper);

        log.info("活动结束成功，活动ID: {}, 操作人: {}", activityId, adminId);
    }

    /**
     * 验证创建活动请求参数
     */
    private void validateCreateRequest(CreateActivityRequest request, String adminRole, String countyCode) {
        // 时间校验
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "开始时间不能晚于结束时间");
        }

        // 县级管理员必须有县域编码
        if ("county".equals(adminRole) && StringUtils.isEmpty(countyCode)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "县级管理员缺少县域信息");
        }
    }

    /**
     * 确定活动的县域范围
     * 市级管理员：可创建全市活动（scopeCountyCode为null）
     * 县级管理员：只能创建本县活动（scopeCountyCode为当前登录用户的县域）
     */
    private String determineScopeCountyCode(String adminRole, String countyCode) {
        if ("county".equals(adminRole)) {
            // 县级管理员：强制使用其县域编码
            return countyCode;
        } else {
            // 市级管理员：scopeCountyCode为null，表示全市范围
            return null;
        }
    }

    /**
     * 验证活动访问权限
     */
    private void validateActivityPermission(Activity activity, String adminRole, String countyCode) {
        if ("county".equals(adminRole)) {
            // 县级管理员只能访问本县活动
            if (activity.getScopeCountyCode() != null && !activity.getScopeCountyCode().equals(countyCode)) {
                throw new BusinessException(ResultCode.FORBIDDEN, "无权访问该活动");
            }
        }
    }

    /**
     * 将Activity实体转换为ActivityVO
     */
    private ActivityVO convertToVO(Activity activity) {
        // 查询创建人信息
        Admin creator = adminMapper.selectById(activity.getCreatedId());

        return ActivityVO.builder()
                .id(activity.getId())
                .name(activity.getName())
                .description(activity.getDescription())
                .scopeCountyCode(activity.getScopeCountyCode())
                .scopeCountyName(activity.getScopeCountyCode() != null ? getCountyName(activity.getScopeCountyCode()) : "全市")
                .startTime(activity.getStartTime())
                .endTime(activity.getEndTime())
                .endedTime(activity.getEndedTime())
                .createdId(activity.getCreatedId())
                .createdUsername(creator != null ? creator.getUsername() : "未知")
                .status(activity.getStatus())
                .statusDesc(activity.getStatus().getDescription())
                .createdTime(activity.getCreatedTime())
                .updatedTime(activity.getUpdatedTime())
                .build();
    }

    /**
     * 获取县域名称（简化实现，实际应从县域表查询）
     */
    private String getCountyName(String countyCode) {
        // TODO: 从县域表查询县域名称
        return countyCode;
    }
}
