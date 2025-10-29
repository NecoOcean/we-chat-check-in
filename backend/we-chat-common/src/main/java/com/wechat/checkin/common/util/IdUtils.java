package com.wechat.checkin.common.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

import java.util.concurrent.ThreadLocalRandom;

import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.ResultCode;

/**
 * ID生成工具类
 * 提供各种ID生成方法
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public class IdUtils {

    /**
     * 雪花算法实例（数据中心ID=1，机器ID=1）
     */
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    /**
     * 生成雪花算法ID
     */
    public static long snowflakeId() {
        return SNOWFLAKE.nextId();
    }

    /**
     * 生成雪花算法ID字符串
     */
    public static String snowflakeIdStr() {
        return String.valueOf(snowflakeId());
    }

    /**
     * 生成UUID（带连字符）
     */
    public static String uuid() {
        return IdUtil.randomUUID();
    }

    /**
     * 生成简化UUID（不带连字符）
     */
    public static String simpleUuid() {
        return IdUtil.simpleUUID();
    }

    /**
     * 生成ObjectId（MongoDB风格）
     */
    public static String objectId() {
        return IdUtil.objectId();
    }

    /**
     * 生成指定长度的随机数字ID
     */
    public static String randomNumericId(int length) {
        if (length <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "Length must be positive");
        }
        
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        // 第一位不能为0
        sb.append(random.nextInt(1, 10));
        
        // 其余位可以为0-9
        for (int i = 1; i < length; i++) {
            sb.append(random.nextInt(0, 10));
        }
        
        return sb.toString();
    }

    /**
     * 生成指定长度的随机字母数字ID
     */
    public static String randomAlphaNumericId(int length) {
        if (length <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "Length must be positive");
        }
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }

    /**
     * 生成订单号（格式：yyyyMMddHHmmss + 6位随机数）
     */
    public static String generateOrderNo() {
        String timestamp = DateUtils.format(DateUtils.now(), "yyyyMMddHHmmss");
        String randomNum = randomNumericId(6);
        return timestamp + randomNum;
    }

    /**
     * 生成活动编号（格式：ACT + yyyyMMdd + 6位随机数）
     */
    public static String generateActivityNo() {
        String date = DateUtils.format(DateUtils.now(), "yyyyMMdd");
        String randomNum = randomNumericId(6);
        return "ACT" + date + randomNum;
    }

    /**
     * 生成二维码编号（格式：QR + 时间戳 + 4位随机数）
     */
    public static String generateQrCodeNo() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomNum = randomNumericId(4);
        return "QR" + timestamp + randomNum;
    }

    /**
     * 生成用户编号（格式：U + 10位雪花算法ID）
     */
    public static String generateUserNo() {
        return "U" + String.valueOf(snowflakeId()).substring(0, 10);
    }

    /**
     * 生成管理员编号（格式：ADMIN + yyyyMMdd + 4位随机数）
     */
    public static String generateAdminNo() {
        String date = DateUtils.format(DateUtils.now(), "yyyyMMdd");
        String randomNum = randomNumericId(4);
        return "ADMIN" + date + randomNum;
    }

    /**
     * 生成验证码（纯数字）
     */
    public static String generateVerifyCode(int length) {
        return randomNumericId(length);
    }

    /**
     * 生成6位数字验证码
     */
    public static String generateVerifyCode() {
        return generateVerifyCode(6);
    }

    /**
     * 生成邀请码（6位字母数字组合）
     */
    public static String generateInviteCode() {
        return randomAlphaNumericId(6);
    }

    /**
     * 生成批次号（格式：BATCH + yyyyMMddHHmmss + 4位随机数）
     */
    public static String generateBatchNo() {
        String timestamp = DateUtils.format(DateUtils.now(), "yyyyMMddHHmmss");
        String randomNum = randomNumericId(4);
        return "BATCH" + timestamp + randomNum;
    }

    /**
     * 生成文件名（基于UUID）
     */
    public static String generateFileName(String originalName) {
        if (StringUtils.isBlank(originalName)) {
            return simpleUuid();
        }
        
        String extension = "";
        int lastDotIndex = originalName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalName.substring(lastDotIndex);
        }
        
        return simpleUuid() + extension;
    }

    /**
     * 生成请求ID（用于链路追踪）
     */
    public static String generateRequestId() {
        return "REQ" + System.currentTimeMillis() + randomNumericId(4);
    }

    /**
     * 生成会话ID
     */
    public static String generateSessionId() {
        return "SID" + System.currentTimeMillis() + randomAlphaNumericId(8);
    }

    /**
     * 生成令牌ID
     */
    public static String generateTokenId() {
        return "TKN" + simpleUuid();
    }

    /**
     * 生成日志ID
     */
    public static String generateLogId() {
        return "LOG" + snowflakeIdStr();
    }

    /**
     * 生成事务ID
     */
    public static String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + randomAlphaNumericId(6);
    }
}
