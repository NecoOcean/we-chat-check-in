-- 崇左市县域数据初始化脚本
-- 数据来源：崇左市各县（市、区）邮政编码
-- 创建时间：2025-11-01

-- 设置字符集
SET NAMES utf8mb4;

-- 插入崇左市各县域数据
-- 使用 INSERT IGNORE 避免重复插入
INSERT INTO counties (code, name, status, created_time, updated_time) VALUES
('532200', '江州区', 'enabled', NOW(), NOW()),
('532100', '扶绥县', 'enabled', NOW(), NOW()),
('532500', '宁明县', 'enabled', NOW(), NOW()),
('532400', '龙州县', 'enabled', NOW(), NOW()),
('532300', '大新县', 'enabled', NOW(), NOW()),
('532800', '天等县', 'enabled', NOW(), NOW()),
('532600', '凭祥市', 'enabled', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    status = VALUES(status),
    updated_time = NOW();

-- 查询验证
SELECT * FROM counties ORDER BY code;

