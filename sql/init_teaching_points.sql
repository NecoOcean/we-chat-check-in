-- 初始化教学点数据

SET NAMES utf8mb4;

-- 1. 先确保存在默认县
INSERT IGNORE INTO `counties` (
    `code`,
    `name`,
    `status`,
    `created_time`
) VALUES (
    'default',
    '默认县域',
    'enabled',
    NOW()
);

-- 2. 插入教学点
INSERT INTO `teaching_points` (
    `id`,
    `name`,
    `county_code`,
    `status`,
    `created_time`
) VALUES (
    1,
    '第一小学教学点',
    'default',
    'enabled',
    NOW()
),
(
    2,
    '第二中学教学点',
    'default',
    'enabled',
    NOW()
),
(
    3,
    '第三高中教学点',
    'default',
    'enabled',
    NOW()
)
ON DUPLICATE KEY UPDATE
    `status` = VALUES(`status`),
    `updated_time` = NOW();

-- 验证插入结果
SELECT 
    id,
    name,
    county_code,
    status,
    created_time
FROM `teaching_points` 
WHERE `county_code` = 'default';

SELECT '教学点初始化完成！' as message;
