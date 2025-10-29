package com.wechat.checkin.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wechat.checkin.common.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 管理员实体类
 * 对应数据库表：admins
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("admins")
public class Admin {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（登录账号）
     */
    @TableField("username")
    private String username;

    /**
     * 密码哈希值
     */
    @TableField("password")
    private String password;

    /**
     * 角色类型：city-市级管理员，county-县级管理员
     */
    @TableField("role")
    private String role;

    /**
     * 县级代码（县级管理员必填，市级管理员为空）
     */
    @TableField("county_code")
    private String countyCode;

    /**
     * 账号状态：enabled-启用，disabled-禁用，deleted-软删除
     */
    @TableField("status")
    private StatusEnum status;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

}