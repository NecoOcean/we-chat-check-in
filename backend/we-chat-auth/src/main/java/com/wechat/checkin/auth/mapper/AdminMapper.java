package com.wechat.checkin.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.checkin.auth.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * 管理员Mapper接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 根据用户名查找管理员
     *
     * @param username 用户名
     * @return 管理员信息
     */
    @Select("SELECT * FROM admins WHERE username = #{username}")
    Admin findByUsername(@Param("username") String username);

    /**
     * 更新最后登录信息
     * 注意：数据库表中已删除last_login_time和last_login_ip字段，此方法保留但不实际使用
     * 建议：在审计日志中记录登录信息
     *
     * @param id 管理员ID
     * @param lastLoginTime 最后登录时间
     * @param lastLoginIp 最后登录IP
     * @return 更新行数
     * @deprecated 数据库表中已删除相关字段
     */
    @Deprecated
    @Update("UPDATE admins SET updated_time = NOW() WHERE id = #{id}")
    int updateLastLoginInfo(@Param("id") Long id, 
                           @Param("lastLoginTime") LocalDateTime lastLoginTime, 
                           @Param("lastLoginIp") String lastLoginIp);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM admins WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    /**
     * 根据县级代码计算管理员数量
     *
     * @param countyCode 县级代码
     * @return 匹配的管理员数量
     */
    @Select("SELECT COUNT(*) FROM admins WHERE county_code = #{countyCode}")
    int countByCountyCode(@Param("countyCode") String countyCode);

    /**
     * 增加登录次数
     * 注意：数据库表中已删除login_count字段，此方法保留但不实际使用
     *
     * @param id 管理员ID
     * @return 更新行数
     * @deprecated 数据库表中已删除相关字段
     */
    @Deprecated
    @Update("UPDATE admins SET updated_time = NOW() WHERE id = #{id}")
    int incrementLoginCount(@Param("id") Long id);
}