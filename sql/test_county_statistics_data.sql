-- ============================================================================
-- 微信打卡系统 - 方案1实现 - 县域统计功能测试数据脚本
-- ============================================================================
-- 功能: 测试市级管理员县域打卡统计接口 (GET /api/activities/{id}/county-statistics)
-- 测试场景:
--   1. 多县域统计: 3个县域，各有不同数量的教学点和打卡记录
--   2. 打卡详情: 测试打卡详情列表按县域分组
--   3. 权限隔离: 市级管理员可见全部数据，县级管理员只见本县
-- 创建日期: 2024-10-31
-- 注意: 市级管理员账户由系统自动生成，无需手动插入
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================================
-- 第1部分: 县域数据 (3个县域)
-- ============================================================================
INSERT INTO `counties` (`code`, `name`, `status`, `created_time`, `updated_time`) VALUES
('001', '朝阳区', 'enabled', NOW(), NOW()),
('002', '丰台区', 'enabled', NOW(), NOW()),
('003', '东城区', 'enabled', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    `status` = VALUES(`status`),
    `updated_time` = NOW();

-- ============================================================================
-- 第2部分: 管理员账号 (仅插入3个县级管理员)
-- 注意: 市级管理员账户已由系统自动生成，无需插入
-- ============================================================================

-- 县级管理员 - 朝阳区
INSERT INTO `admins` (`username`, `password`, `role`, `county_code`, `status`, `created_time`, `updated_time`) VALUES
('chaoyang_admin', '$2a$10$N.zmdr9k7uOCQb0bMs/OdOUBqvCD2XGb67f8UjvMUfQH.q/pzUzem', 'county', '001', 'enabled', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    `password` = VALUES(`password`),
    `status` = VALUES(`status`),
    `updated_time` = NOW();

-- 县级管理员 - 丰台区
INSERT INTO `admins` (`username`, `password`, `role`, `county_code`, `status`, `created_time`, `updated_time`) VALUES
('fengtai_admin', '$2a$10$N.zmdr9k7uOCQb0bMs/OdOUBqvCD2XGb67f8UjvMUfQH.q/pzUzem', 'county', '002', 'enabled', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    `password` = VALUES(`password`),
    `status` = VALUES(`status`),
    `updated_time` = NOW();

-- 县级管理员 - 东城区
INSERT INTO `admins` (`username`, `password`, `role`, `county_code`, `status`, `created_time`, `updated_time`) VALUES
('dongcheng_admin', '$2a$10$N.zmdr9k7uOCQb0bMs/OdOUBqvCD2XGb67f8UjvMUfQH.q/pzUzem', 'county', '003', 'enabled', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    `password` = VALUES(`password`),
    `status` = VALUES(`status`),
    `updated_time` = NOW();

-- ============================================================================
-- 第3部分: 教学点数据 (15个教学点，分布在3个县域)
-- ============================================================================
-- 朝阳区教学点 (5个)
INSERT INTO `teaching_points` (`name`, `county_code`, `status`, `created_time`, `updated_time`) VALUES
('朝阳区第一小学', '001', 'enabled', NOW(), NOW()),
('朝阳区第二中学', '001', 'enabled', NOW(), NOW()),
('朝阳区第三高中', '001', 'enabled', NOW(), NOW()),
('朝阳区第四初中', '001', 'enabled', NOW(), NOW()),
('朝阳区第五小学', '001', 'enabled', NOW(), NOW()),
-- 丰台区教学点 (5个)
('丰台区第一中学', '002', 'enabled', NOW(), NOW()),
('丰台区第二小学', '002', 'enabled', NOW(), NOW()),
('丰台区第三高中', '002', 'enabled', NOW(), NOW()),
('丰台区第四初中', '002', 'enabled', NOW(), NOW()),
('丰台区第五小学', '002', 'enabled', NOW(), NOW()),
-- 东城区教学点 (5个)
('东城区第一小学', '003', 'enabled', NOW(), NOW()),
('东城区第二中学', '003', 'enabled', NOW(), NOW()),
('东城区第三高中', '003', 'enabled', NOW(), NOW()),
('东城区第四初中', '003', 'enabled', NOW(), NOW()),
('东城区第五小学', '003', 'enabled', NOW(), NOW());

-- ============================================================================
-- 第4部分: 获取市级管理员ID (已有的系统管理员)
-- ============================================================================
-- 假设系统中已有的市级管理员ID为1 (通常是第一个创建的管理员账户)
-- 如果不是，请根据实际情况修改 SELECT 语句查询正确的ID
SELECT @city_admin_id := id FROM `admins` WHERE `role` = 'city' LIMIT 1;

-- 如果没有市级管理员，请手动设置:
-- SET @city_admin_id := 1;  -- 请替换为实际的市级管理员ID

-- ============================================================================
-- 第5部分: 活动数据 (1个测试活动, 状态为 ONGOING)
-- ============================================================================
INSERT INTO `activities` (
    `name`, 
    `description`, 
    `scope_county_code`, 
    `start_time`, 
    `end_time`, 
    `created_id`, 
    `status`, 
    `created_time`, 
    `updated_time`
) VALUES (
    '2024年秋季教学活动',
    '全市范围内的教学评估和打卡活动',
    NULL,  -- 市级活动，覆盖全市
    DATE_SUB(NOW(), INTERVAL 2 DAY),  -- 2天前开始
    DATE_ADD(NOW(), INTERVAL 2 DAY),  -- 2天后结束
    @city_admin_id,
    'ongoing',
    NOW(),
    NOW()
);

-- 获取活动ID以备后续使用
SELECT @activity_id := id FROM `activities` 
WHERE `name` = '2024年秋季教学活动' 
ORDER BY created_time DESC LIMIT 1;

-- ============================================================================
-- 第6部分: 二维码数据 (2个二维码: 打卡和评价)
-- ============================================================================
-- 打卡二维码
INSERT INTO `qrcodes` (
    `activity_id`,
    `type`,
    `token`,
    `expire_time`,
    `status`,
    `created_time`,
    `updated_time`
) VALUES (
    @activity_id,
    'checkin',
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY3Rpdml0eV9pZCI6MSwicXJjb2RlX3R5cGUiOiJjaGVja2luIn0.test_token_checkin_1',
    DATE_ADD(NOW(), INTERVAL 7 DAY),
    'enabled',
    NOW(),
    NOW()
);

-- 获取打卡二维码ID
SELECT @checkin_qrcode_id := id FROM `qrcodes` 
WHERE `activity_id` = @activity_id AND `type` = 'checkin' 
LIMIT 1;

-- 评价二维码
INSERT INTO `qrcodes` (
    `activity_id`,
    `type`,
    `token`,
    `expire_time`,
    `status`,
    `created_time`,
    `updated_time`
) VALUES (
    @activity_id,
    'evaluation',
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY3Rpdml0eV9pZCI6MSwicXJjb2RlX3R5cGUiOiJldmFsdWF0aW9uIn0.test_token_evaluation_1',
    DATE_ADD(NOW(), INTERVAL 7 DAY),
    'enabled',
    NOW(),
    NOW()
);

-- 获取评价二维码ID
SELECT @evaluation_qrcode_id := id FROM `qrcodes` 
WHERE `activity_id` = @activity_id AND `type` = 'evaluation' 
LIMIT 1;

-- ============================================================================
-- 第7部分: 打卡数据 (15条打卡记录，分布在3个县域)
-- ============================================================================

-- 朝阳区打卡数据 (5个教学点)
INSERT INTO `checkins` (`activity_id`, `teaching_point_id`, `attendee_count`, `submitted_time`, `source_qrcode_id`) VALUES
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '朝阳区第一小学' LIMIT 1), 30, DATE_SUB(NOW(), INTERVAL 1 DAY), @checkin_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '朝阳区第二中学' LIMIT 1), 28, DATE_SUB(NOW(), INTERVAL 1 DAY), @checkin_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '朝阳区第三高中' LIMIT 1), 32, DATE_SUB(NOW(), INTERVAL 23 HOUR), @checkin_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '朝阳区第四初中' LIMIT 1), 26, DATE_SUB(NOW(), INTERVAL 22 HOUR), @checkin_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '朝阳区第五小学' LIMIT 1), 34, DATE_SUB(NOW(), INTERVAL 20 HOUR), @checkin_qrcode_id),

-- 丰台区打卡数据 (5个教学点)
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '丰台区第一中学' LIMIT 1), 25, DATE_SUB(NOW(), INTERVAL 18 HOUR), @checkin_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '丰台区第二小学' LIMIT 1), 29, DATE_SUB(NOW(), INTERVAL 16 HOUR), @checkin_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '丰台区第三高中' LIMIT 1), 31, DATE_SUB(NOW(), INTERVAL 14 HOUR), @checkin_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '丰台区第四初中' LIMIT 1), 27, DATE_SUB(NOW(), INTERVAL 12 HOUR), @checkin_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '丰台区第五小学' LIMIT 1), 33, DATE_SUB(NOW(), INTERVAL 10 HOUR), @checkin_qrcode_id),

-- 东城区打卡数据 (5个教学点)
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '东城区第一小学' LIMIT 1), 24, DATE_SUB(NOW(), INTERVAL 8 HOUR), @checkin_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '东城区第二中学' LIMIT 1), 22, DATE_SUB(NOW(), INTERVAL 6 HOUR), @checkin_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '东城区第三高中' LIMIT 1), 28, DATE_SUB(NOW(), INTERVAL 4 HOUR), @checkin_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '东城区第四初中' LIMIT 1), 26, DATE_SUB(NOW(), INTERVAL 2 HOUR), @checkin_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '东城区第五小学' LIMIT 1), 30, NOW(), @checkin_qrcode_id);

-- ============================================================================
-- 第8部分: 评价数据 (15条评价记录)
-- ============================================================================

-- 朝阳区评价数据
INSERT INTO `evaluations` (`activity_id`, `teaching_point_id`, `q1_satisfaction`, `q2_practicality`, `q3_quality`, `suggestion_text`, `submitted_time`, `source_qrcode_id`) VALUES
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '朝阳区第一小学' LIMIT 1), 3, 3, 3, '非常满意，活动组织得很好', DATE_SUB(NOW(), INTERVAL 1 DAY), @evaluation_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '朝阳区第二中学' LIMIT 1), 2, 3, 2, '内容有帮助，但时间有点紧', NOW(), @evaluation_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '朝阳区第三高中' LIMIT 1), 3, 2, 3, '收获很大', NOW(), @evaluation_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '朝阳区第四初中' LIMIT 1), 2, 2, 2, '一般般', NOW(), @evaluation_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '朝阳区第五小学' LIMIT 1), 3, 3, 3, '非常好', NOW(), @evaluation_qrcode_id),

-- 丰台区评价数据
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '丰台区第一中学' LIMIT 1), 3, 3, 3, '活动很不错', NOW(), @evaluation_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '丰台区第二小学' LIMIT 1), 2, 3, 2, '希望下次有改进', NOW(), @evaluation_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '丰台区第三高中' LIMIT 1), 3, 3, 3, '推荐', NOW(), @evaluation_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '丰台区第四初中' LIMIT 1), 2, 2, 2, '一般', NOW(), @evaluation_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '丰台区第五小学' LIMIT 1), 3, 3, 3, '很好', NOW(), @evaluation_qrcode_id),

-- 东城区评价数据
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '东城区第一小学' LIMIT 1), 3, 3, 3, '赞', NOW(), @evaluation_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '东城区第二中学' LIMIT 1), 2, 3, 2, '还不错', NOW(), @evaluation_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '东城区第三高中' LIMIT 1), 3, 2, 3, '好', NOW(), @evaluation_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '东城区第四初中' LIMIT 1), 2, 2, 2, '可以接受', NOW(), @evaluation_qrcode_id),
(@activity_id, (SELECT id FROM `teaching_points` WHERE `name` = '东城区第五小学' LIMIT 1), 3, 3, 3, '棒', NOW(), @evaluation_qrcode_id);

-- ============================================================================
-- 第9部分: 数据验证和统计
-- ============================================================================

-- 验证1: 显示活动信息
SELECT '========== 活动信息 ==========' as '';
SELECT 
    id,
    name,
    status,
    start_time,
    end_time,
    created_time
FROM `activities` 
WHERE `name` = '2024年秋季教学活动';

-- 验证2: 显示县域统计（模拟API响应）
SELECT '========== 县域打卡统计 ==========' as '';
SELECT 
    COALESCE(tp.county_code, 'UNKNOWN') as county_code,
    (SELECT name FROM counties WHERE code = tp.county_code LIMIT 1) as county_name,
    COUNT(DISTINCT c.teaching_point_id) as participating_points,
    COALESCE(SUM(c.attendee_count), 0) as total_attendees,
    COUNT(*) as total_checkins
FROM `checkins` c
LEFT JOIN `teaching_points` tp ON c.teaching_point_id = tp.id
WHERE c.activity_id = @activity_id
GROUP BY tp.county_code
ORDER BY tp.county_code;

-- 验证3: 显示打卡详情（模拟API响应）
SELECT '========== 打卡详情列表 ==========' as '';
SELECT 
    c.id,
    c.teaching_point_id,
    tp.name as teaching_point_name,
    tp.county_code,
    (SELECT name FROM counties WHERE code = tp.county_code LIMIT 1) as county_name,
    c.attendee_count,
    c.submitted_time
FROM `checkins` c
LEFT JOIN `teaching_points` tp ON c.teaching_point_id = tp.id
WHERE c.activity_id = @activity_id
ORDER BY tp.county_code, c.submitted_time DESC;

-- 验证4: 显示各县评价统计
SELECT '========== 评价统计 ==========' as '';
SELECT 
    COALESCE(tp.county_code, 'UNKNOWN') as county_code,
    (SELECT name FROM counties WHERE code = tp.county_code LIMIT 1) as county_name,
    COUNT(*) as evaluation_count,
    AVG(e.q1_satisfaction) as avg_satisfaction,
    AVG(e.q2_practicality) as avg_practicality
FROM `evaluations` e
LEFT JOIN `teaching_points` tp ON e.teaching_point_id = tp.id
WHERE e.activity_id = @activity_id
GROUP BY tp.county_code
ORDER BY tp.county_code;

-- 验证5: 显示管理员账号
SELECT '========== 管理员账号（用于测试） ==========' as '';
SELECT 
    id,
    username,
    role,
    county_code,
    '密码: admin123' as password_hint
FROM `admins` 
WHERE username IN ('chaoyang_admin', 'fengtai_admin', 'dongcheng_admin')
ORDER BY county_code;

-- 验证6: 显示系统中的市级管理员
SELECT '========== 系统市级管理员 ==========' as '';
SELECT 
    id,
    username,
    role,
    county_code,
    '由系统自动生成' as note
FROM `admins` 
WHERE role = 'city'
LIMIT 1;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

SELECT '
╔════════════════════════════════════════════════════════════════╗
║                   测试数据插入完成！                           ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║  📊 数据概览:                                                  ║
║  • 县域数量: 3个 (朝阳区、丰台区、东城区)                      ║
║  • 教学点数: 15个 (每个县5个)                                  ║
║  • 活动数: 1个 (状态: ongoing)                                 ║
║  • 打卡记录: 15条 (每个教学点1条)                              ║
║  • 评价记录: 15条 (每个教学点1条)                              ║
║  • 市级管理员: 由系统自动生成（无需插入）                      ║
║  • 县级管理员: 3个 (朝阳、丰台、东城各1个)                     ║
║                                                                ║
║  🔐 测试账号 (密码均为 admin123):                             ║
║  • chaoyang_admin (朝阳区县级管理员)                            ║
║  • fengtai_admin (丰台区县级管理员)                             ║
║  • dongcheng_admin (东城区县级管理员)                           ║
║  • 市级管理员: 使用系统已有账户                                ║
║                                                                ║
║  🧪 测试用例:                                                  ║
║  1. [市级管理员] 调用 GET /api/activities/{id}/county-statistics ║
║     → 应返回 3个县域的统计数据和全部15条打卡详情               ║
║                                                                ║
║  2. [县级管理员-朝阳] 调用同一接口                              ║
║     → 应返回 countyStatistics=null, 仅5条本县打卡详情          ║
║                                                                ║
║  3. 验证县域统计字段:                                          ║
║     • countyCode, countyName, participatingPoints              ║
║     • totalAttendees, totalCheckins                            ║
║                                                                ║
║  4. 验证打卡详情字段:                                          ║
║     • id, teachingPointId, teachingPointName                   ║
║     • countyCode, countyName, attendeeCount, submittedTime     ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝
' as summary;
