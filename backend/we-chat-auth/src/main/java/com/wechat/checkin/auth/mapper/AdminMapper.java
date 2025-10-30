package com.wechat.checkin.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.checkin.auth.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 管理员Mapper接口
 * 继承MyBatis Plus的BaseMapper，自动拥有CRUD能力
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    // BaseMapper 已提供以下常用方法：
    // - selectById(Long id): 根据ID查询
    // - selectOne(Wrapper wrapper): 条件查询单个
    // - selectList(Wrapper wrapper): 条件查询列表
    // - insert(Admin entity): 插入实体
    // - updateById(Admin entity): 根据ID更新
    // 推荐使用 LambdaQueryWrapper 进行类型安全的条件查询

    /**
     * 更新最后登录信息
     * 注意：数据库表中已删除last_login_time和last_login_ip字段，此方法保留但不实际使用
     * 建议：在审计日志中记录登录信息
     *
     * @param id 管理员ID
     * @param lastLoginTime 最后登录时间
     * @param lastLoginIp 最后登录IP
     * @return 更新行数
     * @deprecated 数据库表中已删除相关字段，建议使用审计日志记录登录信息
     */
    @Deprecated
    default int updateLastLoginInfo(Long id, LocalDateTime lastLoginTime, String lastLoginIp) {
        // 由于字段已删除，仅保留方法签名，实际不执行任何操作
        return 0;
    }

    /**
     * 增加登录次数
     * 注意：数据库表中已删除login_count字段，此方法保留但不实际使用
     *
     * @param id 管理员ID
     * @return 更新行数
     * @deprecated 数据库表中已删除相关字段，建议使用审计日志记录登录信息
     */
    @Deprecated
    default int incrementLoginCount(Long id) {
        // 由于字段已删除，仅保留方法签名，实际不执行任何操作
        return 0;
    }
}