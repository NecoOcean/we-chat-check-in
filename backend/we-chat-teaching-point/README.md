# 教学点管理模块 (we-chat-teaching-point)

## 📋 模块简介

教学点管理模块提供教学点的完整生命周期管理功能，包括创建、查询、更新、删除、启用/禁用等操作。教学点是打卡和评价功能的基础数据，归属于特定县域。

## 🎯 核心功能

### 1. 教学点管理

- ✅ **创建教学点** - 市级和县级管理员可以创建新的教学点
- ✅ **更新教学点** - 修改教学点名称或县域信息
- ✅ **删除教学点** - 软删除教学点，数据永久保留
- ✅ **启用/禁用** - 管理教学点的可用状态

### 2. 教学点查询

- ✅ **分页查询列表** - 支持多条件过滤和分页
- ✅ **查询详情** - 获取单个教学点的详细信息
- ✅ **权限隔离** - 县级管理员只能查看本县教学点

### 3. 数据校验

- ✅ **名称唯一性** - 同一县域内教学点名称不能重复
- ✅ **权限校验** - 县级管理员只能操作本县教学点
- ✅ **状态检查** - 防止操作已删除的教学点

## 🏗️ 技术架构

### 分层结构

```
teaching-point
├── entity/               # 实体类
│   └── TeachingPoint    # 教学点实体
├── mapper/              # 数据访问层
│   └── TeachingPointMapper
├── service/             # 业务逻辑层
│   ├── TeachingPointService
│   └── impl/
│       └── TeachingPointServiceImpl
├── controller/          # 控制层
│   └── TeachingPointController
├── dto/                 # 数据传输对象
│   ├── CreateTeachingPointRequest
│   ├── UpdateTeachingPointRequest
│   └── TeachingPointQueryRequest
└── vo/                  # 视图对象
    └── TeachingPointVO
```

### 依赖关系

```
we-chat-teaching-point
├── we-chat-common       # 公共模块
└── we-chat-auth         # 认证模块
```

## 📊 数据模型

### 教学点实体 (TeachingPoint)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键ID |
| name | String | 教学点名称 |
| countyCode | String | 归属县域编码 |
| status | StatusEnum | 状态（启用/禁用/已删除） |
| createdTime | LocalDateTime | 创建时间 |
| updatedTime | LocalDateTime | 更新时间 |

## 🔌 API 接口

### 1. 创建教学点

**接口**: `POST /api/teaching-points`  
**权限**: 市级或县级管理员  
**请求体**:
```json
{
  "name": "第一小学教学点",
  "countyCode": "PX"
}
```
**响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1
}
```

### 2. 更新教学点

**接口**: `PUT /api/teaching-points/{id}`  
**权限**: 市级或县级管理员  
**请求体**:
```json
{
  "name": "第一小学教学点（新）",
  "countyCode": "PX"
}
```

### 3. 删除教学点

**接口**: `DELETE /api/teaching-points/{id}`  
**权限**: 市级或县级管理员  
**说明**: 软删除，数据仅标记为已删除状态

### 4. 启用/禁用教学点

**接口**: 
- `PUT /api/teaching-points/{id}/enable` - 启用
- `PUT /api/teaching-points/{id}/disable` - 禁用

**权限**: 市级或县级管理员

### 5. 查询教学点详情

**接口**: `GET /api/teaching-points/{id}`  
**权限**: 市级或县级管理员  
**响应**:
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
  }
}
```

### 6. 分页查询教学点列表

**接口**: `GET /api/teaching-points`  
**权限**: 市级或县级管理员  
**查询参数**:
- `name` - 教学点名称（模糊查询）
- `countyCode` - 县域编码（市级管理员可用）
- `status` - 状态（enabled/disabled/deleted）
- `current` - 当前页码（默认1）
- `size` - 每页大小（默认10）

**响应**:
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
      "statusDesc": "启用",
      "createdTime": "2024-11-01T12:00:00",
      "updatedTime": "2024-11-01T12:00:00"
    }
  ],
  "total": 1,
  "current": 1,
  "size": 10,
  "pages": 1
}
```

## 🔐 权限控制

### 权限规则

| 操作 | 市级管理员 | 县级管理员 |
|------|-----------|-----------|
| 创建教学点 | ✅ 任意县域 | ✅ 仅本县 |
| 修改教学点名称 | ✅ 所有教学点 | ✅ 仅本县教学点 |
| 修改教学点县域 | ✅ 所有教学点 | ❌ 不允许（v1.0.2修复）|
| 删除教学点 | ✅ 所有教学点 | ✅ 仅本县教学点 |
| 启用/禁用 | ✅ 所有教学点 | ✅ 仅本县教学点 |
| 查询详情 | ✅ 所有教学点 | ✅ 仅本县教学点 |
| 查询列表 | ✅ 全部数据 | ✅ 仅本县数据 |

**⚠️ 重要**: 为防止教学点跨县域转移导致权限丢失，县级管理员不能修改教学点的县域归属。

### 数据隔离

- **市级管理员**: 可以查看和操作所有县域的教学点
- **县级管理员**: 只能查看和操作本县的教学点，无法访问其他县域的数据

## 🚦 业务规则

### 1. 名称唯一性

- 同一县域内教学点名称必须唯一
- 不同县域可以有相同名称的教学点
- 更新时需检查新名称在目标县域内是否已存在

### 2. 软删除策略

- 删除教学点时不会物理删除数据
- 将状态标记为 `DELETED`
- 已删除的教学点不可恢复，不可再次操作
- 默认查询列表不显示已删除的教学点

### 3. 状态管理

- **ENABLED（启用）**: 教学点可正常使用
- **DISABLED（禁用）**: 教学点暂时停用，可重新启用
- **DELETED（已删除）**: 教学点已软删除，不可恢复

## 💡 使用示例

### cURL 示例

#### 1. 创建教学点
```bash
curl -X POST http://localhost:8080/api/teaching-points \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "第一小学教学点",
    "countyCode": "PX"
  }'
```

#### 2. 查询教学点列表
```bash
curl -X GET "http://localhost:8080/api/teaching-points?status=enabled&current=1&size=10" \
  -H "Authorization: Bearer <access_token>"
```

#### 3. 更新教学点
```bash
curl -X PUT http://localhost:8080/api/teaching-points/1 \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "第一小学教学点（更新）"
  }'
```

#### 4. 禁用教学点
```bash
curl -X PUT http://localhost:8080/api/teaching-points/1/disable \
  -H "Authorization: Bearer <access_token>"
```

#### 5. 删除教学点
```bash
curl -X DELETE http://localhost:8080/api/teaching-points/1 \
  -H "Authorization: Bearer <access_token>"
```

## ⚠️ 错误码

| 错误码 | 说明 |
|--------|------|
| 1801 | 教学点不存在 |
| 1802 | 教学点已存在 |
| 1803 | 教学点名称已存在 |
| 1804 | 教学点已被删除 |
| 1204 | 权限不足 |
| 1002 | 参数错误 |

## 📝 开发说明

### 添加新功能

1. 在 `TeachingPointService` 接口中定义新方法
2. 在 `TeachingPointServiceImpl` 中实现业务逻辑
3. 在 `TeachingPointController` 中添加API接口
4. 更新相关DTO/VO类
5. 添加单元测试

### 代码规范

- 遵循项目统一的编码规范
- 所有公共方法必须添加JavaDoc注释
- 使用Lombok简化样板代码
- 使用MyBatis Plus提供的CRUD方法
- 所有API接口必须添加Swagger注解

## 🧪 测试

### 单元测试

```java
// TODO: 添加单元测试示例
```

### 集成测试

```java
// TODO: 添加集成测试示例
```

## 📦 依赖版本

- Spring Boot: 3.5.7
- MyBatis Plus: 3.5.14
- JDK: 17
- MySQL: 8.0+

## 📚 相关文档

- [系统架构说明](../../doc/系统架构说明.md)
- [数据库设计文档](../../doc/数据库设计文档.md)
- [API 接口文档](../../doc/api.md)

## 🤝 贡献

欢迎提交Issue和Pull Request！

## 📄 License

Copyright © 2024 WeChat Check-in System

