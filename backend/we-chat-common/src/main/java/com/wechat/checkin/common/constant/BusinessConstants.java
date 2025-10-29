package com.wechat.checkin.common.constant;

/**
 * 业务常量定义
 * 包含活动状态、二维码类型、角色权限等业务相关常量
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public class BusinessConstants {

    /**
     * 活动状态常量
     */
    public static class ActivityStatus {
        /** 草稿状态 */
        public static final String DRAFT = "DRAFT";
        /** 已发布 */
        public static final String PUBLISHED = "PUBLISHED";
        /** 进行中 */
        public static final String ACTIVE = "ACTIVE";
        /** 已结束 */
        public static final String ENDED = "ENDED";
        /** 已取消 */
        public static final String CANCELLED = "CANCELLED";
        /** 已暂停 */
        public static final String PAUSED = "PAUSED";
    }

    /**
     * 二维码类型常量
     */
    public static class QrCodeType {
        /** 活动二维码 */
        public static final String ACTIVITY = "ACTIVITY";
        /** 签到二维码 */
        public static final String CHECKIN = "CHECKIN";
        /** 临时二维码 */
        public static final String TEMPORARY = "TEMPORARY";
        /** 永久二维码 */
        public static final String PERMANENT = "PERMANENT";
    }

    /**
     * 用户角色常量
     */
    public static class UserRole {
        /** 市级管理员 */
        public static final String CITY_ADMIN = "CITY_ADMIN";
        /** 县级管理员 */
        public static final String COUNTY_ADMIN = "COUNTY_ADMIN";
        /** 普通用户 */
        public static final String USER = "USER";
    }

    /**
     * 权限常量
     */
    public static class Permission {
        /** 活动管理权限 */
        public static final String ACTIVITY_MANAGE = "activity:manage";
        /** 活动查看权限 */
        public static final String ACTIVITY_VIEW = "activity:view";
        /** 用户管理权限 */
        public static final String USER_MANAGE = "user:manage";
        /** 数据统计权限 */
        public static final String DATA_STATISTICS = "data:statistics";
        /** 系统配置权限 */
        public static final String SYSTEM_CONFIG = "system:config";
    }

    /**
     * 签到状态常量
     */
    public static class CheckInStatus {
        /** 未签到 */
        public static final String NOT_CHECKED = "NOT_CHECKED";
        /** 已签到 */
        public static final String CHECKED = "CHECKED";
        /** 迟到签到 */
        public static final String LATE_CHECKED = "LATE_CHECKED";
        /** 签到异常 */
        public static final String ABNORMAL = "ABNORMAL";
    }

    /**
     * 通知类型常量
     */
    public static class NotificationType {
        /** 活动通知 */
        public static final String ACTIVITY = "ACTIVITY";
        /** 系统通知 */
        public static final String SYSTEM = "SYSTEM";
        /** 签到提醒 */
        public static final String CHECKIN_REMINDER = "CHECKIN_REMINDER";
        /** 活动开始提醒 */
        public static final String ACTIVITY_START = "ACTIVITY_START";
        /** 活动结束提醒 */
        public static final String ACTIVITY_END = "ACTIVITY_END";
    }

    /**
     * 文件类型常量
     */
    public static class FileType {
        /** 图片文件 */
        public static final String IMAGE = "IMAGE";
        /** 文档文件 */
        public static final String DOCUMENT = "DOCUMENT";
        /** 视频文件 */
        public static final String VIDEO = "VIDEO";
        /** 音频文件 */
        public static final String AUDIO = "AUDIO";
        /** 其他文件 */
        public static final String OTHER = "OTHER";
    }

    /**
     * 操作类型常量
     */
    public static class OperationType {
        /** 创建操作 */
        public static final String CREATE = "CREATE";
        /** 更新操作 */
        public static final String UPDATE = "UPDATE";
        /** 删除操作 */
        public static final String DELETE = "DELETE";
        /** 查询操作 */
        public static final String SELECT = "SELECT";
        /** 登录操作 */
        public static final String LOGIN = "LOGIN";
        /** 登出操作 */
        public static final String LOGOUT = "LOGOUT";
        /** 导出操作 */
        public static final String EXPORT = "EXPORT";
        /** 导入操作 */
        public static final String IMPORT = "IMPORT";
    }

    /**
     * 业务模块常量
     */
    public static class BusinessModule {
        /** 认证模块 */
        public static final String AUTH = "AUTH";
        /** 活动模块 */
        public static final String ACTIVITY = "ACTIVITY";
        /** 签到模块 */
        public static final String CHECKIN = "CHECKIN";
        /** 用户模块 */
        public static final String USER = "USER";
        /** 系统模块 */
        public static final String SYSTEM = "SYSTEM";
        /** 统计模块 */
        public static final String STATISTICS = "STATISTICS";
    }

    /**
     * 缓存键前缀常量
     */
    public static class CachePrefix {
        /** 用户信息缓存 */
        public static final String USER_INFO = "user:info:";
        /** 活动信息缓存 */
        public static final String ACTIVITY_INFO = "activity:info:";
        /** 二维码缓存 */
        public static final String QR_CODE = "qrcode:";
        /** 签到记录缓存 */
        public static final String CHECKIN_RECORD = "checkin:record:";
        /** 统计数据缓存 */
        public static final String STATISTICS = "statistics:";
        /** 验证码缓存 */
        public static final String VERIFICATION_CODE = "verify:code:";
    }

    /**
     * 时间相关常量
     */
    public static class TimeConstants {
        /** 一分钟的秒数 */
        public static final int MINUTE_SECONDS = 60;
        /** 一小时的秒数 */
        public static final int HOUR_SECONDS = 3600;
        /** 一天的秒数 */
        public static final int DAY_SECONDS = 86400;
        /** 一周的秒数 */
        public static final int WEEK_SECONDS = 604800;
        /** 一个月的秒数（30天） */
        public static final int MONTH_SECONDS = 2592000;
    }

    /**
     * 默认值常量
     */
    public static class DefaultValues {
        /** 默认页码 */
        public static final int DEFAULT_PAGE_NUM = 1;
        /** 默认页面大小 */
        public static final int DEFAULT_PAGE_SIZE = 10;
        /** 最大页面大小 */
        public static final int MAX_PAGE_SIZE = 100;
        /** 默认排序字段 */
        public static final String DEFAULT_SORT_FIELD = "created_at";
        /** 默认排序方向 */
        public static final String DEFAULT_SORT_ORDER = "DESC";
    }
}