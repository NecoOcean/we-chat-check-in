package com.wechat.checkin.common.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 基于Hutool的StrUtil进行扩展
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public class StringUtils extends StrUtil {

    /**
     * 手机号正则表达式
     */
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 邮箱正则表达式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    /**
     * 身份证号正则表达式（18位）
     */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");

    /**
     * 判断字符串是否为空或null
     */
    public static boolean isEmpty(CharSequence str) {
        return StrUtil.isEmpty(str);
    }

    /**
     * 判断字符串是否不为空且不为null
     */
    public static boolean isNotEmpty(CharSequence str) {
        return StrUtil.isNotEmpty(str);
    }

    /**
     * 判断字符串是否为空白（null、空字符串、只包含空白字符）
     */
    public static boolean isBlank(CharSequence str) {
        return StrUtil.isBlank(str);
    }

    /**
     * 判断字符串是否不为空白
     */
    public static boolean isNotBlank(CharSequence str) {
        return StrUtil.isNotBlank(str);
    }

    /**
     * 去除字符串首尾空白字符
     */
    public static String trim(CharSequence str) {
        return StrUtil.trim(str);
    }

    /**
     * 字符串格式化
     */
    public static String format(CharSequence template, Object... params) {
        return StrUtil.format(template, params);
    }

    /**
     * 使用指定分隔符连接集合中的元素
     */
    public static String join(CharSequence delimiter, Collection<?> collection) {
        return StrUtil.join(delimiter, collection);
    }

    /**
     * 使用指定分隔符连接数组中的元素
     */
    public static String join(CharSequence delimiter, Object... array) {
        return StrUtil.join(delimiter, array);
    }

    /**
     * 分割字符串
     */
    public static List<String> splitToList(CharSequence str, CharSequence separator) {
        return StrUtil.split(str, separator);
    }

    /**
     * 分割字符串为数组
     */
    public static String[] splitToArray(CharSequence str, CharSequence separator) {
        List<String> list = StrUtil.split(str, separator);
        return list.toArray(new String[0]);
    }

    /**
     * 验证手机号格式
     */
    public static boolean isMobile(String mobile) {
        if (isBlank(mobile)) {
            return false;
        }
        return MOBILE_PATTERN.matcher(mobile).matches();
    }

    /**
     * 验证邮箱格式
     */
    public static boolean isEmail(String email) {
        if (isBlank(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证身份证号格式
     */
    public static boolean isIdCard(String idCard) {
        if (isBlank(idCard)) {
            return false;
        }
        return ID_CARD_PATTERN.matcher(idCard).matches();
    }

    /**
     * 隐藏手机号中间4位
     */
    public static String hideMobile(String mobile) {
        if (isBlank(mobile) || mobile.length() != 11) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }

    /**
     * 隐藏邮箱用户名部分
     */
    public static String hideEmail(String email) {
        if (isBlank(email) || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        if (username.length() <= 2) {
            return email;
        }
        return username.substring(0, 1) + "***" + username.substring(username.length() - 1) + "@" + parts[1];
    }

    /**
     * 隐藏身份证号中间部分
     */
    public static String hideIdCard(String idCard) {
        if (isBlank(idCard) || idCard.length() != 18) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }

    /**
     * 生成指定长度的随机字符串（包含数字和字母）
     */
    public static String randomString(int length) {
        return IdUtil.randomUUID().replace("-", "").substring(0, Math.min(length, 32));
    }

    /**
     * 生成指定长度的随机数字字符串
     */
    public static String randomNumbers(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    /**
     * 首字母大写
     */
    public static String upperFirst(CharSequence str) {
        return StrUtil.upperFirst(str);
    }

    /**
     * 首字母小写
     */
    public static String lowerFirst(CharSequence str) {
        return StrUtil.lowerFirst(str);
    }

    /**
     * 驼峰转下划线
     */
    public static String toUnderlineCase(CharSequence str) {
        return StrUtil.toUnderlineCase(str);
    }

    /**
     * 下划线转驼峰
     */
    public static String toCamelCase(CharSequence name) {
        return StrUtil.toCamelCase(name);
    }

    /**
     * 检查字符串长度是否在指定范围内
     */
    public static boolean lengthBetween(CharSequence str, int min, int max) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= min && length <= max;
    }

    /**
     * 安全的字符串比较（避免空指针异常）
     */
    public static boolean equals(CharSequence str1, CharSequence str2) {
        return StrUtil.equals(str1, str2);
    }

    /**
     * 安全的字符串比较（忽略大小写）
     */
    public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
        return StrUtil.equalsIgnoreCase(str1, str2);
    }
}