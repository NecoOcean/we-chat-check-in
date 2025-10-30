package com.wechat.checkin.qrcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wechat.checkin.common.enums.QrCodeStatusEnum;
import com.wechat.checkin.common.enums.QrCodeTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 二维码实体类
 * 对应数据库表：qrcodes
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("qrcodes")
public class QrCode {

    /**
     * 二维码ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联活动ID
     */
    @TableField("activity_id")
    private Long activityId;

    /**
     * 二维码类型：checkin-打卡，evaluation-评价
     */
    @TableField("type")
    private QrCodeTypeEnum type;

    /**
     * 二维码令牌（含签名信息）
     */
    @TableField("token")
    private String token;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 禁用时间
     */
    @TableField("disabled_time")
    private LocalDateTime disabledTime;

    /**
     * 二维码状态：enabled-启用，disabled-禁用，deleted-软删除
     */
    @TableField("status")
    private QrCodeStatusEnum status;

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

