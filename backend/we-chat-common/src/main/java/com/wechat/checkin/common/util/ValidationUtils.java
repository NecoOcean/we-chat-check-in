package com.wechat.checkin.common.util;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 验证工具类
 * 提供常用的数据验证方法
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public class ValidationUtils {

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
     * 密码强度正则表达式（至少8位，包含字母和数字）
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$");

    /**
     * 用户名正则表达式（4-20位字母、数字、下划线）
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");

    /**
     * 验证对象是否为null
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 验证对象是否不为null
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * 验证字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return StrUtil.isEmpty(str);
    }

    /**
     * 验证字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return StrUtil.isNotEmpty(str);
    }

    /**
     * 验证字符串是否为空白
     */
    public static boolean isBlank(String str) {
        return StrUtil.isBlank(str);
    }

    /**
     * 验证字符串是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return StrUtil.isNotBlank(str);
    }

    /**
     * 验证集合是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 验证集合是否不为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 验证Map是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 验证Map是否不为空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 验证数组是否为空
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 验证数组是否不为空
     */
    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
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
     * 验证密码强度
     */
    public static boolean isValidPassword(String password) {
        if (isBlank(password)) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 验证用户名格式
     */
    public static boolean isValidUsername(String username) {
        if (isBlank(username)) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * 验证字符串长度是否在指定范围内
     */
    public static boolean isLengthBetween(String str, int min, int max) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= min && length <= max;
    }

    /**
     * 验证数值是否在指定范围内
     */
    public static boolean isNumberBetween(Number number, Number min, Number max) {
        if (number == null || min == null || max == null) {
            return false;
        }
        double value = number.doubleValue();
        double minValue = min.doubleValue();
        double maxValue = max.doubleValue();
        return value >= minValue && value <= maxValue;
    }

    /**
     * 验证是否为正整数
     */
    public static boolean isPositiveInteger(Number number) {
        if (number == null) {
            return false;
        }
        return number.longValue() > 0 && number.doubleValue() == number.longValue();
    }

    /**
     * 验证是否为非负整数
     */
    public static boolean isNonNegativeInteger(Number number) {
        if (number == null) {
            return false;
        }
        return number.longValue() >= 0 && number.doubleValue() == number.longValue();
    }

    /**
     * 验证URL格式
     */
    public static boolean isUrl(String url) {
        if (isBlank(url)) {
            return false;
        }
        return ReUtil.isMatch("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", url);
    }

    /**
     * 验证IP地址格式
     */
    public static boolean isIpAddress(String ip) {
        if (isBlank(ip)) {
            return false;
        }
        return ReUtil.isMatch("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$", ip);
    }

    /**
     * 验证日期格式（yyyy-MM-dd）
     */
    public static boolean isDateFormat(String date) {
        if (isBlank(date)) {
            return false;
        }
        return ReUtil.isMatch("^\\d{4}-\\d{2}-\\d{2}$", date);
    }

    /**
     * 验证时间格式（HH:mm:ss）
     */
    public static boolean isTimeFormat(String time) {
        if (isBlank(time)) {
            return false;
        }
        return ReUtil.isMatch("^\\d{2}:\\d{2}:\\d{2}$", time);
    }

    /**
     * 验证日期时间格式（yyyy-MM-dd HH:mm:ss）
     */
    public static boolean isDateTimeFormat(String dateTime) {
        if (isBlank(dateTime)) {
            return false;
        }
        return ReUtil.isMatch("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", dateTime);
    }

    /**
     * 验证是否为数字字符串
     */
    public static boolean isNumeric(String str) {
        if (isBlank(str)) {
            return false;
        }
        return ReUtil.isMatch("^\\d+$", str);
    }

    /**
     * 验证是否为字母字符串
     */
    public static boolean isAlpha(String str) {
        if (isBlank(str)) {
            return false;
        }
        return ReUtil.isMatch("^[a-zA-Z]+$", str);
    }

    /**
     * 验证是否为字母数字字符串
     */
    public static boolean isAlphaNumeric(String str) {
        if (isBlank(str)) {
            return false;
        }
        return ReUtil.isMatch("^[a-zA-Z0-9]+$", str);
    }

    /**
     * 验证是否包含中文字符
     */
    public static boolean containsChinese(String str) {
        if (isBlank(str)) {
            return false;
        }
        return ReUtil.isMatch(".*[\\u4e00-\\u9fa5].*", str);
    }

    /**
     * 验证是否为纯中文字符串
     */
    public static boolean isChinese(String str) {
        if (isBlank(str)) {
            return false;
        }
        return ReUtil.isMatch("^[\\u4e00-\\u9fa5]+$", str);
    }

    /**
     * 验证字符串是否匹配指定正则表达式
     */
    public static boolean matches(String str, String regex) {
        if (isBlank(str) || isBlank(regex)) {
            return false;
        }
        return ReUtil.isMatch(regex, str);
    }
}