# API 接口文档（版本 5 对齐）

> 说明：除参与端打卡与评价接口外，其余接口均需管理员鉴权。统一返回结构：
> `{ code: 0, message: 'ok', data: ... }`；错误码非0，`message`提供原因。

## 认证与用户

- POST `/api/auth/login`
  - 入参：`{ username, password }`
  - 出参：`{ token, user: { id, role, county_code } }`

- GET `/api/auth/me`
  - 出参：当前登录管理员信息。

- POST `/api/auth/logout`
  - 功能：退出登录。

## 县级账号管理（仅市级）

- POST `/api/admins`
  - 入参：`{ username, password, county_code }`

- PUT `/api/admins/:id/password`
  - 入参：`{ new_password }`

- PATCH `/api/admins/:id/status`
  - 入参：`{ status: 'enabled'|'disabled' }`

- DELETE `/api/admins/:id`
  - 软删除；需记录审计日志。

- GET `/api/admins`
  - 过滤：`?status=&county_code=`；分页参数`page,size`。

## 教学点

- GET `/api/teaching-points`
  - 过滤：`?county_code=&status=`；分页。

## 活动管理

- POST `/api/activities`
  - 入参：`{ name, start_time, end_time, description, scope_county_code? }`
  - 出参：`{ id, ... }` 及打卡二维码生成结果（可选返回URL）。

- GET `/api/activities`
  - 过滤：`?status=&county_code=`；分页。

- GET `/api/activities/:id`
  - 出参：活动详情与参与统计基础数据。

- POST `/api/activities/:id/finish`
  - 结束活动；关联打卡二维码失效；审计日志记录。

## 二维码管理

- POST `/api/activities/:id/qrcodes`
  - 入参：`{ type: 'checkin'|'evaluation', expire_time? }`
  - 出参：`{ id, url, token, type, expire_time, status }`

- GET `/api/qrcodes`
  - 过滤：`?activity_id=&type=&status=`；分页。

- PATCH `/api/qrcodes/:id/disable`
  - 禁用二维码；审计日志记录。

## 参与端（无需登录）

- POST `/api/checkins/checkin`
  - 入参：`{ token, teaching_point_id, attendee_count }`
  - 业务校验：二维码为`checkin`类型、有效且未禁用；活动进行中；人数为正整数。
  - 出参：`{ success: true, submitted_time }`

- POST `/api/checkins/evaluate`
  - 入参：`{ token, teaching_point_id, q1_satisfaction, q2_practicality, q3_quality?, suggestion_text? }`
  - 业务校验：二维码为`evaluation`类型、有效且未禁用；验证该教学点参与过该活动。
  - 出参：`{ success: true, submitted_time }`

## 看板与统计

- GET `/api/dashboard/summary`
  - 出参：`{ total_activities, ongoing_activities, total_teaching_points_participated, total_attendees }`

- GET `/api/activities/:id/participants`
  - 出参：教学点参与列表（名称、人数、提交时间），分页。

- GET `/api/activities/:id/evaluations/summary`
  - 出参：评价饼图所需聚合数据（各选项占比等）。

- GET `/api/activities/:id/evaluations/detail`
  - 出参：逐教学点评价明细。

## 错误码约定（示例）

- `1001` 未授权/登录过期
- `2001` 参数非法
- `3001` 活动已结束
- `3002` 二维码已失效（禁用/过期）
- `3003` 非评价阶段
- `3004` 无参与记录，无法评价
- `5000` 系统错误