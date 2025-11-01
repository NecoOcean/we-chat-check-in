# 县域管理模块

**模块名称**: we-chat-county  
**版本**: 1.3.0  
**功能概述**: 县域基础数据的CRUD操作和状态管理

---

## 📋 模块简介

县域管理模块负责管理系统中的县域基础数据，包括县域的创建、查询、更新、删除和状态管理等功能。县域是系统中的基础数据，教学点、管理员等都需要关联到具体的县域。

**注意**: 本模块的所有操作仅限**市级管理员**，县级管理员无权管理县域数据。

---

## 🎯 核心功能

### 1. 县域CRUD操作

| 功能 | 描述 | 权限要求 |
|------|------|---------|
| 创建县域 | 新增县域基础数据 | 市级管理员 |
| 查询县域详情 | 根据编码查询县域信息 | 所有用户 |
| 查询县域列表 | 分页查询、支持多条件过滤 | 所有用户 |
| 更新县域 | 修改县域名称等信息 | 市级管理员 |
| 删除县域 | 软删除县域（数据保留） | 市级管理员 |

### 2. 县域状态管理

| 功能 | 描述 | 权限要求 |
|------|------|---------|
| 启用县域 | 将县域状态设置为启用 | 市级管理员 |
| 禁用县域 | 将县域状态设置为禁用 | 市级管理员 |

---

## 🔧 技术实现

### 实体类设计

```java
@Data
@TableName("counties")
public class County implements Serializable {
    @TableId(value = "code", type = IdType.INPUT)
    private String code;              // 县域编码（主键）
    
    private String name;              // 县域名称
    private StatusEnum status;        // 状态：启用/禁用/删除
    private LocalDateTime createdTime; // 创建时间
    private LocalDateTime updatedTime; // 更新时间
}
```

### 数据库表结构

```sql
CREATE TABLE `counties` (
  `code` VARCHAR(16) NOT NULL COMMENT '县域编码',
  `name` VARCHAR(64) NOT NULL COMMENT '县域名称',
  `status` ENUM('enabled', 'disabled', 'deleted') NOT NULL DEFAULT 'enabled',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`code`),
  INDEX `idx_counties_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='县域表';
```

---

## 📡 API 接口说明

### 1. 创建县域

**接口**: `POST /api/counties`  
**权限**: 市级管理员  
**请求示例**:

```json
{
  "code": "PX",
  "name": "萍乡县"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "code": "PX",
    "name": "萍乡县",
    "status": "enabled",
    "createdTime": "2024-11-01T12:00:00",
    "updatedTime": "2024-11-01T12:00:00"
  }
}
```

### 2. 更新县域

**接口**: `PUT /api/counties/{code}`  
**权限**: 市级管理员  
**请求示例**:

```json
{
  "name": "萍乡市安源区"
}
```

### 3. 删除县域

**接口**: `DELETE /api/counties/{code}`  
**权限**: 市级管理员  
**说明**: 软删除，数据保留但状态标记为 `deleted`

### 4. 启用县域

**接口**: `PUT /api/counties/{code}/enable`  
**权限**: 市级管理员  

### 5. 禁用县域

**接口**: `PUT /api/counties/{code}/disable`  
**权限**: 市级管理员  

### 6. 查询县域详情

**接口**: `GET /api/counties/{code}`  
**权限**: 无需认证  
**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "code": "PX",
    "name": "萍乡县",
    "status": "enabled",
    "createdTime": "2024-11-01T12:00:00",
    "updatedTime": "2024-11-01T12:00:00"
  }
}
```

### 7. 查询县域列表

**接口**: `GET /api/counties`  
**权限**: 无需认证  
**查询参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | String | 否 | 县域编码（精确查询） |
| name | String | 否 | 县域名称（模糊查询） |
| status | Enum | 否 | 状态过滤（enabled/disabled/deleted） |
| current | Long | 否 | 当前页码（默认1） |
| size | Long | 否 | 每页数量（默认10） |

**请求示例**:

```
GET /api/counties?name=萍乡&status=enabled&current=1&size=10
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "code": "PX",
        "name": "萍乡县",
        "status": "enabled",
        "createdTime": "2024-11-01T12:00:00",
        "updatedTime": "2024-11-01T12:00:00"
      }
    ],
    "current": 1,
    "size": 10,
    "total": 1
  }
}
```

---

## 🔐 权限说明

### 权限设计原则

1. **管理权限**: 仅市级管理员可以进行县域的创建、更新、删除和状态管理
2. **查询权限**: 所有用户（包括未登录用户）都可以查询县域信息

### 权限控制实现

使用 `@RequireRole("city")` 注解限制仅市级管理员可以操作：

```java
@PostMapping
@RequireRole("city")
public Result<CountyVO> createCounty(@Valid @RequestBody CountyCreateRequest request) {
    // 创建县域逻辑
}
```

---

## ✨ 业务规则

### 1. 县域编码规则

- 格式要求：仅支持大写字母和数字
- 长度限制：最多16个字符
- 唯一性：系统内唯一，不可重复
- 示例：`PX`、`LC`、`SHANGLI`

### 2. 县域名称规则

- 长度限制：最多64个字符
- 唯一性：系统内唯一，不可重复
- 示例：`萍乡县`、`莲花县`、`上栗县`

### 3. 状态管理规则

- **enabled**: 启用状态，正常使用
- **disabled**: 禁用状态，暂时停用
- **deleted**: 已删除状态，软删除（数据保留）

### 4. 软删除策略

- 删除操作不会真正删除数据库记录
- 仅将状态设置为 `deleted`
- 已删除的县域不可再进行启用、禁用等操作
- 默认查询不显示已删除的县域

---

## 🔗 模块依赖

### 内部依赖

- `we-chat-common`: 公共模块（工具类、异常、响应封装等）
- `we-chat-auth`: 认证授权模块（权限校验）

### 外部依赖

- Spring Boot Web
- Spring Boot Validation
- MyBatis Plus
- Hutool
- Lombok

---

## 📦 项目结构

```
we-chat-county/
├── pom.xml                           # Maven项目配置
├── README.md                         # 模块文档
└── src/
    └── main/
        └── java/
            └── com/wechat/checkin/county/
                ├── CountyModuleConfig.java         # 模块配置类
                ├── entity/
                │   └── County.java                 # 县域实体
                ├── mapper/
                │   └── CountyMapper.java           # 数据访问层
                ├── service/
                │   ├── CountyService.java          # 服务接口
                │   └── impl/
                │       └── CountyServiceImpl.java  # 服务实现
                ├── controller/
                │   └── CountyController.java       # API控制器
                ├── dto/
                │   ├── CountyCreateRequest.java    # 创建请求
                │   ├── CountyUpdateRequest.java    # 更新请求
                │   └── CountyQueryRequest.java     # 查询请求
                └── vo/
                    └── CountyVO.java               # 视图对象
```

---

## 🚀 快速开始

### 1. 编译模块

```bash
cd backend/we-chat-county
mvn clean install
```

### 2. 使用API

#### 方式一：Swagger/Knife4j文档

访问 http://localhost:8080/doc.html，在"县域管理"标签下测试接口。

#### 方式二：curl命令

```bash
# 1. 市级管理员登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"city_admin","password":"password"}'

# 2. 创建县域
curl -X POST http://localhost:8080/api/counties \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"code":"PX","name":"萍乡县"}'

# 3. 查询县域列表（无需登录）
curl -X GET "http://localhost:8080/api/counties?current=1&size=10"

# 4. 查询县域详情（无需登录）
curl -X GET http://localhost:8080/api/counties/PX

# 5. 更新县域
curl -X PUT http://localhost:8080/api/counties/PX \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"萍乡市安源区"}'

# 6. 禁用县域
curl -X PUT http://localhost:8080/api/counties/PX/disable \
  -H "Authorization: Bearer <token>"

# 7. 启用县域
curl -X PUT http://localhost:8080/api/counties/PX/enable \
  -H "Authorization: Bearer <token>"

# 8. 删除县域
curl -X DELETE http://localhost:8080/api/counties/PX \
  -H "Authorization: Bearer <token>"
```

---

## 📝 错误码说明

| 错误码 | 错误信息 | 说明 |
|--------|---------|------|
| 2101 | 县域不存在 | 查询的县域编码不存在 |
| 2102 | 县域编码已存在 | 创建时县域编码已被使用 |
| 2103 | 县域名称已存在 | 创建/更新时县域名称已存在 |
| 2104 | 县域已被删除 | 操作已删除的县域 |
| 1204 | 权限不足 | 非市级管理员尝试管理县域 |

---

## 🧪 测试建议

### 单元测试

```java
@SpringBootTest
class CountyServiceImplTest {
    
    @Test
    void testCreateCounty_Success() {
        // 测试成功创建县域
    }
    
    @Test
    void testCreateCounty_CodeExists() {
        // 测试县域编码已存在的情况
    }
    
    @Test
    void testUpdateCounty_Success() {
        // 测试成功更新县域
    }
    
    @Test
    void testDeleteCounty_Success() {
        // 测试软删除县域
    }
    
    @Test
    void testEnableCounty_Success() {
        // 测试启用县域
    }
    
    @Test
    void testDisableCounty_Success() {
        // 测试禁用县域
    }
}
```

### 集成测试场景

1. ✅ 市级管理员创建县域成功
2. ✅ 县级管理员尝试创建县域失败（权限不足）
3. ✅ 创建重复编码的县域失败
4. ✅ 创建重复名称的县域失败
5. ✅ 更新县域名称成功
6. ✅ 软删除县域成功
7. ✅ 启用/禁用县域成功
8. ✅ 查询县域列表（带各种过滤条件）

---

## 📚 相关文档

- [系统架构说明](../../doc/系统架构说明.md)
- [API接口文档](../../doc/api.md)
- [数据库设计文档](../../doc/数据库设计文档.md)
- [县域管理模块开发报告](../../doc/县域管理模块开发报告.md)

---

## 🤝 贡献指南

如需为本模块贡献代码，请遵循以下规范：

1. 代码风格遵循阿里巴巴Java开发手册
2. 所有公共方法必须有完整的JavaDoc注释
3. 新增功能需要编写单元测试
4. 提交前运行 `mvn clean test` 确保测试通过

---

## 📞 技术支持

如遇到问题，请查看：

- API文档：http://localhost:8080/doc.html
- 日志文件：logs/wechat-checkin.log
- 项目文档：doc/ 目录

---

**模块状态**: ✅ 生产就绪  
**最后更新**: 2024-11-01  
**维护团队**: WeChat Check-in Development Team

