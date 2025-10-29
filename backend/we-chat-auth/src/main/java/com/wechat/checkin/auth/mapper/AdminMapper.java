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
    @Select("SELECT * FROM admins WHERE username = #{username} AND deleted = 0")
    Admin findByUsername(@Param("username") String username);

    /**
     * 更新最后登录信息
     *
     * @param id 管理员ID
     * @param lastLoginTime 最后登录时间
     * @param lastLoginIp 最后登录IP
     * @return 更新行数
     */
    @Update("UPDATE admins SET last_login_time = #{lastLoginTime}, last_login_ip = #{lastLoginIp}, updated_at = NOW() WHERE id = #{id}")
    int updateLastLoginInfo(@Param("id") Long id, 
                           @Param("lastLoginTime") LocalDateTime lastLoginTime, 
                           @Param("lastLoginIp") String lastLoginIp);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM admins WHERE username = #{username} AND deleted = 0")
    int countByUsername(@Param("username") String username);

    /**
     * 检查县域代码下的管理员数量
     *
     * @param countyCode 县域代码
     * @return 管理员数量
     */
    @Select("SELECT COUNT(*) FROM admins WHERE county_code = #{countyCode} AND deleted = 0")
    int countByCountyCode(@Param("countyCode") String countyCode);

    /**
     * 增加登录次数
     *
     * @param id 管理员ID
     * @return 更新行数
     */
    @Update("UPDATE admins SET login_count = login_count + 1, updated_at = NOW() WHERE id = #{id}")
    int incrementLoginCount(@Param("id") Long id);
}