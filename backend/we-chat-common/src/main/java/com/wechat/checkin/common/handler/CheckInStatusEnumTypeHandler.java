package com.wechat.checkin.common.handler;

import com.wechat.checkin.common.enums.CheckInStatusEnum;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * CheckInStatusEnum 类型处理器
 * 继承通用枚举类型处理器基类
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@MappedTypes(CheckInStatusEnum.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class CheckInStatusEnumTypeHandler extends BaseValueEnumTypeHandler<CheckInStatusEnum> {

    public CheckInStatusEnumTypeHandler() {
        super(CheckInStatusEnum.class);
    }
}