package com.wechat.checkin.common.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 通用枚举类型处理器基类
 * 支持所有具有 getValue() 和 getByValue(String) 方法的枚举类型
 *
 * @param <E> 枚举类型
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public abstract class BaseValueEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private final Class<E> enumClass;
    private final Method getValueMethod;
    private final Method getByValueMethod;

    public BaseValueEnumTypeHandler(Class<E> enumClass) {
        if (enumClass == null) {
            throw new IllegalArgumentException("枚举类型不能为空");
        }
        this.enumClass = enumClass;
        
        try {
            // 获取 getValue() 方法
            this.getValueMethod = enumClass.getMethod("getValue");
            // 获取 getByValue(String) 静态方法
            this.getByValueMethod = enumClass.getMethod("getByValue", String.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                String.format("枚举类 %s 必须包含 getValue() 实例方法和 getByValue(String) 静态方法", 
                enumClass.getSimpleName()), e);
        }
    }

    /**
     * 设置参数：将枚举值转换为数据库字符串
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        try {
            String value = (String) getValueMethod.invoke(parameter);
            ps.setString(i, value);
        } catch (Exception e) {
            throw new SQLException("设置枚举参数失败: " + parameter, e);
        }
    }

    /**
     * 获取结果：将数据库字符串转换为枚举值
     */
    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return convertToEnum(value);
    }

    /**
     * 获取结果：将数据库字符串转换为枚举值（通过列索引）
     */
    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return convertToEnum(value);
    }

    /**
     * 获取结果：将数据库字符串转换为枚举值（存储过程调用）
     */
    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return convertToEnum(value);
    }

    /**
     * 将字符串值转换为枚举
     */
    @SuppressWarnings("unchecked")
    private E convertToEnum(String value) throws SQLException {
        if (value == null) {
            return null;
        }
        
        try {
            return (E) getByValueMethod.invoke(null, value);
        } catch (Exception e) {
            throw new SQLException(
                String.format("无法将值 '%s' 转换为枚举类型 %s", value, enumClass.getSimpleName()), e);
        }
    }
}