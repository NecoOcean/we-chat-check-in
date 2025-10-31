# 打卡参与模块 (we-chat-checkins)

## 模块简介

打卡参与模块负责处理教学点的打卡提交、数据校验、幂等处理和统计功能。该模块支持参与端（无需登录）快速提交打卡，以及管理端的数据查询和统计。

## 核心功能

### 1. 打卡提交（参与端接口，无需登录）
- 通过二维码令牌验证
- 支持教学点选择和人数输入
- 实时数据入库
- 幂等处理（同一活动同一教学点只能打卡一次）
- 自动时间记录和二维码来源追踪

### 2. 打卡数据查询（管理端接口，需要登录）
- 分页查询打卡记录
- 支持按活动、教学点过滤
- 权限隔离（市级查看全部，县级查看本县）
- 显示活动名称、教学点名称等关联信息

### 3. 打卡统计
- 查询活动的参与教学点数
- 查询活动的累计参与人数
- 实时聚合计算

## 技术栈

- **MyBatis Plus**: 数据库操作（ORM）
- **Spring Boot**: Web框架和依赖注入
- **Spring Security**: 权限校验（管理端接口）
- **Hutool**: 工具类库
- **Validation**: 参数校验

## 主要组件

### Entity（实体类）
- `Checkin`: 打卡实体，对应数据库表 `checkins`

### Mapper（数据访问层）
- `CheckinMapper`: 打卡数据访问接口

### Service（服务层）
- `CheckinService`: 打卡服务接口
- `CheckinServiceImpl`: 打卡服务实现

### Controller（控制器）
- `CheckinController`: 打卡管理API接口

### DTO（数据传输对象）
- `CheckinSubmitRequest`: 打卡提交请求
- `CheckinQueryRequest`: 打卡查询请求

### VO（视图对象）
- `CheckinVO`: 打卡响应对象
- `CheckinSubmitResponseVO`: 打卡成功响应
- `CheckinStatisticsVO`: 打卡统计信息

## API接口

### 参与端接口（无需登录）

#### 1. 提交打卡
```http
POST /api/checkins/checkin
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "teachingPointId": 1,
  "attendeeCount": 30
}
```

**请求参数说明**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| token | String | 是 | 二维码令牌 |
| teachingPointId | Long | 是 | 教学点ID |
| attendeeCount | Integer | 是 | 实到人数，必须为正整数 |

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
    "submittedTime": "2024-03-15T14:30:00",
    "checkinId": 1
  },
  "timestamp": 1698765432000
}
```

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

### 管理端接口（需要登录）

#### 2. 查询打卡记录列表
```http
GET /api/checkins?activityId=1&teachingPointId=1&current=1&size=10
Authorization: Bearer <access_token>
```

**请求参数说明**:

| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| activityId | Long | Query | 否 | 活动ID |
| teachingPointId | Long | Query | 否 | 教学点ID |
| current | Long | Query | 否 | 当前页码，默认1 |
| size | Long | Query | 否 | 每页大小，默认10，最大100 |

**权限**: 市级/县级管理员（县级仅可查看本县数据）

**响应示例**:

```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "activityId": 1,
      "activityName": "2024年春季教学活动",
      "teachingPointId": 1,
      "teachingPointName": "第一小学教学点",
      "attendeeCount": 30,
      "submittedTime": "2024-03-15T14:30:00",
      "sourceQrcodeId": 1
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

#### 3. 查询打卡统计信息
```http
GET /api/checkins/statistics/1
Authorization: Bearer <access_token>
```

**请求参数说明**:

| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| activityId | Long | Path | 是 | 活动ID |

**权限**: 市级/县级管理员

**响应示例**:

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "activityId": 1,
    "participatingTeachingPoints": 5,
    "totalAttendees": 150
  },
  "timestamp": 1698765432000
}
```

## 业务流程

### 打卡流程
1. 教学点管理员扫描打卡二维码
2. 前端获取二维码令牌（token）
3. 用户选择教学点和输入实到人数
4. 前端调用打卡提交接口
5. 服务端验证二维码：
   - 验证令牌签名和有效期
   - 检查二维码状态（是否禁用）
   - 确保是打卡类型的二维码
6. 验证活动信息：
   - 活动是否存在
   - 活动是否在进行中
   - 活动是否已开始且未结束
7. 幂等性检查：
   - 同一活动同一教学点是否已打卡
8. 创建打卡记录并入库
9. 返回打卡成功响应

### 数据权限隔离
- **市级管理员**: 可查看全市所有打卡记录
- **县级管理员**: 仅可查看本县教学点的打卡记录

## 数据库表

### checkins 表结构

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| activity_id | BIGINT | 活动ID（外键） |
| teaching_point_id | BIGINT | 教学点ID（外键） |
| attendee_count | INT | 实到人数 |
| submitted_time | DATETIME | 提交时间 |
| source_qrcode_id | BIGINT | 来源二维码ID（外键） |

**约束**:
- PRIMARY KEY: `id`
- UNIQUE KEY: `(activity_id, teaching_point_id)` - 幂等约束
- FOREIGN KEY: `activity_id` -> `activities.id`
- FOREIGN KEY: `teaching_point_id` -> `teaching_points.id`
- FOREIGN KEY: `source_qrcode_id` -> `qrcodes.id`
- INDEX: `(activity_id, submitted_time)`
- INDEX: `(teaching_point_id)`
- INDEX: `(source_qrcode_id)`

## 错误处理

所有业务异常均映射为标准错误码：

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| 1001 | 400 | 系统错误 |
| 1002 | 400 | 参数错误 |
| 1301 | 404 | 活动不存在 |
| 1302 | 400 | 活动未开始 |
| 1303 | 400 | 活动已结束 |
| 1402 | 409 | 该教学点已打卡（冲突） |
| 1501 | 400 | 二维码无效 |
| 1502 | 400 | 二维码已过期 |

## 使用示例

### 提交打卡（参与端）

```java
@Autowired
private CheckinService checkinService;

public void submitCheckin() {
    CheckinSubmitRequest request = new CheckinSubmitRequest();
    request.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
    request.setTeachingPointId(1L);
    request.setAttendeeCount(30);
    
    CheckinSubmitResponseVO response = checkinService.submitCheckin(request);
    
    System.out.println("打卡成功: " + response.getSuccess());
    System.out.println("打卡ID: " + response.getCheckinId());
}
```

### 查询打卡记录（管理端）

```java
@Autowired
private CheckinService checkinService;

public void queryCheckins() {
    CheckinQueryRequest request = new CheckinQueryRequest();
    request.setActivityId(1L);
    request.setCurrent(1L);
    request.setSize(10L);
    
    Page<CheckinVO> page = checkinService.queryCheckins(request);
    
    System.out.println("总记录数: " + page.getTotal());
    for (CheckinVO checkin : page.getRecords()) {
        System.out.println("活动: " + checkin.getActivityName());
        System.out.println("教学点: " + checkin.getTeachingPointName());
        System.out.println("参与人数: " + checkin.getAttendeeCount());
    }
}
```

### 查询打卡统计

```java
@Autowired
private CheckinService checkinService;

public void getStatistics() {
    CheckinStatisticsVO stats = checkinService.getCheckinStatistics(1L);
    
    System.out.println("参与教学点数: " + stats.getParticipatingTeachingPoints());
    System.out.println("累计参与人数: " + stats.getTotalAttendees());
}
```

## 性能优化

### 索引策略
- `(activity_id, submitted_time)`: 用于快速查询活动的打卡记录并按时间排序
- `(teaching_point_id)`: 用于按教学点查询
- `(source_qrcode_id)`: 用于追踪二维码来源

### 幂等性设计
- 利用数据库唯一约束 `(activity_id, teaching_point_id)` 保证幂等
- 避免重复打卡，提高数据准确性

## 安全性设计

1. **参与端接口**:
   - 通过二维码令牌验证，无需登录
   - 二维码含签名，防止伪造
   - 支持过期时间控制
   - 支持禁用机制

2. **管理端接口**:
   - 需要JWT认证
   - 权限隔离（市级/县级）
   - 数据权限过滤

## 注意事项

1. **打卡时间**: 打卡记录中的submitted_time由服务端自动设置，客户端不可修改
2. **幂等性**: 同一活动同一教学点只能打卡一次，重复提交会返回1402错误
3. **人数限制**: attendeeCount必须为正整数，不能为0或负数
4. **二维码有效期**: 确保打卡二维码在有效期内未过期
5. **活动状态**: 打卡只能在活动进行中时进行，不能在活动开始前或结束后打卡

## 后续优化

1. 支持打卡批量提交
2. 支持打卡数据导出（Excel）
3. 添加打卡提醒和通知功能
4. 支持自定义错误提示文案
5. 优化大数据量查询性能（添加缓存）

## 相关文档

- [系统架构说明](../../doc/系统架构说明.md)
- [API接口文档](../../doc/api.md)
- [数据库设计文档](../../doc/数据库设计文档.md)
- [功能概述](../../doc/功能概述.md)
