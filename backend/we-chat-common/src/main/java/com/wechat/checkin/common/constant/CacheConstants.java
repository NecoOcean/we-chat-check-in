package com.wechat.checkin.common.constant;

/**
 * 缓存常量类
 * 定义各种缓存键和过期时间
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public class CacheConstants {

    /**
     * 缓存键分隔符
     */
    public static final String CACHE_KEY_SEPARATOR = ":";

    /**
     * 缓存键前缀
     */
    public static final String CACHE_PREFIX = "wechat_checkin";

    // ==================== 用户相关缓存 ====================

    /**
     * 用户信息缓存键前缀
     */
    public static final String USER_INFO_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "user_info" + CACHE_KEY_SEPARATOR;

    /**
     * 用户权限缓存键前缀
     */
    public static final String USER_PERMISSION_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "user_permission" + CACHE_KEY_SEPARATOR;

    /**
     * 用户角色缓存键前缀
     */
    public static final String USER_ROLE_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "user_role" + CACHE_KEY_SEPARATOR;

    // ==================== 认证相关缓存 ====================

    /**
     * JWT令牌黑名单缓存键前缀
     */
    public static final String JWT_BLACKLIST_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "jwt_blacklist" + CACHE_KEY_SEPARATOR;

    /**
     * 登录验证码缓存键前缀
     */
    public static final String LOGIN_CAPTCHA_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "login_captcha" + CACHE_KEY_SEPARATOR;

    /**
     * 短信验证码缓存键前缀
     */
    public static final String SMS_CODE_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "sms_code" + CACHE_KEY_SEPARATOR;

    /**
     * 登录失败次数缓存键前缀
     */
    public static final String LOGIN_FAIL_COUNT_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "login_fail_count" + CACHE_KEY_SEPARATOR;

    // ==================== 活动相关缓存 ====================

    /**
     * 活动信息缓存键前缀
     */
    public static final String ACTIVITY_INFO_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "activity_info" + CACHE_KEY_SEPARATOR;

    /**
     * 活动列表缓存键前缀
     */
    public static final String ACTIVITY_LIST_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "activity_list" + CACHE_KEY_SEPARATOR;

    /**
     * 活动统计缓存键前缀
     */
    public static final String ACTIVITY_STATS_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "activity_stats" + CACHE_KEY_SEPARATOR;

    // ==================== 二维码相关缓存 ====================

    /**
     * 二维码信息缓存键前缀
     */
    public static final String QRCODE_INFO_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "qrcode_info" + CACHE_KEY_SEPARATOR;

    /**
     * 二维码签名缓存键前缀
     */
    public static final String QRCODE_SIGNATURE_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "qrcode_signature" + CACHE_KEY_SEPARATOR;

    // ==================== 打卡相关缓存 ====================

    /**
     * 打卡记录缓存键前缀
     */
    public static final String CHECKIN_RECORD_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "checkin_record" + CACHE_KEY_SEPARATOR;

    /**
     * 打卡幂等缓存键前缀
     */
    public static final String CHECKIN_IDEMPOTENT_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "checkin_idempotent" + CACHE_KEY_SEPARATOR;

    /**
     * 用户今日打卡状态缓存键前缀
     */
    public static final String USER_TODAY_CHECKIN_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "user_today_checkin" + CACHE_KEY_SEPARATOR;

    // ==================== 评价相关缓存 ====================

    /**
     * 评价记录缓存键前缀
     */
    public static final String EVALUATION_RECORD_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "evaluation_record" + CACHE_KEY_SEPARATOR;

    /**
     * 评价统计缓存键前缀
     */
    public static final String EVALUATION_STATS_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "evaluation_stats" + CACHE_KEY_SEPARATOR;

    // ==================== 系统相关缓存 ====================

    /**
     * 系统配置缓存键前缀
     */
    public static final String SYSTEM_CONFIG_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "system_config" + CACHE_KEY_SEPARATOR;

    /**
     * 字典数据缓存键前缀
     */
    public static final String DICT_DATA_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "dict_data" + CACHE_KEY_SEPARATOR;

    /**
     * 接口限流缓存键前缀
     */
    public static final String RATE_LIMIT_KEY = CACHE_PREFIX + CACHE_KEY_SEPARATOR + "rate_limit" + CACHE_KEY_SEPARATOR;

    // ==================== 缓存过期时间（秒） ====================

    /**
     * 默认缓存过期时间（30分钟）
     */
    public static final long DEFAULT_EXPIRE_TIME = 30 * 60;

    /**
     * 短期缓存过期时间（5分钟）
     */
    public static final long SHORT_EXPIRE_TIME = 5 * 60;

    /**
     * 长期缓存过期时间（2小时）
     */
    public static final long LONG_EXPIRE_TIME = 2 * 60 * 60;

    /**
     * 用户信息缓存过期时间（1小时）
     */
    public static final long USER_INFO_EXPIRE_TIME = 60 * 60;

    /**
     * JWT令牌黑名单缓存过期时间（24小时）
     */
    public static final long JWT_BLACKLIST_EXPIRE_TIME = 24 * 60 * 60;

    /**
     * 验证码缓存过期时间（5分钟）
     */
    public static final long CAPTCHA_EXPIRE_TIME = 5 * 60;

    /**
     * 短信验证码缓存过期时间（10分钟）
     */
    public static final long SMS_CODE_EXPIRE_TIME = 10 * 60;

    /**
     * 登录失败次数缓存过期时间（30分钟）
     */
    public static final long LOGIN_FAIL_COUNT_EXPIRE_TIME = 30 * 60;

    /**
     * 活动信息缓存过期时间（1小时）
     */
    public static final long ACTIVITY_INFO_EXPIRE_TIME = 60 * 60;

    /**
     * 二维码信息缓存过期时间（30分钟）
     */
    public static final long QRCODE_INFO_EXPIRE_TIME = 30 * 60;

    /**
     * 二维码签名缓存过期时间（10分钟）
     */
    public static final long QRCODE_SIGNATURE_EXPIRE_TIME = 10 * 60;

    /**
     * 打卡幂等缓存过期时间（1小时）
     */
    public static final long CHECKIN_IDEMPOTENT_EXPIRE_TIME = 60 * 60;

    /**
     * 用户今日打卡状态缓存过期时间（到当天结束）
     */
    public static final long USER_TODAY_CHECKIN_EXPIRE_TIME = 24 * 60 * 60;

    /**
     * 系统配置缓存过期时间（6小时）
     */
    public static final long SYSTEM_CONFIG_EXPIRE_TIME = 6 * 60 * 60;

    /**
     * 字典数据缓存过期时间（12小时）
     */
    public static final long DICT_DATA_EXPIRE_TIME = 12 * 60 * 60;

    /**
     * 接口限流缓存过期时间（1分钟）
     */
    public static final long RATE_LIMIT_EXPIRE_TIME = 60;

    // ==================== 工具方法 ====================

    /**
     * 构建缓存键
     */
    public static String buildKey(String prefix, String... keys) {
        StringBuilder sb = new StringBuilder(prefix);
        for (String key : keys) {
            sb.append(key);
        }
        return sb.toString();
    }

    /**
     * 构建用户信息缓存键
     */
    public static String buildUserInfoKey(Long userId) {
        return buildKey(USER_INFO_KEY, String.valueOf(userId));
    }

    /**
     * 构建用户权限缓存键
     */
    public static String buildUserPermissionKey(Long userId) {
        return buildKey(USER_PERMISSION_KEY, String.valueOf(userId));
    }

    /**
     * 构建JWT黑名单缓存键
     */
    public static String buildJwtBlacklistKey(String jti) {
        return buildKey(JWT_BLACKLIST_KEY, jti);
    }

    /**
     * 构建短信验证码缓存键
     */
    public static String buildSmsCodeKey(String mobile) {
        return buildKey(SMS_CODE_KEY, mobile);
    }

    /**
     * 构建登录失败次数缓存键
     */
    public static String buildLoginFailCountKey(String username) {
        return buildKey(LOGIN_FAIL_COUNT_KEY, username);
    }

    /**
     * 构建活动信息缓存键
     */
    public static String buildActivityInfoKey(Long activityId) {
        return buildKey(ACTIVITY_INFO_KEY, String.valueOf(activityId));
    }

    /**
     * 构建二维码信息缓存键
     */
    public static String buildQrCodeInfoKey(String qrCodeId) {
        return buildKey(QRCODE_INFO_KEY, qrCodeId);
    }

    /**
     * 构建打卡幂等缓存键
     */
    public static String buildCheckinIdempotentKey(Long userId, Long activityId, String date) {
        return buildKey(CHECKIN_IDEMPOTENT_KEY, String.valueOf(userId), CACHE_KEY_SEPARATOR, 
                       String.valueOf(activityId), CACHE_KEY_SEPARATOR, date);
    }

    /**
     * 构建用户今日打卡状态缓存键
     */
    public static String buildUserTodayCheckinKey(Long userId, String date) {
        return buildKey(USER_TODAY_CHECKIN_KEY, String.valueOf(userId), CACHE_KEY_SEPARATOR, date);
    }
}