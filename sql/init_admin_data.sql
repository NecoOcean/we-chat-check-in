-- 微信打卡小程序 - 管理员初始化数据脚本
-- 创建内置的市级管理员账号

SET NAMES utf8mb4;

-- 插入市级管理员账号
-- 密码：admin123，使用BCrypt加密（$2a$10$rounds）
-- 注意：实际部署时应该使用更强的密码和更高的加密轮数
INSERT INTO `admins` (
    `username`, 
    `password_hash`, 
    `role`, 
    `county_code`, 
    `status`, 
    `created_at`
) VALUES (
    'admin',
    '$2a$10$N.zmdr9k7uOCQb0bMs/OdOUBqvCD2XGb67f8UjvMUfQH.q/pzUzem', -- admin123
    'city',
    NULL,
    'enabled',
    NOW()
) ON DUPLICATE KEY UPDATE
    `password_hash` = VALUES(`password_hash`),
    `status` = VALUES(`status`),
    `updated_at` = NOW();

-- 验证插入结果
SELECT 
    id,
    username,
    role,
    county_code,
    status,
    created_at
FROM `admins` 
WHERE `username` = 'admin' AND `role` = 'city';

-- 提示信息
SELECT '市级管理员账号创建完成！账号：admin，密码：admin123' as message;