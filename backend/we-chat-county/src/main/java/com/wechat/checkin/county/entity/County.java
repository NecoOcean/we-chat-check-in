package com.wechat.checkin.county.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wechat.checkin.common.enums.StatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 县域实体类
 * 对应数据库表: counties
 * 
 * @author WeChat Check-in System
 * @since 1.3.0
 */
@Data
@TableName("counties")
public class County implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 县域编码（主键）
     * 如：PX（萍乡）、LC（莲花）
     */
    @TableId(value = "code", type = IdType.INPUT)
    private String code;

    /**
     * 县域名称
     */
    @TableField("name")
    private String name;

    /**
     * 状态：启用/禁用/软删除
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

