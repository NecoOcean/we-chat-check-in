package com.wechat.checkin.teachingpoint.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wechat.checkin.common.enums.StatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教学点实体类
 * 对应数据库表: teaching_points
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@TableName("teaching_points")
public class TeachingPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 教学点ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 教学点名称
     */
    @TableField("name")
    private String name;

    /**
     * 归属县域编码
     */
    @TableField("county_code")
    private String countyCode;

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

