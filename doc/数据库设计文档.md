# 数据库设计（版本 5 对齐）

## 一、概览

- 选型建议：MySQL（或PostgreSQL）；使用InnoDB，开启外键约束与事务。
- 关键要求：权限隔离（县域）、二维码分阶段、幂等打卡、防重复评价、审计日志。

## 二、表结构

### admins（管理员账号）
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 管理员ID |
| username | VARCHAR(64) UNIQUE | 账号（如“PXadmin”） |
| password_hash | VARCHAR(255) | 密码哈希 |
| role | ENUM('city','county') | 角色：市级/县级 |
| county_code | VARCHAR(16) NULL | 县域编码（县级必填，市级为空） |
| status | ENUM('enabled','disabled','deleted') | 状态：启用/禁用/软删除 |
| last_login_at | DATETIME NULL | 最近登录时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

索引：`UNIQUE(username)`，`INDEX(role, county_code)`。

### counties（县域）
| 字段 | 类型 | 说明 |
|---|---|---|
| code | VARCHAR(16) PK | 县域编码 |
| name | VARCHAR(64) | 县域名称 |
| status | ENUM('enabled','disabled') | 状态 |
| created_at | DATETIME | 创建时间 |

### teaching_points（教学点）
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 教学点ID |
| name | VARCHAR(128) | 教学点名称 |
| county_code | VARCHAR(16) FK counties.code | 归属县域 |
| status | ENUM('enabled','disabled','deleted') | 状态 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

索引：`INDEX(county_code, status)`，`INDEX(name)`。

### activities（活动）
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 活动ID |
| name | VARCHAR(128) | 活动名称 |
| start_time | DATETIME | 打卡开始时间 |
| end_time | DATETIME | 打卡结束时间 |
| description | TEXT NULL | 活动说明 |
| status | ENUM('draft','ongoing','ended') | 活动状态 |
| scope_county_code | VARCHAR(16) NULL | 县域范围（县级活动必填，市级可空或指定县） |
| created_by | BIGINT FK admins.id | 发起管理员 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| ended_at | DATETIME NULL | 结束时间（操作时间） |

索引：`INDEX(status, start_time, end_time)`，`INDEX(scope_county_code)`。

### qrcodes（二维码）
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 二维码ID |
| activity_id | BIGINT FK activities.id | 关联活动 |
| type | ENUM('checkin','evaluation') | 类型：打卡/评价 |
| token | VARCHAR(128) UNIQUE | 二维码令牌（含签名信息） |
| expire_at | DATETIME NULL | 过期时间 |
| status | ENUM('active','disabled','expired') | 状态：正常/禁用/过期 |
| created_at | DATETIME | 创建时间 |
| disabled_at | DATETIME NULL | 禁用时间 |

索引：`UNIQUE(token)`，`INDEX(activity_id, type, status)`。

### checkins（打卡）
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 打卡ID |
| activity_id | BIGINT FK activities.id | 活动ID |
| teaching_point_id | BIGINT FK teaching_points.id | 教学点ID |
| attendee_count | INT | 实到人数 |
| submitted_at | DATETIME | 提交时间 |
| source_qrcode_id | BIGINT FK qrcodes.id | 来源二维码 |

约束：`UNIQUE(activity_id, teaching_point_id)`（幂等覆盖最近一次提交）。
索引：`INDEX(activity_id, submitted_at)`。

### evaluations（评价）
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 评价ID |
| activity_id | BIGINT FK activities.id | 活动ID |
| teaching_point_id | BIGINT FK teaching_points.id | 教学点ID |
| q1_satisfaction | TINYINT | 满意度（1-3） |
| q2_practicality | TINYINT | 实用性（1-3） |
| q3_quality | TINYINT NULL | 质量（1-3，可选） |
| suggestion_text | VARCHAR(200) NULL | 建议（≤200字） |
| submitted_at | DATETIME | 提交时间 |
| source_qrcode_id | BIGINT FK qrcodes.id | 来源二维码 |

约束：仅允许参与过活动的教学点写评价；如需限制一次评价，可加`UNIQUE(activity_id, teaching_point_id)`。
索引：`INDEX(activity_id, submitted_at)`，`INDEX(teaching_point_id)`。

### audit_logs（审计日志）
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK | 日志ID |
| actor_admin_id | BIGINT FK admins.id | 操作者管理员 |
| action | VARCHAR(64) | 动作（create_admin、disable_qrcode、finish_activity等） |
| target_type | VARCHAR(64) | 目标类型（admin、activity、qrcode等） |
| target_id | BIGINT | 目标ID |
| before_json | JSON NULL | 变更前 |
| after_json | JSON NULL | 变更后 |
| created_at | DATETIME | 记录时间 |
| ip | VARCHAR(64) NULL | 操作来源IP |

索引：`INDEX(actor_admin_id, created_at)`，`INDEX(action)`。

## 三、设计要点

- 权限：所有查询与写入按角色与县域过滤；市级访问全量，县级限制本县。
- 二维码：token需签名与校验；生成时记录类型与有效期；禁用后状态更新。
- 幂等：打卡采用活动+教学点唯一约束进行覆盖；评价可选择去重策略。
- 统计：看板与详情依赖聚合查询，需加索引与适度缓存。