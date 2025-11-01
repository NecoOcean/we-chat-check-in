package com.wechat.checkin.county.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.checkin.county.entity.County;
import org.apache.ibatis.annotations.Mapper;

/**
 * 县域数据访问层
 * 
 * @author WeChat Check-in System
 * @since 1.3.0
 */
@Mapper
public interface CountyMapper extends BaseMapper<County> {
    // 基础CRUD操作由BaseMapper提供
    // 如需自定义SQL查询，可在此添加方法
}

