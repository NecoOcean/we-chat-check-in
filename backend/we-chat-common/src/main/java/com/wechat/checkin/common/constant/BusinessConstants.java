package com.wechat.checkin.common.constant;

import com.wechat.checkin.common.enums.*;

/**
 * 业务常量定义
 * 重构后的版本，使用专门的枚举类替代字符串常量
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public class BusinessConstants {

    /**
     * 活动状态常量 - 使用ActivityStatusEnum
     * @deprecated 请使用 {@link ActivityStatusEnum}
     */
    @Deprecated
    public static class ActivityStatus {
        /** 草稿状态 */
        public static final String DRAFT = ActivityStatusEnum.DRAFT.getValue();
        /** 进行中 */
        public static final String ONGOING = ActivityStatusEnum.ONGOING.getValue();
        /** 已结束 */
        public static final String ENDED = ActivityStatusEnum.ENDED.getValue();
    }

    /**
     * 二维码类型常量 - 使用QrCodeTypeEnum
     * @deprecated 请使用 {@link QrCodeTypeEnum}
     */
    @Deprecated
    public static class QrCodeType {
        /** 签到二维码 */
        public static final String CHECKIN = QrCodeTypeEnum.CHECKIN.getValue();
        /** 评价二维码 */
        public static final String EVALUATION = QrCodeTypeEnum.EVALUATION.getValue();
    }

    /**
     * 二维码状态常量 - 使用QrCodeStatusEnum
     * @deprecated 请使用 {@link QrCodeStatusEnum}
     */
    @Deprecated
    public static class QrCodeStatus {
        /** 激活状态 */
        public static final String ACTIVE = QrCodeStatusEnum.ACTIVE.getValue();
        /** 非激活状态 */
        public static final String INACTIVE = QrCodeStatusEnum.INACTIVE.getValue();
    }

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

    /**
     * 签到状态常量 - 使用CheckInStatusEnum
     * @deprecated 请使用 {@link CheckInStatusEnum}
     */
    @Deprecated
    public static class CheckInStatus {
        /** 未签到 */
        public static final String NOT_CHECKED = CheckInStatusEnum.NOT_CHECKED.getValue();
        /** 已签到 */
        public static final String CHECKED = CheckInStatusEnum.CHECKED.getValue();
        /** 迟到签到 */
        public static final String LATE_CHECKED = CheckInStatusEnum.LATE_CHECKED.getValue();
        /** 签到异常 */
        public static final String ABNORMAL = CheckInStatusEnum.ABNORMAL.getValue();
    }

    /**
     * 通用状态常量 - 使用StatusEnum
     * @deprecated 请使用 {@link StatusEnum}
     */
    @Deprecated
    public static class Status {
        /** 启用 */
        public static final String ENABLED = StatusEnum.ENABLED.getValue();
        /** 禁用 */
        public static final String DISABLED = StatusEnum.DISABLED.getValue();
        /** 已删除 */
        public static final String DELETED = StatusEnum.DELETED.getValue();
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