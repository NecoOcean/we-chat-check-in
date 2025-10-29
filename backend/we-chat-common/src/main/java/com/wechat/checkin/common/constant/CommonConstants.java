package com.wechat.checkin.common.constant;

import com.wechat.checkin.common.enums.UserRoleEnum;

/**
 * 通用常量类
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public final class CommonConstants {

    private CommonConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== 通用常量 ====================

    /**
     * 成功标识
     */
    public static final String SUCCESS = "success";

    /**
     * 失败标识
     */
    public static final String FAIL = "fail";

    /**
     * 是
     */
    public static final String YES = "Y";

    /**
     * 否
     */
    public static final String NO = "N";

    /**
     * 启用状态
     */
    public static final Integer ENABLED = 1;

    /**
     * 禁用状态
     */
    public static final Integer DISABLED = 0;

    /**
     * 删除标识
     */
    public static final Integer DELETED = 1;

    /**
     * 未删除标识
     */
    public static final Integer NOT_DELETED = 0;

    // ==================== 字符常量 ====================

    /**
     * 空字符串
     */
    public static final String EMPTY_STRING = "";

    /**
     * 逗号分隔符
     */
    public static final String COMMA = ",";

    /**
     * 点分隔符
     */
    public static final String DOT = ".";

    /**
     * 下划线分隔符
     */
    public static final String UNDERSCORE = "_";

    /**
     * 中划线分隔符
     */
    public static final String HYPHEN = "-";

    /**
     * 斜杠分隔符
     */
    public static final String SLASH = "/";

    /**
     * 冒号分隔符
     */
    public static final String COLON = ":";

    // ==================== 数字常量 ====================

    /**
     * 零
     */
    public static final Integer ZERO = 0;

    /**
     * 一
     */
    public static final Integer ONE = 1;

    /**
     * 负一
     */
    public static final Integer MINUS_ONE = -1;

    // ==================== 时间常量 ====================

    /**
     * 一秒的毫秒数
     */
    public static final long SECOND_IN_MILLIS = 1000L;

    /**
     * 一分钟的毫秒数
     */
    public static final long MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS;

    /**
     * 一小时的毫秒数
     */
    public static final long HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS;

    /**
     * 一天的毫秒数
     */
    public static final long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;

    // ==================== 编码常量 ====================

    /**
     * UTF-8编码
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK编码
     */
    public static final String GBK = "GBK";

    // ==================== HTTP常量 ====================

    /**
     * HTTP GET方法
     */
    public static final String HTTP_GET = "GET";

    /**
     * HTTP POST方法
     */
    public static final String HTTP_POST = "POST";

    /**
     * HTTP PUT方法
     */
    public static final String HTTP_PUT = "PUT";

    /**
     * HTTP DELETE方法
     */
    public static final String HTTP_DELETE = "DELETE";

    /**
     * Content-Type: application/json
     */
    public static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * Content-Type: application/x-www-form-urlencoded
     */
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    // ==================== 缓存常量 ====================

    /**
     * 默认缓存过期时间（秒）
     */
    public static final long DEFAULT_CACHE_EXPIRE = 3600L;

    /**
     * 短期缓存过期时间（秒）
     */
    public static final long SHORT_CACHE_EXPIRE = 300L;

    /**
     * 长期缓存过期时间（秒）
     */
    public static final long LONG_CACHE_EXPIRE = 86400L;

    // ==================== 分页常量 ====================

    /**
     * 默认页码
     */
    public static final long DEFAULT_PAGE_NUM = 1L;

    /**
     * 默认页面大小
     */
    public static final long DEFAULT_PAGE_SIZE = 10L;

    /**
     * 最大页面大小
     */
    public static final long MAX_PAGE_SIZE = 1000L;

    // ==================== 用户角色常量 ====================

    /**
     * 用户角色常量 - 使用UserRoleEnum
     * @deprecated 请使用 {@link UserRoleEnum}
     */
    @Deprecated
    public static class UserRole {
        /** 市级管理员 */
        public static final String CITY_ADMIN = UserRoleEnum.CITY.getValue();
        /** 县级管理员 */
        public static final String COUNTY_ADMIN = UserRoleEnum.COUNTY.getValue();
        /** 普通用户 */
        public static final String USER = UserRoleEnum.USER.getValue();
    }

    // ==================== 请求属性常量 ====================

    /**
     * 县域代码请求属性名
     */
    public static final String COUNTY_CODE_ATTR = "currentCountyCode";
}