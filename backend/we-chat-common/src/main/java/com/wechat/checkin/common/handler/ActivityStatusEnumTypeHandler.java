package com.wechat.checkin.common.handler;

import com.wechat.checkin.common.enums.ActivityStatusEnum;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * ActivityStatusEnum 类型处理器
 * 继承通用枚举类型处理器基类
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@MappedTypes(ActivityStatusEnum.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ActivityStatusEnumTypeHandler extends BaseValueEnumTypeHandler<ActivityStatusEnum> {

    public ActivityStatusEnumTypeHandler() {
        super(ActivityStatusEnum.class);
    }
}