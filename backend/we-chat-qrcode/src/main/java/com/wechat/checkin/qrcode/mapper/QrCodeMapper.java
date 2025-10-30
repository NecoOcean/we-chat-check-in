package com.wechat.checkin.qrcode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.checkin.qrcode.entity.QrCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 二维码Mapper接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Mapper
public interface QrCodeMapper extends BaseMapper<QrCode> {
}

