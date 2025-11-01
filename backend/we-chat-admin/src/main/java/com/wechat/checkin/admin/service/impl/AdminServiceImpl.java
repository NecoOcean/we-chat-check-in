package com.wechat.checkin.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.checkin.admin.dto.AdminQueryRequest;
import com.wechat.checkin.admin.dto.CreateAdminRequest;
import com.wechat.checkin.admin.dto.UpdateAdminPasswordRequest;
import com.wechat.checkin.admin.dto.UpdateAdminRequest;
import com.wechat.checkin.admin.service.AdminService;
import com.wechat.checkin.admin.vo.AdminVO;
import com.wechat.checkin.auth.entity.Admin;
import com.wechat.checkin.auth.mapper.AdminMapper;
import com.wechat.checkin.common.enums.StatusEnum;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.PageResult;
import com.wechat.checkin.common.response.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员服务实现类
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAdmin(CreateAdminRequest request) {
        log.info("开始创建管理员: username={}, role={}, countyCode={}", 
                request.getUsername(), request.getRole(), request.getCountyCode());

        // 1. 检查用户名是否已存在
        int count = adminMapper.countByUsername(request.getUsername());
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 2. 如果是县级管理员，验证县域代码是否存在
        if ("county".equals(request.getRole()) && StringUtils.hasText(request.getCountyCode())) {
            if (!adminMapper.existsCountyCode(request.getCountyCode())) {
                throw new BusinessException(ResultCode.COUNTY_CODE_NOT_FOUND);
            }
        }

        // 3. 构建管理员实体
        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(request.getRole());
        admin.setCountyCode(request.getCountyCode());
        admin.setStatus(StatusEnum.ENABLED);

        // 4. 保存到数据库
        int result = adminMapper.insert(admin);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED);
        }

        log.info("创建管理员成功: adminId={}, username={}", admin.getId(), admin.getUsername());
        return admin.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdmin(Long adminId, UpdateAdminRequest request) {
        log.info("开始更新管理员: adminId={}, username={}, countyCode={}", 
                adminId, request.getUsername(), request.getCountyCode());

        // 1. 查询管理员是否存在
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(ResultCode.ADMIN_NOT_FOUND);
        }

        // 2. 检查账号状态
        if (StatusEnum.DELETED.equals(admin.getStatus())) {
            throw new BusinessException(ResultCode.ADMIN_DELETED);
        }

        // 3. 更新用户名（如果提供）
        if (StringUtils.hasText(request.getUsername()) && 
            !request.getUsername().equals(admin.getUsername())) {
            // 检查新用户名是否已被使用
            int count = adminMapper.countByUsernameExcludeId(request.getUsername(), adminId);
            if (count > 0) {
                throw new BusinessException(ResultCode.USERNAME_EXISTS);
            }
            admin.setUsername(request.getUsername());
        }

        // 4. 更新县域代码（如果提供）
        if (StringUtils.hasText(request.getCountyCode())) {
            // 验证县域代码是否存在
            if (!adminMapper.existsCountyCode(request.getCountyCode())) {
                throw new BusinessException(ResultCode.COUNTY_CODE_NOT_FOUND);
            }
            admin.setCountyCode(request.getCountyCode());
        }

        // 5. 保存更新
        int result = adminMapper.updateById(admin);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED);
        }

        log.info("更新管理员成功: adminId={}", adminId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdminPassword(Long adminId, UpdateAdminPasswordRequest request) {
        log.info("开始重置管理员密码: adminId={}", adminId);

        // 1. 查询管理员是否存在
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(ResultCode.ADMIN_NOT_FOUND);
        }

        // 2. 检查账号状态
        if (StatusEnum.DELETED.equals(admin.getStatus())) {
            throw new BusinessException(ResultCode.ADMIN_DELETED);
        }

        // 3. 加密新密码
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());

        // 4. 更新密码
        int result = adminMapper.updatePassword(adminId, encodedPassword);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED);
        }

        log.info("重置管理员密码成功: adminId={}", adminId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableAdmin(Long adminId) {
        log.info("开始启用管理员: adminId={}", adminId);
        updateAdminStatus(adminId, StatusEnum.ENABLED);
        log.info("启用管理员成功: adminId={}", adminId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableAdmin(Long adminId) {
        log.info("开始禁用管理员: adminId={}", adminId);
        updateAdminStatus(adminId, StatusEnum.DISABLED);
        log.info("禁用管理员成功: adminId={}", adminId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdmin(Long adminId) {
        log.info("开始删除管理员: adminId={}", adminId);
        updateAdminStatus(adminId, StatusEnum.DELETED);
        log.info("删除管理员成功: adminId={}", adminId);
    }

    @Override
    public AdminVO getAdminDetail(Long adminId) {
        log.info("查询管理员详情: adminId={}", adminId);

        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(ResultCode.ADMIN_NOT_FOUND);
        }

        return convertToVO(admin);
    }

    @Override
    public PageResult<AdminVO> listAdmins(AdminQueryRequest request) {
        log.info("分页查询管理员列表: page={}, size={}, username={}, role={}, countyCode={}, status={}", 
                request.getCurrent(), request.getSize(), request.getUsername(), 
                request.getRole(), request.getCountyCode(), request.getStatus());

        // 1. 构建查询条件
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        
        // 用户名模糊查询
        if (StringUtils.hasText(request.getUsername())) {
            wrapper.like(Admin::getUsername, request.getUsername());
        }
        
        // 角色过滤
        if (StringUtils.hasText(request.getRole())) {
            wrapper.eq(Admin::getRole, request.getRole());
        }
        
        // 县域过滤
        if (StringUtils.hasText(request.getCountyCode())) {
            wrapper.eq(Admin::getCountyCode, request.getCountyCode());
        }
        
        // 状态过滤
        if (request.getStatus() != null) {
            wrapper.eq(Admin::getStatus, request.getStatus());
        } else {
            // 默认不显示已删除的账号
            wrapper.ne(Admin::getStatus, StatusEnum.DELETED);
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(Admin::getCreatedTime);

        // 2. 分页查询
        Page<Admin> page = new Page<>(request.getCurrent(), request.getSize());
        Page<Admin> resultPage = adminMapper.selectPage(page, wrapper);

        // 3. 转换为VO
        List<AdminVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.success(
                resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getTotal(),
                voList
        );
    }

    /**
     * 更新管理员状态
     *
     * @param adminId 管理员ID
     * @param status 状态
     */
    private void updateAdminStatus(Long adminId, StatusEnum status) {
        // 1. 查询管理员是否存在
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(ResultCode.ADMIN_NOT_FOUND);
        }

        // 2. 更新状态
        admin.setStatus(status);
        int result = adminMapper.updateById(admin);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED);
        }
    }

    /**
     * 将Admin实体转换为AdminVO
     *
     * @param admin 管理员实体
     * @return AdminVO
     */
    private AdminVO convertToVO(Admin admin) {
        AdminVO vo = new AdminVO();
        BeanUtil.copyProperties(admin, vo);
        
        // 设置角色名称
        vo.setRoleName(getRoleName(admin.getRole()));
        
        // 设置状态描述
        vo.setStatusDesc(getStatusDesc(admin.getStatus()));
        
        return vo;
    }

    /**
     * 获取角色名称
     *
     * @param role 角色代码
     * @return 角色名称
     */
    private String getRoleName(String role) {
        if ("city".equals(role)) {
            return "市级管理员";
        } else if ("county".equals(role)) {
            return "县级管理员";
        }
        return role;
    }

    /**
     * 获取状态描述
     *
     * @param status 状态
     * @return 状态描述
     */
    private String getStatusDesc(StatusEnum status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case ENABLED:
                return "启用";
            case DISABLED:
                return "禁用";
            case DELETED:
                return "已删除";
            default:
                return status.name();
        }
    }
}

