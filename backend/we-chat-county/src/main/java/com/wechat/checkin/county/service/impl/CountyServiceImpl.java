package com.wechat.checkin.county.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.checkin.auth.util.SecurityContextHolder;
import com.wechat.checkin.common.enums.StatusEnum;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.ResultCode;
import com.wechat.checkin.county.dto.CountyCreateRequest;
import com.wechat.checkin.county.dto.CountyQueryRequest;
import com.wechat.checkin.county.dto.CountyUpdateRequest;
import com.wechat.checkin.county.entity.County;
import com.wechat.checkin.county.mapper.CountyMapper;
import com.wechat.checkin.county.service.CountyService;
import com.wechat.checkin.county.vo.CountyVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 县域服务实现类
 * 
 * @author WeChat Check-in System
 * @since 1.3.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CountyServiceImpl implements CountyService {

    private final CountyMapper countyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CountyVO createCounty(CountyCreateRequest request) {
        log.info("创建县域: code={}, name={}", request.getCode(), request.getName());

        // 仅市级管理员可以创建县域
        validateCityAdmin();

        // 检查县域编码是否已存在
        County existingCounty = countyMapper.selectById(request.getCode());
        if (existingCounty != null) {
            log.warn("县域编码已存在: code={}", request.getCode());
            throw new BusinessException(ResultCode.COUNTY_ALREADY_EXISTS);
        }

        // 检查县域名称是否已存在
        LambdaQueryWrapper<County> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(County::getName, request.getName());
        Long count = countyMapper.selectCount(wrapper);
        if (count > 0) {
            log.warn("县域名称已存在: name={}", request.getName());
            throw new BusinessException(ResultCode.COUNTY_NAME_EXISTS);
        }

        // 构建县域实体
        County county = new County();
        county.setCode(request.getCode());
        county.setName(request.getName());
        county.setStatus(StatusEnum.ENABLED);

        // 保存到数据库
        int result = countyMapper.insert(county);
        if (result <= 0) {
            log.error("创建县域失败: code={}", request.getCode());
            throw new BusinessException(ResultCode.OPERATION_FAILED);
        }

        log.info("县域创建成功: code={}, name={}", county.getCode(), county.getName());
        return convertToVO(county);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CountyVO updateCounty(String code, CountyUpdateRequest request) {
        log.info("更新县域: code={}, name={}", code, request.getName());

        // 仅市级管理员可以更新县域
        validateCityAdmin();

        // 查询县域是否存在
        County county = countyMapper.selectById(code);
        if (county == null) {
            log.warn("县域不存在: code={}", code);
            throw new BusinessException(ResultCode.COUNTY_NOT_FOUND);
        }

        // 检查是否已删除
        if (StatusEnum.DELETED.equals(county.getStatus())) {
            log.warn("县域已被删除: code={}", code);
            throw new BusinessException(ResultCode.COUNTY_DELETED);
        }

        // 更新县域名称
        if (StrUtil.isNotBlank(request.getName()) && 
            !request.getName().equals(county.getName())) {
            // 检查新名称是否已存在
            LambdaQueryWrapper<County> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(County::getName, request.getName())
                   .ne(County::getCode, code);
            Long count = countyMapper.selectCount(wrapper);
            if (count > 0) {
                log.warn("县域名称已存在: name={}", request.getName());
                throw new BusinessException(ResultCode.COUNTY_NAME_EXISTS);
            }
            county.setName(request.getName());
        }

        // 保存更新
        int result = countyMapper.updateById(county);
        if (result <= 0) {
            log.error("更新县域失败: code={}", code);
            throw new BusinessException(ResultCode.OPERATION_FAILED);
        }

        log.info("县域更新成功: code={}", code);
        return convertToVO(county);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCounty(String code) {
        log.info("删除县域: code={}", code);

        // 仅市级管理员可以删除县域
        validateCityAdmin();

        // 查询县域是否存在
        County county = countyMapper.selectById(code);
        if (county == null) {
            log.warn("县域不存在: code={}", code);
            throw new BusinessException(ResultCode.COUNTY_NOT_FOUND);
        }

        // 软删除：设置状态为已删除
        county.setStatus(StatusEnum.DELETED);
        int result = countyMapper.updateById(county);
        if (result <= 0) {
            log.error("删除县域失败: code={}", code);
            throw new BusinessException(ResultCode.OPERATION_FAILED);
        }

        log.info("县域删除成功: code={}", code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CountyVO enableCounty(String code) {
        log.info("启用县域: code={}", code);
        County county = updateStatus(code, StatusEnum.ENABLED);
        log.info("县域启用成功: code={}", code);
        return convertToVO(county);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CountyVO disableCounty(String code) {
        log.info("禁用县域: code={}", code);
        County county = updateStatus(code, StatusEnum.DISABLED);
        log.info("县域禁用成功: code={}", code);
        return convertToVO(county);
    }

    @Override
    public CountyVO getCountyByCode(String code) {
        log.info("查询县域详情: code={}", code);

        // 查询县域
        County county = countyMapper.selectById(code);
        if (county == null) {
            log.warn("县域不存在: code={}", code);
            throw new BusinessException(ResultCode.COUNTY_NOT_FOUND);
        }

        // 转换为VO
        return convertToVO(county);
    }

    @Override
    public Page<CountyVO> listCounties(CountyQueryRequest request) {
        log.info("查询县域列表: code={}, name={}, status={}, current={}, size={}", 
                request.getCode(), request.getName(), request.getStatus(),
                request.getCurrent(), request.getSize());

        // 构建查询条件
        LambdaQueryWrapper<County> wrapper = new LambdaQueryWrapper<>();
        
        // 县域编码精确查询
        if (StrUtil.isNotBlank(request.getCode())) {
            wrapper.eq(County::getCode, request.getCode());
        }

        // 名称模糊查询
        if (StrUtil.isNotBlank(request.getName())) {
            wrapper.like(County::getName, request.getName());
        }

        // 状态过滤：默认不显示已删除的
        if (request.getStatus() != null) {
            wrapper.eq(County::getStatus, request.getStatus());
        } else {
            // 默认不显示已删除的
            wrapper.ne(County::getStatus, StatusEnum.DELETED);
        }

        // 按创建时间倒序
        wrapper.orderByDesc(County::getCreatedTime);

        // 分页查询
        Page<County> page = new Page<>(request.getCurrent(), request.getSize());
        Page<County> resultPage = countyMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<CountyVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        voPage.setRecords(resultPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        log.info("查询县域列表成功: total={}", voPage.getTotal());
        return voPage;
    }

    /**
     * 更新县域状态
     * 
     * @param code 县域编码
     * @param status 新状态
     * @return 更新后的县域
     */
    private County updateStatus(String code, StatusEnum status) {
        // 仅市级管理员可以更新状态
        validateCityAdmin();

        // 查询县域是否存在
        County county = countyMapper.selectById(code);
        if (county == null) {
            log.warn("县域不存在: code={}", code);
            throw new BusinessException(ResultCode.COUNTY_NOT_FOUND);
        }

        // 检查是否已删除
        if (StatusEnum.DELETED.equals(county.getStatus())) {
            log.warn("县域已被删除，无法更新状态: code={}", code);
            throw new BusinessException(ResultCode.COUNTY_DELETED);
        }

        // 更新状态
        county.setStatus(status);
        int result = countyMapper.updateById(county);
        if (result <= 0) {
            log.error("更新县域状态失败: code={}, status={}", code, status);
            throw new BusinessException(ResultCode.OPERATION_FAILED);
        }

        return county;
    }

    /**
     * 校验是否为市级管理员
     */
    private void validateCityAdmin() {
        if (!SecurityContextHolder.isCityAdmin()) {
            log.warn("仅市级管理员可以管理县域");
            throw new BusinessException(ResultCode.PERMISSION_DENIED);
        }
    }

    /**
     * 将县域实体转换为VO
     * 
     * @param county 县域实体
     * @return 县域VO
     */
    private CountyVO convertToVO(County county) {
        if (county == null) {
            return null;
        }

        return CountyVO.builder()
                .code(county.getCode())
                .name(county.getName())
                .status(county.getStatus())
                .createdTime(county.getCreatedTime())
                .updatedTime(county.getUpdatedTime())
                .build();
    }
}

