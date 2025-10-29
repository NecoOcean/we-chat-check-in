package com.wechat.checkin.common.handler;

import com.wechat.checkin.common.enums.StatusEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * StatusEnum 类型处理器
 * 用于 MyBatis 在数据库字符串值和 StatusEnum 枚举之间进行转换
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@MappedTypes(StatusEnum.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class StatusEnumTypeHandler extends BaseTypeHandler<StatusEnum> {

    /**
     * 设置参数：将枚举值转换为数据库字符串
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, StatusEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    /**
     * 获取结果：将数据库字符串转换为枚举值
     */
    @Override
    public StatusEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : StatusEnum.getByValue(value);
    }

    /**
     * 获取结果：将数据库字符串转换为枚举值（通过列索引）
     */
    @Override
    public StatusEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : StatusEnum.getByValue(value);
    }

    /**
     * 获取结果：将数据库字符串转换为枚举值（存储过程调用）
     */
    @Override
    public StatusEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : StatusEnum.getByValue(value);
    }
}