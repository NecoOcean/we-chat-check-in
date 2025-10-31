package com.wechat.checkin.checkins.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.checkin.checkins.entity.Checkin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打卡数据访问接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Mapper
public interface CheckinMapper extends BaseMapper<Checkin> {
}
