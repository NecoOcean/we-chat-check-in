package com.wechat.checkin.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),
    VALIDATION_ERROR(422, "参数校验失败"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    // 服务器错误 5xx
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    BAD_GATEWAY(502, "网关错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),

    // 业务错误 1xxx
    BUSINESS_ERROR(1000, "业务处理失败"),
    SYSTEM_ERROR(1001, "系统错误"),
    PARAM_ERROR(1002, "参数错误"),
    
    // 用户相关错误 11xx
    USER_NOT_FOUND(1101, "用户不存在"),
    USER_DISABLED(1102, "用户已被禁用"),
    USER_ALREADY_EXISTS(1103, "用户已存在"),
    PASSWORD_ERROR(1104, "密码错误"),
    USERNAME_EXISTS(1105, "用户名已存在"),
    ADMIN_NOT_FOUND(1106, "管理员不存在"),
    ADMIN_DELETED(1107, "管理员已被删除"),
    OPERATION_FAILED(1108, "操作失败"),
    COUNTY_CODE_NOT_FOUND(1109, "县域代码不存在"),
    
    // 认证授权错误 12xx
    TOKEN_INVALID(1201, "令牌无效"),
    TOKEN_EXPIRED(1202, "令牌已过期"),
    TOKEN_MISSING(1203, "令牌缺失"),
    PERMISSION_DENIED(1204, "权限不足"),
    LOGIN_REQUIRED(1205, "请先登录"),
    
    // 活动相关错误 13xx
    ACTIVITY_NOT_FOUND(1301, "活动不存在"),
    ACTIVITY_NOT_STARTED(1302, "活动未开始"),
    ACTIVITY_ENDED(1303, "活动已结束"),
    ACTIVITY_FULL(1304, "活动人数已满"),
    ACTIVITY_DISABLED(1305, "活动已禁用"),
    
    // 打卡相关错误 14xx
    CHECKIN_NOT_FOUND(1401, "打卡记录不存在"),
    CHECKIN_ALREADY_EXISTS(1402, "今日已打卡"),
    CHECKIN_TIME_INVALID(1403, "打卡时间无效"),
    CHECKIN_LOCATION_INVALID(1404, "打卡位置无效"),
    
    // 二维码相关错误 15xx
    QRCODE_INVALID(1501, "二维码无效"),
    QRCODE_EXPIRED(1502, "二维码已过期"),
    QRCODE_USED(1503, "二维码已使用"),
    QRCODE_GENERATE_FAILED(1504, "二维码生成失败"),
    
    // 评价相关错误 16xx
    EVALUATION_NOT_FOUND(1601, "评价不存在"),
    EVALUATION_ALREADY_EXISTS(1602, "已评价过该活动"),
    EVALUATION_NOT_ALLOWED(1603, "不允许评价"),
    
    // 数据相关错误 17xx
    DATA_NOT_FOUND(1701, "数据不存在"),
    DATA_ALREADY_EXISTS(1702, "数据已存在"),
    DATA_INTEGRITY_VIOLATION(1703, "数据完整性约束违反"),
    
    // 教学点相关错误 18xx（原18xx文件相关错误移至19xx）
    TEACHING_POINT_NOT_FOUND(1801, "教学点不存在"),
    TEACHING_POINT_ALREADY_EXISTS(1802, "教学点已存在"),
    TEACHING_POINT_NAME_EXISTS(1803, "教学点名称已存在"),
    TEACHING_POINT_DELETED(1804, "教学点已被删除"),
    
    // 文件相关错误 19xx（原18xx移至19xx）
    FILE_NOT_FOUND(1901, "文件不存在"),
    FILE_UPLOAD_FAILED(1902, "文件上传失败"),
    FILE_TYPE_NOT_SUPPORTED(1903, "文件类型不支持"),
    FILE_SIZE_EXCEEDED(1904, "文件大小超出限制"),
    
    // 外部服务错误 20xx（原19xx移至20xx）
    WECHAT_API_ERROR(2001, "微信接口调用失败"),
    SMS_SEND_FAILED(2002, "短信发送失败"),
    EMAIL_SEND_FAILED(2003, "邮件发送失败");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;
}