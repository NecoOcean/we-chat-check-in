-- 微信打卡小程序数据库建表脚本（版本 5.1 修复版）
-- 数据库引擎：MySQL InnoDB，支持外键约束和事务
-- 字符集：utf8mb4，排序规则：utf8mb4_unicode_ci

-- 设置字符集和排序规则
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 县域表
CREATE TABLE `counties` (
  `code` VARCHAR(16) NOT NULL COMMENT '县域编码',
  `name` VARCHAR(64) NOT NULL COMMENT '县域名称',
  `status` ENUM('enabled', 'disabled', 'deleted') NOT NULL DEFAULT 'enabled' COMMENT '状态：启用/禁用/软删除',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`code`),
  INDEX `idx_counties_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='县域表';

-- 2. 管理员账号表
CREATE TABLE `admins` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` VARCHAR(64) NOT NULL COMMENT '账号（如"PXadmin"）',
  `password` VARCHAR(255) NOT NULL COMMENT '密码哈希',
  `role` ENUM('city', 'county') NOT NULL COMMENT '角色：市级/县级',
  `county_code` VARCHAR(16) NULL COMMENT '县域编码（县级必填，市级为空）',
  `status` ENUM('enabled', 'disabled', 'deleted') NOT NULL DEFAULT 'enabled' COMMENT '状态：启用/禁用/软删除',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admins_username` (`username`),
  INDEX `idx_admins_role_county` (`role`, `county_code`),
  INDEX `idx_admins_status` (`status`),
  CONSTRAINT `fk_admins_county_code` FOREIGN KEY (`county_code`) REFERENCES `counties` (`code`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员账号表';

-- 3. 教学点表
CREATE TABLE `teaching_points` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '教学点ID',
  `name` VARCHAR(128) NOT NULL COMMENT '教学点名称',
  `county_code` VARCHAR(16) NOT NULL COMMENT '归属县域',
  `status` ENUM('enabled', 'disabled', 'deleted') NOT NULL DEFAULT 'enabled' COMMENT '状态：启用/禁用/软删除',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teaching_points_county_status` (`county_code`, `status`),
  INDEX `idx_teaching_points_name` (`name`),
  CONSTRAINT `fk_teaching_points_county_code` FOREIGN KEY (`county_code`) REFERENCES `counties` (`code`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学点表';

-- 4. 活动表
CREATE TABLE `activities` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `name` VARCHAR(128) NOT NULL COMMENT '活动名称',
  `description` TEXT NULL COMMENT '活动说明',
  `scope_county_code` VARCHAR(16) NULL COMMENT '县域范围（县级活动必填；市级活动为空时覆盖本市所有县域，指定时仅覆盖该县域）',
  `start_time` DATETIME NOT NULL COMMENT '打卡开始时间',
  `end_time` DATETIME NOT NULL COMMENT '打卡结束时间',
  `ended_time` DATETIME NULL COMMENT '结束操作时间',
  `created_id` BIGINT NOT NULL COMMENT '发起管理员ID',
  `status` ENUM('draft', 'ongoing', 'ended') NOT NULL DEFAULT 'draft' COMMENT '活动状态：草稿/进行中/已结束',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_activities_status_time` (`status`, `start_time`, `end_time`),
  INDEX `idx_activities_scope_county` (`scope_county_code`),
  CONSTRAINT `fk_activities_scope_county_code` FOREIGN KEY (`scope_county_code`) REFERENCES `counties` (`code`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_activities_created_id` FOREIGN KEY (`created_id`) REFERENCES `admins` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE -- 修复外键字段名
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- 5. 二维码表
CREATE TABLE `qrcodes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '二维码ID',
  `activity_id` BIGINT NOT NULL COMMENT '关联活动',
  `type` ENUM('checkin', 'evaluation') NOT NULL COMMENT '类型：打卡/评价',
  `token` VARCHAR(512) NOT NULL COMMENT '二维码令牌（含签名信息，JWT token通常较长）',
  `expire_time` DATETIME NULL COMMENT '过期时间',
  `disabled_time` DATETIME NULL COMMENT '禁用时间',
  `status` ENUM('enabled', 'disabled', 'deleted') NOT NULL DEFAULT 'enabled' COMMENT '状态：启用/禁用/软删除',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_qrcodes_token` (`token`),
  INDEX `idx_qrcodes_activity_type_status` (`activity_id`, `type`, `status`),
  INDEX `idx_qrcodes_expire_time` (`expire_time`), -- 修复索引字段名（at→time）
  CONSTRAINT `fk_qrcodes_activity_id` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='二维码表';

-- 6. 打卡表
CREATE TABLE `checkins` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '打卡ID',
  `activity_id` BIGINT NOT NULL COMMENT '活动ID',
  `teaching_point_id` BIGINT NOT NULL COMMENT '教学点ID',
  `attendee_count` INT NOT NULL COMMENT '实到人数',
  `submitted_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `source_qrcode_id` BIGINT NOT NULL COMMENT '来源二维码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_checkins_activity_teaching_point` (`activity_id`, `teaching_point_id`) COMMENT '幂等约束：同一活动同一教学点只能有一次打卡记录',
  INDEX `idx_checkins_activity_submitted_time` (`activity_id`, `submitted_time`),
  INDEX `idx_checkins_teaching_point` (`teaching_point_id`),
  INDEX `idx_checkins_source_qrcode` (`source_qrcode_id`),
  CONSTRAINT `fk_checkins_activity_id` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_checkins_teaching_point_id` FOREIGN KEY (`teaching_point_id`) REFERENCES `teaching_points` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_checkins_source_qrcode_id` FOREIGN KEY (`source_qrcode_id`) REFERENCES `qrcodes` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡表';

-- 7. 评价表
CREATE TABLE `evaluations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `activity_id` BIGINT NOT NULL COMMENT '活动ID',
  `teaching_point_id` BIGINT NOT NULL COMMENT '教学点ID',
  `q1_satisfaction` TINYINT NOT NULL COMMENT '满意度（1-3）',
  `q2_practicality` TINYINT NOT NULL COMMENT '实用性（1-3）',
  `q3_quality` TINYINT NULL COMMENT '质量（1-3，可选）',
  `suggestion_text` VARCHAR(200) NULL COMMENT '建议（≤200字）',
  `submitted_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `source_qrcode_id` BIGINT NOT NULL COMMENT '来源二维码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_evaluations_activity_teaching_point` (`activity_id`, `teaching_point_id`) COMMENT '防重复评价：同一活动同一教学点只能评价一次',
  INDEX `idx_evaluations_activity_submitted_time` (`activity_id`, `submitted_time`),
  INDEX `idx_evaluations_teaching_point` (`teaching_point_id`),
  INDEX `idx_evaluations_source_qrcode` (`source_qrcode_id`),
  CONSTRAINT `fk_evaluations_activity_id` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_evaluations_teaching_point_id` FOREIGN KEY (`teaching_point_id`) REFERENCES `teaching_points` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_evaluations_source_qrcode_id` FOREIGN KEY (`source_qrcode_id`) REFERENCES `qrcodes` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_evaluations_q1_satisfaction` CHECK (`q1_satisfaction` BETWEEN 1 AND 3),
  CONSTRAINT `chk_evaluations_q2_practicality` CHECK (`q2_practicality` BETWEEN 1 AND 3),
  CONSTRAINT `chk_evaluations_q3_quality` CHECK (`q3_quality` IS NULL OR (`q3_quality` BETWEEN 1 AND 3))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- 8. 审计日志表
CREATE TABLE `audit_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `actor_admin_id` BIGINT NOT NULL COMMENT '操作者管理员',
  `action` VARCHAR(64) NOT NULL COMMENT '动作（create_admin、disable_qrcode、finish_activity等）',
  `target_type` VARCHAR(64) NOT NULL COMMENT '目标类型（admin、activity、qrcode等）',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `before_json` JSON NULL COMMENT '变更前数据',
  `after_json` JSON NULL COMMENT '变更后数据',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  PRIMARY KEY (`id`),
  INDEX `idx_audit_logs_actor_created` (`actor_admin_id`, `created_time`),
  INDEX `idx_audit_logs_action` (`action`),
  INDEX `idx_audit_logs_target` (`target_type`, `target_id`),
  CONSTRAINT `fk_audit_logs_actor_admin_id` FOREIGN KEY (`actor_admin_id`) REFERENCES `admins` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 创建完成提示
SELECT 'Database tables created successfully! (Fixed version)' as message;