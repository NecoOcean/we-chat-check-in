# API 接口文档

**版本**: v1.0.0  
**最后更新**: 2025-10-30  
**文档状态**: Draft

---

## 1. 概述

### 1.1 基本信息

- **Base URL**: `http://localhost:8080` (开发环境)
- **协议**: HTTP/HTTPS
- **数据格式**: JSON
- **字符编码**: UTF-8
- **时间格式**: ISO 8601 (`yyyy-MM-dd'T'HH:mm:ss`)

### 1.2 统一响应结构

所有接口均采用统一的响应格式:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1698765432000,
  "requestId": "req-xxx-xxx"
}
```

**响应字段说明**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | Integer | 是 | 响应状态码,200表示成功,其他表示失败 |
| message | String | 是 | 响应消息描述 |
| data | Object/Array | 否 | 响应数据,可为对象、数组或null |
| timestamp | Long | 是 | 响应时间戳(毫秒) |
| requestId | String | 否 | 请求追踪ID,用于日志追踪 |

### 1.3 分页响应结构

分页接口的响应格式:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [],
  "current": 1,
  "size": 10,
  "total": 100,
  "pages": 10,
  "hasPrevious": false,
  "hasNext": true,
  "timestamp": 1698765432000
}
```

**分页字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| current | Long | 当前页码(从1开始) |
| size | Long | 每页大小 |
| total | Long | 总记录数 |
| pages | Long | 总页数 |
| hasPrevious | Boolean | 是否有上一页 |
| hasNext | Boolean | 是否有下一页 |

### 1.4 认证机制

除参与端接口(打卡、评价)外,所有管理端接口均需要JWT认证:

**请求头**:
```
Authorization: Bearer <access_token>
```

**认证流程**:
1. 通过登录接口获取访问令牌(accessToken)
2. 在后续请求的Header中携带令牌
3. 令牌过期后使用刷新令牌获取新的访问令牌
4. 登出后令牌失效

### 1.5 权限说明

系统支持两种角色:

| 角色 | 角色值 | 权限范围 |
|------|--------|----------|
| 市级管理员 | city | 全市数据访问权限、账号管理权限 |
| 县级管理员 | county | 本县数据访问权限、无账号管理权限 |

---

## 2. 认证管理

### 2.1 管理员登录

**接口**: `POST /api/auth/login`  
**权限**: 无需认证  
**描述**: 市级或县级管理员使用用户名和密码登录系统

**请求参数**:

```json
{
  "username": "admin",
  "password": "password123"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名,长度1-64 |
| password | String | 是 | 密码,长度6-64 |

**响应示例**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "id": 1,
      "username": "admin",
      "role": "city",
      "countyCode": null
    }
  },
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| accessToken | String | JWT访问令牌 |
| refreshToken | String | JWT刷新令牌 |
| tokenType | String | 令牌类型,固定为"Bearer" |
| expiresIn | Long | 访问令牌过期时间(秒) |
| userInfo | Object | 用户信息对象 |
| userInfo.id | Long | 用户ID |
| userInfo.username | String | 用户名 |
| userInfo.role | String | 角色(city/county) |
| userInfo.countyCode | String | 县域编码,市级为null |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1101 | 用户不存在 |
| 1104 | 密码错误 |
| 1102 | 用户已被禁用 |
| 1002 | 参数错误 |

---

### 2.2 刷新令牌

**接口**: `POST /api/auth/refresh`  
**权限**: 无需认证  
**描述**: 使用刷新令牌获取新的访问令牌

**请求参数**:

| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| refreshToken | String | Query | 是 | 刷新令牌 |

**请求示例**:
```
POST /api/auth/refresh?refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**响应示例**:

```json
{
  "code": 200,
  "message": "令牌刷新成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "id": 1,
      "username": "admin",
      "role": "city",
      "countyCode": null
    }
  },
  "timestamp": 1698765432000
}
```

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1201 | 令牌无效 |
| 1202 | 令牌已过期 |

---

### 2.3 管理员登出

**接口**: `POST /api/auth/logout`  
**权限**: 需要认证  
**描述**: 销毁当前会话,使访问令牌失效

**请求头**:
```
Authorization: Bearer <access_token>
```

**响应示例**:

```json
{
  "code": 200,
  "message": "登出成功",
  "timestamp": 1698765432000
}
```

---

### 2.4 获取当前用户信息

**接口**: `GET /api/auth/me`  
**权限**: 需要认证  
**描述**: 根据访问令牌获取当前登录用户的基本信息

**请求头**:
```
Authorization: Bearer <access_token>
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "role": "city",
    "countyCode": null
  },
  "timestamp": 1698765432000
}
```

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1205 | 请先登录 |
| 1201 | 令牌无效 |
| 1202 | 令牌已过期 |

---

### 2.5 验证令牌

**接口**: `GET /api/auth/validate`  
**权限**: 无需认证  
**描述**: 验证访问令牌是否有效

**请求参数**:

| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| token | String | Query | 是 | 访问令牌 |

**请求示例**:
```
GET /api/auth/validate?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": true,
  "timestamp": 1698765432000
}
```

---

## 3. 活动管理

### 3.1 创建活动

**接口**: `POST /api/activities`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 创建新的打卡活动,并自动生成打卡二维码。县域范围通过当前登录用户的角色和所属区域自动确定,前端不传递该字段。

**请求头**:
```
Authorization: Bearer <access_token>
```

**请求参数**:

```json
{
  "name": "2024年春季教学活动",
  "description": "本次活动旨在提升教学质量",
  "startTime": "2024-03-01T08:00:00",
  "endTime": "2024-03-31T18:00:00"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 活动名称,长度1-128 |
| description | String | 否 | 活动说明 |
| startTime | String | 是 | 打卡开始时间,ISO 8601格式 |
| endTime | String | 是 | 打卡结束时间,ISO 8601格式 |

**业务规则**:
- 县域范围由系统根据登录用户自动确定:
  - 县级管理员:自动设置为所属县域
  - 市级管理员:不指定县域(覆盖全市)
- 开始时间必须早于结束时间
- 活动创建后状态为"进行中"(ongoing)

**响应示例**:

```json
{
  "code": 200,
  "message": "活动创建成功",
  "data": 1,
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| data | Long | 新创建的活动ID |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1204 | 权限不足 |
| 1002 | 参数错误 |
| 422 | 参数校验失败 |

---

### 3.2 查询活动列表

**接口**: `GET /api/activities`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 分页查询活动列表,支持按状态、县域过滤。县级管理员只能查看本县活动,市级管理员可查看全部。

**请求头**:
```
Authorization: Bearer <access_token>
```

**请求参数**:

| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| status | String | Query | 否 | 活动状态:draft/ongoing/ended |
| countyCode | String | Query | 否 | 县域编码(市级管理员可用) |
| current | Long | Query | 否 | 当前页码,默认1 |
| size | Long | Query | 否 | 每页大小,默认10,最大100 |

**请求示例**:
```
GET /api/activities?status=ongoing&current=1&size=10
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "2024年春季教学活动",
      "description": "本次活动旨在提升教学质量",
      "scopeCountyCode": "PX",
      "scopeCountyName": "屏县",
      "startTime": "2024-03-01T08:00:00",
      "endTime": "2024-03-31T18:00:00",
      "endedTime": null,
      "createdId": 1,
      "createdUsername": "admin",
      "status": "ONGOING",
      "statusDesc": "进行中",
      "createdTime": "2024-02-28T10:00:00",
      "updatedTime": "2024-02-28T10:00:00"
    }
  ],
  "current": 1,
  "size": 10,
  "total": 1,
  "pages": 1,
  "hasPrevious": false,
  "hasNext": false,
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 活动ID |
| name | String | 活动名称 |
| description | String | 活动说明 |
| scopeCountyCode | String | 县域范围编码 |
| scopeCountyName | String | 县域范围名称 |
| startTime | String | 打卡开始时间 |
| endTime | String | 打卡结束时间 |
| endedTime | String | 结束操作时间 |
| createdId | Long | 发起管理员ID |
| createdUsername | String | 发起管理员用户名 |
| status | String | 活动状态枚举值 |
| statusDesc | String | 活动状态描述 |
| createdTime | String | 创建时间 |
| updatedTime | String | 更新时间 |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1204 | 权限不足 |

---

### 3.3 查询活动详情

**接口**: `GET /api/activities/{id}`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 查询活动详细信息及参与统计数据,包括参与教学点数量、总参与人数、评价数量。

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 活动ID |

**请求示例**:
```
GET /api/activities/1
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "activity": {
      "id": 1,
      "name": "2024年春季教学活动",
      "description": "本次活动旨在提升教学质量",
      "scopeCountyCode": "PX",
      "scopeCountyName": "屏县",
      "startTime": "2024-03-01T08:00:00",
      "endTime": "2024-03-31T18:00:00",
      "endedTime": null,
      "createdId": 1,
      "createdUsername": "admin",
      "status": "ONGOING",
      "statusDesc": "进行中",
      "createdTime": "2024-02-28T10:00:00",
      "updatedTime": "2024-02-28T10:00:00"
    },
    "participatedCount": 15,
    "totalAttendees": 450,
    "evaluationCount": 0
  },
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| activity | Object | 活动基本信息 |
| participatedCount | Integer | 参与教学点数量 |
| totalAttendees | Integer | 总参与人数 |
| evaluationCount | Integer | 评价数量 |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1301 | 活动不存在 |
| 1204 | 权限不足(非本县活动) |

---

### 3.4 结束活动

**接口**: `POST /api/activities/{id}/finish`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 手动结束活动,关联的打卡二维码将失效,操作会被记录到审计日志。

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 活动ID |

**请求示例**:
```
POST /api/activities/1/finish
```

**业务规则**:
- 只能结束"进行中"(ongoing)状态的活动
- 结束后活动状态变更为"已结束"(ended)
- 关联的打卡类型二维码自动失效
- 操作记录到审计日志

**响应示例**:

```json
{
  "code": 200,
  "message": "活动已成功结束",
  "timestamp": 1698765432000
}
```

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1301 | 活动不存在 |
| 1303 | 活动已结束 |
| 1204 | 权限不足(非本县活动) |

---

## 4. 县级账号管理

> **注意**: 本模块所有接口仅限市级管理员使用

### 4.1 创建县级账号

**接口**: `POST /api/admins`  
**权限**: 需要认证,仅市级管理员  
**角色要求**: `@RequireRole({"city"})`  
**描述**: 市级管理员创建县级管理员账号

**请求头**:
```
Authorization: Bearer <access_token>
```

**请求参数**:

```json
{
  "username": "PXadmin",
  "password": "password123",
  "countyCode": "PX"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名,长度1-64,唯一 |
| password | String | 是 | 初始密码,长度6-64 |
| countyCode | String | 是 | 县域编码,长度1-16 |

**响应示例**:

```json
{
  "code": 200,
  "message": "账号创建成功",
  "data": 2,
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| data | Long | 新创建的管理员ID |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1204 | 权限不足(非市级管理员) |
| 1103 | 用户名已存在 |
| 1002 | 参数错误 |

---

### 4.2 修改县级账号密码

**接口**: `PUT /api/admins/{id}/password`  
**权限**: 需要认证,仅市级管理员  
**角色要求**: `@RequireRole({"city"})`  
**描述**: 市级管理员修改县级管理员密码

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 管理员ID |

**请求参数**:

```json
{
  "newPassword": "newPassword123"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| newPassword | String | 是 | 新密码,长度6-64 |

**响应示例**:

```json
{
  "code": 200,
  "message": "密码修改成功",
  "timestamp": 1698765432000
}
```

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1204 | 权限不足 |
| 1101 | 用户不存在 |

---

### 4.3 启用/禁用县级账号

**接口**: `PATCH /api/admins/{id}/status`  
**权限**: 需要认证,仅市级管理员  
**角色要求**: `@RequireRole({"city"})`  
**描述**: 市级管理员启用或禁用县级管理员账号,操作记录到审计日志

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 管理员ID |

**请求参数**:

```json
{
  "status": "enabled"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | String | 是 | 状态:enabled(启用)/disabled(禁用) |

**响应示例**:

```json
{
  "code": 200,
  "message": "账号状态修改成功",
  "timestamp": 1698765432000
}
```

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1204 | 权限不足 |
| 1101 | 用户不存在 |

---

### 4.4 删除县级账号

**接口**: `DELETE /api/admins/{id}`  
**权限**: 需要认证,仅市级管理员  
**角色要求**: `@RequireRole({"city"})`  
**描述**: 软删除县级管理员账号,数据永久保留仅隐藏,操作记录到审计日志

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 管理员ID |

**请求示例**:
```
DELETE /api/admins/2
```

**响应示例**:

```json
{
  "code": 200,
  "message": "账号删除成功",
  "timestamp": 1698765432000
}
```

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1204 | 权限不足 |
| 1101 | 用户不存在 |

---

### 4.5 查询县级账号列表

**接口**: `GET /api/admins`  
**权限**: 需要认证,仅市级管理员  
**角色要求**: `@RequireRole({"city"})`  
**描述**: 分页查询县级管理员列表,支持按状态、县域过滤

**请求头**:
```
Authorization: Bearer <access_token>
```

**请求参数**:

| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| status | String | Query | 否 | 状态:enabled/disabled/deleted |
| countyCode | String | Query | 否 | 县域编码 |
| current | Long | Query | 否 | 当前页码,默认1 |
| size | Long | Query | 否 | 每页大小,默认10,最大100 |

**请求示例**:
```
GET /api/admins?status=enabled&current=1&size=10
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 2,
      "username": "PXadmin",
      "role": "county",
      "countyCode": "PX",
      "countyName": "屏县",
      "status": "enabled",
      "createdTime": "2024-02-28T10:00:00",
      "updatedTime": "2024-02-28T10:00:00"
    }
  ],
  "current": 1,
  "size": 10,
  "total": 1,
  "pages": 1,
  "hasPrevious": false,
  "hasNext": false,
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 管理员ID |
| username | String | 用户名 |
| role | String | 角色 |
| countyCode | String | 县域编码 |
| countyName | String | 县域名称 |
| status | String | 状态 |
| createdTime | String | 创建时间 |
| updatedTime | String | 更新时间 |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1204 | 权限不足 |

---

## 5. 教学点管理

### 5.1 创建教学点

**接口**: `POST /api/teaching-points`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 创建新的教学点，市级管理员可创建任意县域的教学点，县级管理员只能创建本县教学点

**请求头**:
```
Authorization: Bearer <access_token>
```

**请求参数**:

```json
{
  "name": "第一小学教学点",
  "countyCode": "PX"
}
```

|| 字段 | 类型 | 必填 | 说明 |
||------|------|------|------|
|| name | String | 是 | 教学点名称,长度2-128 |
|| countyCode | String | 是 | 归属县域编码,长度2-16 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1,
  "timestamp": 1698765432000
}
```

**错误码**:

|| 错误码 | 说明 |
||--------|------|
|| 1204 | 权限不足 |
|| 1803 | 教学点名称已存在 |
|| 1002 | 参数错误 |

---

### 5.2 更新教学点

**接口**: `PUT /api/teaching-points/{id}`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 更新教学点名称或县域信息

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

|| 参数 | 类型 | 必填 | 说明 |
||------|------|------|------|
|| id | Long | 是 | 教学点ID |

**请求参数**:

```json
{
  "name": "第一小学教学点（新）",
  "countyCode": "PX"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "timestamp": 1698765432000
}
```

**错误码**:

|| 错误码 | 说明 |
||--------|------|
|| 1801 | 教学点不存在 |
|| 1803 | 教学点名称已存在 |
|| 1204 | 权限不足 |

---

### 5.3 删除教学点

**接口**: `DELETE /api/teaching-points/{id}`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 软删除教学点，数据永久保留仅隐藏

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

|| 参数 | 类型 | 必填 | 说明 |
||------|------|------|------|
|| id | Long | 是 | 教学点ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "timestamp": 1698765432000
}
```

**错误码**:

|| 错误码 | 说明 |
||--------|------|
|| 1801 | 教学点不存在 |
|| 1204 | 权限不足 |

---

### 5.4 启用教学点

**接口**: `PUT /api/teaching-points/{id}/enable`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 将教学点状态设置为启用

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

|| 参数 | 类型 | 必填 | 说明 |
||------|------|------|------|
|| id | Long | 是 | 教学点ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "timestamp": 1698765432000
}
```

---

### 5.5 禁用教学点

**接口**: `PUT /api/teaching-points/{id}/disable`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 将教学点状态设置为禁用

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

|| 参数 | 类型 | 必填 | 说明 |
||------|------|------|------|
|| id | Long | 是 | 教学点ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "timestamp": 1698765432000
}
```

---

### 5.6 查询教学点详情

**接口**: `GET /api/teaching-points/{id}`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 根据ID查询教学点的详细信息

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

|| 参数 | 类型 | 必填 | 说明 |
||------|------|------|------|
|| id | Long | 是 | 教学点ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "第一小学教学点",
    "countyCode": "PX",
    "countyName": "屏县",
    "status": "enabled",
    "statusDesc": "启用",
    "createdTime": "2024-11-01T12:00:00",
    "updatedTime": "2024-11-01T12:00:00"
  },
  "timestamp": 1698765432000
}
```

**错误码**:

|| 错误码 | 说明 |
||--------|------|
|| 1801 | 教学点不存在 |
|| 1204 | 权限不足 |

---

### 5.7 查询教学点列表

**接口**: `GET /api/teaching-points`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 分页查询教学点列表,支持按县域、状态过滤。县级管理员只能查看本县教学点。

**请求头**:
```
Authorization: Bearer <access_token>
```

**请求参数**:

| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| countyCode | String | Query | 否 | 县域编码(市级管理员可用) |
| status | String | Query | 否 | 状态:enabled/disabled/deleted |
| current | Long | Query | 否 | 当前页码,默认1 |
| size | Long | Query | 否 | 每页大小,默认10,最大100 |

**请求示例**:
```
GET /api/teaching-points?countyCode=PX&status=enabled&current=1&size=10
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "第一小学教学点",
      "countyCode": "PX",
      "countyName": "屏县",
      "status": "enabled",
      "createdTime": "2024-02-28T10:00:00",
      "updatedTime": "2024-02-28T10:00:00"
    }
  ],
  "current": 1,
  "size": 10,
  "total": 1,
  "pages": 1,
  "hasPrevious": false,
  "hasNext": false,
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 教学点ID |
| name | String | 教学点名称 |
| countyCode | String | 归属县域编码 |
| countyName | String | 归属县域名称 |
| status | String | 状态 |
| createdTime | String | 创建时间 |
| updatedTime | String | 更新时间 |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1204 | 权限不足 |

---

## 6. 二维码管理

### 6.1 生成二维码

**接口**: `POST /api/activities/{id}/qrcodes`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 为活动生成打卡或评价二维码

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 活动ID |

**请求参数**:

```json
{
  "type": "evaluation",
  "expireTime": "2024-04-07T23:59:59"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | String | 是 | 类型:checkin(打卡)/evaluation(评价) |
| expireTime | String | 否 | 过期时间,ISO 8601格式,默认7天后 |

**业务规则**:
- 打卡二维码(checkin):活动创建时自动生成,也可手动生成
- 评价二维码(evaluation):活动结束后才能生成,默认有效期7天
- 二维码包含签名防篡改

**响应示例**:

```json
{
  "code": 200,
  "message": "二维码生成成功",
  "data": {
    "id": 1,
    "activityId": 1,
    "type": "evaluation",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "url": "https://example.com/qr?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expireTime": "2024-04-07T23:59:59",
    "status": "enabled"
  },
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 二维码ID |
| activityId | Long | 关联活动ID |
| type | String | 类型 |
| token | String | 二维码令牌 |
| url | String | 二维码访问URL |
| expireTime | String | 过期时间 |
| status | String | 状态 |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1301 | 活动不存在 |
| 1204 | 权限不足 |
| 1603 | 活动未结束,不能生成评价二维码 |

---

### 6.2 查询二维码列表

**接口**: `GET /api/qrcodes`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 分页查询二维码列表,支持按活动、类型、状态过滤

**请求头**:
```
Authorization: Bearer <access_token>
```

**请求参数**:

| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| activityId | Long | Query | 否 | 活动ID |
| type | String | Query | 否 | 类型:checkin/evaluation |
| status | String | Query | 否 | 状态:enabled/disabled/deleted |
| current | Long | Query | 否 | 当前页码,默认1 |
| size | Long | Query | 否 | 每页大小,默认10,最大100 |

**请求示例**:
```
GET /api/qrcodes?activityId=1&type=evaluation&current=1&size=10
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "activityId": 1,
      "activityName": "2024年春季教学活动",
      "type": "evaluation",
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "url": "https://example.com/qr?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "expireTime": "2024-04-07T23:59:59",
      "disabledTime": null,
      "status": "enabled",
      "createdTime": "2024-03-31T18:00:00",
      "updatedTime": "2024-03-31T18:00:00"
    }
  ],
  "current": 1,
  "size": 10,
  "total": 1,
  "pages": 1,
  "hasPrevious": false,
  "hasNext": false,
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 二维码ID |
| activityId | Long | 关联活动ID |
| activityName | String | 活动名称 |
| type | String | 类型 |
| token | String | 二维码令牌 |
| url | String | 二维码访问URL |
| expireTime | String | 过期时间 |
| disabledTime | String | 禁用时间 |
| status | String | 状态 |
| createdTime | String | 创建时间 |
| updatedTime | String | 更新时间 |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1204 | 权限不足 |

---

### 6.3 禁用二维码

**接口**: `PATCH /api/qrcodes/{id}/disable`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 禁用二维码,防止泄露后越权操作,操作记录到审计日志

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 二维码ID |

**请求示例**:
```
PATCH /api/qrcodes/1/disable
```

**响应示例**:

```json
{
  "code": 200,
  "message": "二维码已禁用",
  "timestamp": 1698765432000
}
```

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1501 | 二维码不存在 |
| 1204 | 权限不足 |

---

## 7. 参与端接口(无需登录)

> **注意**: 本模块所有接口无需JWT认证,通过二维码令牌进行校验

### 7.1 打卡提交

**接口**: `POST /api/checkins/checkin`  
**权限**: 无需认证  
**描述**: 教学点扫描打卡二维码后提交打卡信息,包括教学点和实到人数

**请求参数**:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "teachingPointId": 1,
  "attendeeCount": 30
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| token | String | 是 | 二维码令牌 |
| teachingPointId | Long | 是 | 教学点ID |
| attendeeCount | Integer | 是 | 实到人数,必须为正整数 |

**业务规则**:
- 二维码必须是打卡类型(checkin)
- 二维码必须有效且未禁用
- 二维码未过期
- 活动必须在进行中(ongoing)状态
- 同一活动同一教学点只能打卡一次(幂等)
- 实到人数必须大于0

**响应示例**:

```json
{
  "code": 200,
  "message": "打卡成功",
  "data": {
    "success": true,
    "submittedTime": "2024-03-15T14:30:00"
  },
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| success | Boolean | 是否成功 |
| submittedTime | String | 提交时间 |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1501 | 二维码无效 |
| 1502 | 二维码已过期 |
| 1301 | 活动不存在 |
| 1302 | 活动未开始 |
| 1303 | 活动已结束 |
| 1402 | 该教学点已打卡 |
| 1002 | 参数错误 |

---

### 7.2 活动评价提交

**接口**: `POST /api/checkins/evaluate`  
**权限**: 无需认证  
**描述**: 教学点扫描评价二维码后提交活动评价,包括选择题和文字建议

**请求参数**:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "teachingPointId": 1,
  "q1Satisfaction": 3,
  "q2Practicality": 3,
  "q3Quality": 2,
  "suggestionText": "活动内容很实用,希望能多举办类似活动"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| token | String | 是 | 二维码令牌 |
| teachingPointId | Long | 是 | 教学点ID |
| q1Satisfaction | Integer | 是 | 满意度(1-3):1不满意,2一般,3满意 |
| q2Practicality | Integer | 是 | 实用性(1-3):1弱,2中,3强 |
| q3Quality | Integer | 否 | 质量(1-3):1差,2中,3好 |
| suggestionText | String | 否 | 建议,最多200字 |

**业务规则**:
- 二维码必须是评价类型(evaluation)
- 二维码必须有效且未禁用
- 二维码未过期
- 活动必须已结束(ended)状态
- 该教学点必须参与过该活动(有打卡记录)
- 同一活动同一教学点只能评价一次(防重复)
- 选择题答案必须在1-3范围内

**响应示例**:

```json
{
  "code": 200,
  "message": "评价提交成功",
  "data": {
    "success": true,
    "submittedTime": "2024-04-01T10:00:00"
  },
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| success | Boolean | 是否成功 |
| submittedTime | String | 提交时间 |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1501 | 二维码无效 |
| 1502 | 二维码已过期 |
| 1301 | 活动不存在 |
| 1603 | 不允许评价(活动未结束) |
| 1401 | 无参与记录,无法评价 |
| 1602 | 已评价过该活动 |
| 1002 | 参数错误 |

---

## 8. 数据看板与统计

### 8.1 总览看板

**接口**: `GET /api/dashboard/summary`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 展示平台总活动数、进行中活动数、累计参与教学点数、累计参与人数。县级管理员只能看本县数据。

**请求头**:
```
Authorization: Bearer <access_token>
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalActivities": 50,
    "ongoingActivities": 5,
    "totalTeachingPointsParticipated": 200,
    "totalAttendees": 6000
  },
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| totalActivities | Integer | 总活动数 |
| ongoingActivities | Integer | 进行中活动数 |
| totalTeachingPointsParticipated | Integer | 累计参与教学点数(去重) |
| totalAttendees | Integer | 累计参与人数 |

**数据刷新**: 建议每10分钟自动刷新一次

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1204 | 权限不足 |

---

### 8.2 活动参与列表

**接口**: `GET /api/activities/{id}/participants`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 查询活动的参与教学点列表,包括教学点名称、实到人数、提交时间

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 活动ID |

**请求参数**:

| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| current | Long | Query | 否 | 当前页码,默认1 |
| size | Long | Query | 否 | 每页大小,默认10,最大100 |

**请求示例**:
```
GET /api/activities/1/participants?current=1&size=10
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "teachingPointId": 1,
      "teachingPointName": "第一小学教学点",
      "countyCode": "PX",
      "countyName": "屏县",
      "attendeeCount": 30,
      "submittedTime": "2024-03-15T14:30:00"
    }
  ],
  "current": 1,
  "size": 10,
  "total": 1,
  "pages": 1,
  "hasPrevious": false,
  "hasNext": false,
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| teachingPointId | Long | 教学点ID |
| teachingPointName | String | 教学点名称 |
| countyCode | String | 县域编码 |
| countyName | String | 县域名称 |
| attendeeCount | Integer | 实到人数 |
| submittedTime | String | 提交时间 |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1301 | 活动不存在 |
| 1204 | 权限不足 |

---

### 8.3 活动评价汇总

**接口**: `GET /api/activities/{id}/evaluations/summary`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 查询活动评价的汇总统计数据,用于生成饼图等可视化图表

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 活动ID |

**请求示例**:
```
GET /api/activities/1/evaluations/summary
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalEvaluations": 15,
    "q1Satisfaction": {
      "unsatisfied": 1,
      "neutral": 4,
      "satisfied": 10
    },
    "q2Practicality": {
      "weak": 0,
      "medium": 5,
      "strong": 10
    },
    "q3Quality": {
      "poor": 0,
      "medium": 3,
      "good": 12
    }
  },
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| totalEvaluations | Integer | 总评价数 |
| q1Satisfaction | Object | 满意度统计 |
| q1Satisfaction.unsatisfied | Integer | 不满意数量(1) |
| q1Satisfaction.neutral | Integer | 一般数量(2) |
| q1Satisfaction.satisfied | Integer | 满意数量(3) |
| q2Practicality | Object | 实用性统计 |
| q2Practicality.weak | Integer | 弱数量(1) |
| q2Practicality.medium | Integer | 中数量(2) |
| q2Practicality.strong | Integer | 强数量(3) |
| q3Quality | Object | 质量统计 |
| q3Quality.poor | Integer | 差数量(1) |
| q3Quality.medium | Integer | 中数量(2) |
| q3Quality.good | Integer | 好数量(3) |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1301 | 活动不存在 |
| 1204 | 权限不足 |

---

### 8.4 活动评价明细

**接口**: `GET /api/activities/{id}/evaluations/detail`  
**权限**: 需要认证,市级或县级管理员  
**角色要求**: `@RequireRole({"city", "county"})`  
**描述**: 查询活动评价的详细信息,逐教学点展示评价内容

**请求头**:
```
Authorization: Bearer <access_token>
```

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 活动ID |

**请求参数**:

| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| current | Long | Query | 否 | 当前页码,默认1 |
| size | Long | Query | 否 | 每页大小,默认10,最大100 |

**请求示例**:
```
GET /api/activities/1/evaluations/detail?current=1&size=10
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "teachingPointId": 1,
      "teachingPointName": "第一小学教学点",
      "countyCode": "PX",
      "countyName": "屏县",
      "q1Satisfaction": 3,
      "q2Practicality": 3,
      "q3Quality": 2,
      "suggestionText": "活动内容很实用,希望能多举办类似活动",
      "submittedTime": "2024-04-01T10:00:00"
    }
  ],
  "current": 1,
  "size": 10,
  "total": 1,
  "pages": 1,
  "hasPrevious": false,
  "hasNext": false,
  "timestamp": 1698765432000
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| teachingPointId | Long | 教学点ID |
| teachingPointName | String | 教学点名称 |
| countyCode | String | 县域编码 |
| countyName | String | 县域名称 |
| q1Satisfaction | Integer | 满意度(1-3) |
| q2Practicality | Integer | 实用性(1-3) |
| q3Quality | Integer | 质量(1-3) |
| suggestionText | String | 建议文本 |
| submittedTime | String | 提交时间 |

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 1301 | 活动不存在 |
| 1204 | 权限不足 |

---

## 9. 错误码一览

### 9.1 通用错误码

| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| 200 | 操作成功 | 200 |
| 400 | 请求参数错误 | 400 |
| 401 | 未授权访问 | 401 |
| 403 | 禁止访问 | 403 |
| 404 | 资源不存在 | 404 |
| 405 | 请求方法不允许 | 405 |
| 409 | 资源冲突 | 409 |
| 422 | 参数校验失败 | 422 |
| 429 | 请求过于频繁 | 429 |
| 500 | 服务器内部错误 | 500 |
| 502 | 网关错误 | 502 |
| 503 | 服务不可用 | 503 |
| 504 | 网关超时 | 504 |

### 9.2 业务错误码

#### 系统错误 (10xx)

| 错误码 | 说明 |
|--------|------|
| 1000 | 业务处理失败 |
| 1001 | 系统错误 |
| 1002 | 参数错误 |

#### 用户相关错误 (11xx)

| 错误码 | 说明 |
|--------|------|
| 1101 | 用户不存在 |
| 1102 | 用户已被禁用 |
| 1103 | 用户已存在 |
| 1104 | 密码错误 |

#### 认证授权错误 (12xx)

| 错误码 | 说明 |
|--------|------|
| 1201 | 令牌无效 |
| 1202 | 令牌已过期 |
| 1203 | 令牌缺失 |
| 1204 | 权限不足 |
| 1205 | 请先登录 |

#### 活动相关错误 (13xx)

| 错误码 | 说明 |
|--------|------|
| 1301 | 活动不存在 |
| 1302 | 活动未开始 |
| 1303 | 活动已结束 |
| 1304 | 活动人数已满 |
| 1305 | 活动已禁用 |

#### 打卡相关错误 (14xx)

| 错误码 | 说明 |
|--------|------|
| 1401 | 打卡记录不存在 |
| 1402 | 今日已打卡 |
| 1403 | 打卡时间无效 |
| 1404 | 打卡位置无效 |

#### 二维码相关错误 (15xx)

| 错误码 | 说明 |
|--------|------|
| 1501 | 二维码无效 |
| 1502 | 二维码已过期 |
| 1503 | 二维码已使用 |
| 1504 | 二维码生成失败 |

#### 评价相关错误 (16xx)

| 错误码 | 说明 |
|--------|------|
| 1601 | 评价不存在 |
| 1602 | 已评价过该活动 |
| 1603 | 不允许评价 |

#### 数据相关错误 (17xx)

| 错误码 | 说明 |
|--------|------|
| 1701 | 数据不存在 |
| 1702 | 数据已存在 |
| 1703 | 数据完整性约束违反 |

#### 文件相关错误 (18xx)

| 错误码 | 说明 |
|--------|------|
| 1801 | 文件不存在 |
| 1802 | 文件上传失败 |
| 1803 | 文件类型不支持 |
| 1804 | 文件大小超出限制 |

#### 外部服务错误 (19xx)

| 错误码 | 说明 |
|--------|------|
| 1901 | 微信接口调用失败 |
| 1902 | 短信发送失败 |
| 1903 | 邮件发送失败 |

---

## 10. 附录

### 10.1 数据字典

#### 活动状态 (ActivityStatus)

| 值 | 说明 |
|------|------|
| draft | 草稿 |
| ongoing | 进行中 |
| ended | 已结束 |

#### 用户角色 (UserRole)

| 值 | 说明 |
|------|------|
| city | 市级管理员 |
| county | 县级管理员 |

#### 账号状态 (AdminStatus)

| 值 | 说明 |
|------|------|
| enabled | 启用 |
| disabled | 禁用 |
| deleted | 已删除 |

#### 二维码类型 (QrCodeType)

| 值 | 说明 |
|------|------|
| checkin | 打卡二维码 |
| evaluation | 评价二维码 |

#### 二维码状态 (QrCodeStatus)

| 值 | 说明 |
|------|------|
| enabled | 有效 |
| disabled | 已禁用 |
| deleted | 已删除 |

#### 评价选项 (EvaluationOptions)

**满意度 (q1Satisfaction)**:
- 1: 不满意
- 2: 一般
- 3: 满意

**实用性 (q2Practicality)**:
- 1: 弱
- 2: 中
- 3: 强

**质量 (q3Quality)**:
- 1: 差
- 2: 中
- 3: 好

### 10.2 变更历史

| 版本 | 日期 | 说明 |
|------|--------|------|
| v1.0.0 | 2025-10-30 | 初始版本,定义了所有核心接口 |

### 10.3 联系方式

- **项目仓库**: [GitHub/WeChat-CheckIn]
- **技术支持**: support@wechat-checkin.com
- **文档维护**: dev@wechat-checkin.com

### 10.4 使用示例

#### Postman 示例

**1. 登录获取Token**

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

**2. 创建活动**

```bash
POST http://localhost:8080/api/activities
Authorization: Bearer <your_access_token>
Content-Type: application/json

{
  "name": "2024年春季教学活动",
  "description": "本次活动旨在提升教学质量",
  "startTime": "2024-03-01T08:00:00",
  "endTime": "2024-03-31T18:00:00"
}
```

**3. 打卡提交(无需Token)**

```bash
POST http://localhost:8080/api/checkins/checkin
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "teachingPointId": 1,
  "attendeeCount": 30
}
```

#### cURL 示例

**1. 登录**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

**2. 查询活动列表**

```bash
curl -X GET "http://localhost:8080/api/activities?status=ongoing&current=1&size=10" \
  -H "Authorization: Bearer <your_access_token>"
```

**3. 结束活动**

```bash
curl -X POST http://localhost:8080/api/activities/1/finish \
  -H "Authorization: Bearer <your_access_token>"
```

### 10.5 注意事项

1. **认证令牌**:
   - 访问令牌默认有效期为24小时
   - 刷新令牌有效期为7天
   - 令牌过期后需重新登录

2. **分页参数**:
   - 页码从1开始
   - 每页最大数量为100
   - 建议每页显示10-20条数据

3. **日期时间格式**:
   - 统一使用ISO 8601格式: `yyyy-MM-dd'T'HH:mm:ss`
   - 时区为东八区(UTC+8)

4. **数据权限**:
   - 市级管理员可查看全市数据
   - 县级管理员仅能查看本县数据
   - 越权访问将返回403错误

5. **幂等性**:
   - 打卡接口支持幂等,重复提交不会创建重复数据
   - 评价接口支持幂等,重复提交不会创建重复数据

6. **限流**:
   - 同一IP每秒最多100次请求
   - 超过限制将返回429错误码

7. **安全建议**:
   - 生产环境必须使用HTTPS
   - 定期更换管理员密码
   - 及时禁用泄露的二维码

---

## 结束语

本文档提供了微信打卡小程序后端系统的完整API接口说明。如有任何疑问或建议,请联系技术支持团队。

**文档版本**: v1.0.0  
**最后更新**: 2025-10-30  
**状态**: Draft