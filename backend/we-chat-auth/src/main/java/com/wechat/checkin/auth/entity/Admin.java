package com.wechat.checkin.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wechat.checkin.common.enums.DeletedEnum;
import com.wechat.checkin.common.enums.StatusEnum;
import com.wechat.checkin.common.enums.UserRoleEnum;
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
    @TableField("password_hash")
    private String passwordHash;

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
     * 账号状态
     */
    @TableField("status")
    private StatusEnum status;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 删除标记
     */
    @TableField("deleted")
    @TableLogic
    private DeletedEnum deleted;

    /**
     * 创建者ID
     */
    @TableField("created_by")
    private Long createdBy;

    /**
     * 更新者ID
     */
    @TableField("updated_by")
    private Long updatedBy;

    /**
     * 角色常量
     * @deprecated 请使用 {@link UserRoleEnum}
     */
    @Deprecated
    public static final String ROLE_CITY = UserRoleEnum.CITY.getValue();
    @Deprecated
    public static final String ROLE_COUNTY = UserRoleEnum.COUNTY.getValue();
}