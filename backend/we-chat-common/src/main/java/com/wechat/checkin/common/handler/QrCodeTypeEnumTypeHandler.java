package com.wechat.checkin.common.handler;

import com.wechat.checkin.common.enums.QrCodeTypeEnum;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * QrCodeTypeEnum 类型处理器
 * 继承通用枚举类型处理器基类
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@MappedTypes(QrCodeTypeEnum.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class QrCodeTypeEnumTypeHandler extends BaseValueEnumTypeHandler<QrCodeTypeEnum> {

    public QrCodeTypeEnumTypeHandler() {
        super(QrCodeTypeEnum.class);
    }
}