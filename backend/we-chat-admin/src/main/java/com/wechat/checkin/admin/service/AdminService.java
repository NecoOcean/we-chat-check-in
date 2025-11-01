package com.wechat.checkin.admin.service;

import com.wechat.checkin.admin.dto.AdminQueryRequest;
import com.wechat.checkin.admin.dto.CreateAdminRequest;
import com.wechat.checkin.admin.dto.UpdateAdminPasswordRequest;
import com.wechat.checkin.admin.dto.UpdateAdminRequest;
import com.wechat.checkin.admin.vo.AdminVO;
import com.wechat.checkin.common.response.PageResult;

/**
 * 管理员服务接口
 * 提供管理员账号的增删改查等功能
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public interface AdminService {

    /**
     * 创建管理员账号
     * 只有市级管理员可以创建县级管理员账号
     *
     * @param request 创建请求
     * @return 新创建的管理员ID
     */
    Long createAdmin(CreateAdminRequest request);

    /**
     * 更新管理员信息
     * 市级管理员可以更新任何账号，县级管理员不可更新其他账号
     *
     * @param adminId 管理员ID
     * @param request 更新请求
     */
    void updateAdmin(Long adminId, UpdateAdminRequest request);

    /**
     * 重置管理员密码
     * 市级管理员可以重置任何账号密码
     *
     * @param adminId 管理员ID
     * @param request 密码更新请求
     */
    void updateAdminPassword(Long adminId, UpdateAdminPasswordRequest request);

    /**
     * 启用管理员账号
     *
     * @param adminId 管理员ID
     */
    void enableAdmin(Long adminId);

    /**
     * 禁用管理员账号
     *
     * @param adminId 管理员ID
     */
    void disableAdmin(Long adminId);

    /**
     * 软删除管理员账号
     * 数据保留但不可登录
     *
     * @param adminId 管理员ID
     */
    void deleteAdmin(Long adminId);

    /**
     * 查询管理员详情
     *
     * @param adminId 管理员ID
     * @return 管理员信息
     */
    AdminVO getAdminDetail(Long adminId);

    /**
     * 分页查询管理员列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<AdminVO> listAdmins(AdminQueryRequest request);
}

