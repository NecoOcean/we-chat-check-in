package com.wechat.checkin.teachingpoint.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.checkin.auth.security.UserPrincipal;
import com.wechat.checkin.auth.util.SecurityContextHolder;
import com.wechat.checkin.common.enums.StatusEnum;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.PageResult;
import com.wechat.checkin.common.response.ResultCode;
import com.wechat.checkin.teachingpoint.dto.CreateTeachingPointRequest;
import com.wechat.checkin.teachingpoint.dto.TeachingPointQueryRequest;
import com.wechat.checkin.teachingpoint.dto.UpdateTeachingPointRequest;
import com.wechat.checkin.teachingpoint.entity.TeachingPoint;
import com.wechat.checkin.teachingpoint.mapper.TeachingPointMapper;
import com.wechat.checkin.teachingpoint.service.TeachingPointService;
import com.wechat.checkin.teachingpoint.vo.TeachingPointVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 教学点服务实现类
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeachingPointServiceImpl implements TeachingPointService {

    private final TeachingPointMapper teachingPointMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTeachingPoint(CreateTeachingPointRequest request) {
        log.info("创建教学点: name={}, countyCode={}", request.getName(), request.getCountyCode());

        // 县级管理员只能创建本县教学点
        UserPrincipal currentUser = SecurityContextHolder.getCurrentUser();
        if (!SecurityContextHolder.isCityAdmin()) {
            String userCountyCode = currentUser.getCountyCode();
            if (StrUtil.isBlank(userCountyCode) || !userCountyCode.equals(request.getCountyCode())) {
                log.warn("县级管理员只能创建本县教学点: userCountyCode={}, requestCountyCode={}", 
                        userCountyCode, request.getCountyCode());
                throw new BusinessException(ResultCode.PERMISSION_DENIED);
            }
        }

        // 检查同一县域内教学点名称是否已存在
        int count = teachingPointMapper.countByNameAndCounty(request.getName(), request.getCountyCode());
        if (count > 0) {
            log.warn("教学点名称已存在: name={}, countyCode={}", request.getName(), request.getCountyCode());
            throw new BusinessException(ResultCode.TEACHING_POINT_NAME_EXISTS);
        }

        // 构建教学点实体
        TeachingPoint teachingPoint = new TeachingPoint();
        teachingPoint.setName(request.getName());
        teachingPoint.setCountyCode(request.getCountyCode());
        teachingPoint.setStatus(StatusEnum.ENABLED);

        // 保存到数据库
        int result = teachingPointMapper.insert(teachingPoint);
        if (result <= 0) {
            log.error("创建教学点失败: name={}", request.getName());
            throw new BusinessException(ResultCode.OPERATION_FAILED);
        }

        log.info("教学点创建成功: id={}, name={}", teachingPoint.getId(), teachingPoint.getName());
        return teachingPoint.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTeachingPoint(Long id, UpdateTeachingPointRequest request) {
        log.info("更新教学点: id={}, name={}, countyCode={}", 
                id, request.getName(), request.getCountyCode());

        // 查询教学点是否存在
        TeachingPoint teachingPoint = teachingPointMapper.selectById(id);
        if (teachingPoint == null) {
            log.warn("教学点不存在: id={}", id);
            throw new BusinessException(ResultCode.TEACHING_POINT_NOT_FOUND);
        }

        // 检查是否已删除
        if (StatusEnum.DELETED.equals(teachingPoint.getStatus())) {
            log.warn("教学点已被删除: id={}", id);
            throw new BusinessException(ResultCode.TEACHING_POINT_DELETED);
        }

        // 权限校验
        validatePermission(teachingPoint.getCountyCode());

        // 更新县域编码（仅市级管理员可操作）
        if (StrUtil.isNotBlank(request.getCountyCode()) && 
            !request.getCountyCode().equals(teachingPoint.getCountyCode())) {
            // 检查是否为市级管理员
            if (!SecurityContextHolder.isCityAdmin()) {
                log.warn("县级管理员不允许修改教学点县域: teachingPointId={}, oldCounty={}, newCounty={}", 
                        id, teachingPoint.getCountyCode(), request.getCountyCode());
                throw new BusinessException(ResultCode.PERMISSION_DENIED);
            }
            teachingPoint.setCountyCode(request.getCountyCode());
        }

        // 更新教学点名称
        if (StrUtil.isNotBlank(request.getName()) && 
            !request.getName().equals(teachingPoint.getName())) {
            // 检查新名称在目标县域内是否已存在
            String targetCountyCode = teachingPoint.getCountyCode(); // 使用当前县域（可能已被市级管理员修改）
            int count = teachingPointMapper.countByNameAndCountyExcludeId(
                    request.getName(), targetCountyCode, id);
            if (count > 0) {
                log.warn("教学点名称已存在: name={}, countyCode={}", 
                        request.getName(), targetCountyCode);
                throw new BusinessException(ResultCode.TEACHING_POINT_NAME_EXISTS);
            }
            teachingPoint.setName(request.getName());
        }

        // 保存更新
        int result = teachingPointMapper.updateById(teachingPoint);
        if (result <= 0) {
            log.error("更新教学点失败: id={}", id);
            throw new BusinessException(ResultCode.OPERATION_FAILED);
        }

        log.info("教学点更新成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeachingPoint(Long id) {
        log.info("删除教学点: id={}", id);

        // 查询教学点是否存在
        TeachingPoint teachingPoint = teachingPointMapper.selectById(id);
        if (teachingPoint == null) {
            log.warn("教学点不存在: id={}", id);
            throw new BusinessException(ResultCode.TEACHING_POINT_NOT_FOUND);
        }

        // 权限校验
        validatePermission(teachingPoint.getCountyCode());

        // 软删除：设置状态为已删除
        teachingPoint.setStatus(StatusEnum.DELETED);
        int result = teachingPointMapper.updateById(teachingPoint);
        if (result <= 0) {
            log.error("删除教学点失败: id={}", id);
            throw new BusinessException(ResultCode.OPERATION_FAILED);
        }

        log.info("教学点删除成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableTeachingPoint(Long id) {
        log.info("启用教学点: id={}", id);
        updateStatus(id, StatusEnum.ENABLED);
        log.info("教学点启用成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableTeachingPoint(Long id) {
        log.info("禁用教学点: id={}", id);
        updateStatus(id, StatusEnum.DISABLED);
        log.info("教学点禁用成功: id={}", id);
    }

    @Override
    public TeachingPointVO getTeachingPointDetail(Long id) {
        log.info("查询教学点详情: id={}", id);

        // 查询教学点
        TeachingPoint teachingPoint = teachingPointMapper.selectById(id);
        if (teachingPoint == null) {
            log.warn("教学点不存在: id={}", id);
            throw new BusinessException(ResultCode.TEACHING_POINT_NOT_FOUND);
        }

        // 权限校验
        validatePermission(teachingPoint.getCountyCode());

        // 转换为VO
        return convertToVO(teachingPoint);
    }

    @Override
    public PageResult<TeachingPointVO> listTeachingPoints(TeachingPointQueryRequest request) {
        log.info("查询教学点列表: countyCode={}, status={}, name={}, current={}, size={}", 
                request.getCountyCode(), request.getStatus(), request.getName(),
                request.getCurrent(), request.getSize());

        // 获取当前用户信息
        UserPrincipal currentUser = SecurityContextHolder.getCurrentUser();
        String userCountyCode = currentUser.getCountyCode();
        boolean isCityAdmin = SecurityContextHolder.isCityAdmin();

        // 构建查询条件
        LambdaQueryWrapper<TeachingPoint> wrapper = new LambdaQueryWrapper<>();
        
        // 权限过滤：县级管理员只能查看本县教学点
        if (!isCityAdmin && StrUtil.isNotBlank(userCountyCode)) {
            wrapper.eq(TeachingPoint::getCountyCode, userCountyCode);
        } else if (StrUtil.isNotBlank(request.getCountyCode())) {
            // 市级管理员可以按县域过滤
            wrapper.eq(TeachingPoint::getCountyCode, request.getCountyCode());
        }

        // 名称模糊查询
        if (StrUtil.isNotBlank(request.getName())) {
            wrapper.like(TeachingPoint::getName, request.getName());
        }

        // 状态过滤：默认不显示已删除的
        if (StrUtil.isNotBlank(request.getStatus())) {
            StatusEnum status = StatusEnum.getByValue(request.getStatus());
            if (status != null) {
                wrapper.eq(TeachingPoint::getStatus, status);
            }
        } else {
            // 默认不显示已删除的
            wrapper.ne(TeachingPoint::getStatus, StatusEnum.DELETED);
        }

        // 按创建时间倒序
        wrapper.orderByDesc(TeachingPoint::getCreatedTime);

        // 分页查询
        Page<TeachingPoint> page = new Page<>(request.getCurrent(), request.getSize());
        Page<TeachingPoint> resultPage = teachingPointMapper.selectPage(page, wrapper);

        // 转换为VO
        List<TeachingPointVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .toList();
        
        PageResult<TeachingPointVO> pageResult = PageResult.success(
                resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getTotal(),
                voList
        );

        log.info("查询教学点列表成功: total={}", pageResult.getTotal());
        return pageResult;
    }

    /**
     * 更新教学点状态
     * 
     * @param id 教学点ID
     * @param status 新状态
     */
    private void updateStatus(Long id, StatusEnum status) {
        // 查询教学点是否存在
        TeachingPoint teachingPoint = teachingPointMapper.selectById(id);
        if (teachingPoint == null) {
            log.warn("教学点不存在: id={}", id);
            throw new BusinessException(ResultCode.TEACHING_POINT_NOT_FOUND);
        }

        // 权限校验
        validatePermission(teachingPoint.getCountyCode());

        // 更新状态
        teachingPoint.setStatus(status);
        int result = teachingPointMapper.updateById(teachingPoint);
        if (result <= 0) {
            log.error("更新教学点状态失败: id={}, status={}", id, status);
            throw new BusinessException(ResultCode.OPERATION_FAILED);
        }
    }

    /**
     * 权限校验
     * 县级管理员只能操作本县的教学点
     * 
     * @param countyCode 教学点所属县域编码
     */
    private void validatePermission(String countyCode) {
        UserPrincipal currentUser = SecurityContextHolder.getCurrentUser();
        
        // 市级管理员可以操作所有教学点
        if (SecurityContextHolder.isCityAdmin()) {
            return;
        }

        // 县级管理员只能操作本县教学点
        String userCountyCode = currentUser.getCountyCode();
        if (StrUtil.isBlank(userCountyCode) || !userCountyCode.equals(countyCode)) {
            log.warn("权限不足: userCountyCode={}, teachingPointCountyCode={}", 
                    userCountyCode, countyCode);
            throw new BusinessException(ResultCode.PERMISSION_DENIED);
        }
    }

    /**
     * 将教学点实体转换为VO
     * 
     * @param teachingPoint 教学点实体
     * @return 教学点VO
     */
    private TeachingPointVO convertToVO(TeachingPoint teachingPoint) {
        if (teachingPoint == null) {
            return null;
        }

        TeachingPointVO vo = new TeachingPointVO();
        vo.setId(teachingPoint.getId());
        vo.setName(teachingPoint.getName());
        vo.setCountyCode(teachingPoint.getCountyCode());
        
        // 查询县域名称
        if (StrUtil.isNotBlank(teachingPoint.getCountyCode())) {
            String countyName = teachingPointMapper.getCountyNameByCode(teachingPoint.getCountyCode());
            vo.setCountyName(countyName != null ? countyName : "未知县域");
        } else {
            vo.setCountyName(null);
        }
        
        vo.setStatus(teachingPoint.getStatus() != null ? 
                teachingPoint.getStatus().getValue() : null);
        vo.setStatusDesc(teachingPoint.getStatus() != null ? 
                teachingPoint.getStatus().getDescription() : null);
        vo.setCreatedTime(teachingPoint.getCreatedTime());
        vo.setUpdatedTime(teachingPoint.getUpdatedTime());

        return vo;
    }
}

