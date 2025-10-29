package com.wechat.checkin.common.enums;

/**
 * 权限类型枚举
 * 用于权限判断和分类
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public enum PermissionTypeEnum {
    
    /**
     * 县级权限
     */
    COUNTY("county", "县级权限"),
    
    /**
     * 签到权限
     */
    CHECKIN("checkin", "签到权限"),
    
    /**
     * 活动权限
     */
    ACTIVITY("activity", "活动权限");

    private final String value;
    private final String description;

    PermissionTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据值获取枚举
     */
    public static PermissionTypeEnum getByValue(String value) {
        for (PermissionTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 检查权限代码是否包含指定类型
     */
    public boolean isContainedIn(String permissionCode) {
        return permissionCode != null && permissionCode.contains(this.value);
    }

    /**
     * 检查是否为县级相关权限
     */
    public static boolean isCountyRelated(String permissionCode) {
        return COUNTY.isContainedIn(permissionCode) || 
               CHECKIN.isContainedIn(permissionCode) || 
               ACTIVITY.isContainedIn(permissionCode);
    }
}