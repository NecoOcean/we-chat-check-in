# 活动评价模块（we-chat-evaluation）

**版本**：1.0.0 | **状态**：✅ 已完成 | **最后更新**：2024 年 11 月

## 模块概述

活动评价模块是微信打卡小程序的核心功能模块，用于管理活动结束后的参与方评价。支持评价提交、数据查询、统计汇总等完整的评价管理功能。

## 核心功能

### 1. 评价提交接口
- **端点**：`POST /api/evaluations/evaluation`
- **权限**：无需登录（通过二维码令牌验证）
- **功能**：教学点扫描评价二维码后提交评价
- **验证机制**：
  - JWT 令牌签名验证
  - 二维码类型检查（EVALUATION）
  - 活动状态检查（必须已结束）
  - 参与资格检查（必须已打卡）
  - 防重复评价（数据库唯一约束）

### 2. 评价列表查询
- **端点**：`GET /api/evaluations`
- **权限**：需要登录（市/县管理员）
- **功能**：分页查询评价记录
- **支持**：按活动/教学点过滤，按提交时间倒序排列
- **权限控制**：市级查全部，县级查本县

### 3. 评价统计汇总
- **端点**：`GET /api/evaluations/statistics?activityId=1`
- **权限**：需要登录（市/县管理员）
- **统计内容**：
  - 总评价数、满意度/实用性/质量平均分
  - 满意度等级分布（1分、2分、3分数量）
  - 包含建议的评价数量

## API 文档

### 请求/响应示例

#### 提交评价
```bash
POST /api/evaluations/evaluation
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "teachingPointId": 1,
  "q1Satisfaction": 3,
  "q2Practicality": 2,
  "q3Quality": 3,
  "suggestionText": "希望能增加更多互动环节"
}
```

**成功响应（200）**：
```json
{
  "code": 0,
  "message": "评价提交成功",
  "data": {
    "id": 1,
    "activityId": 1,
    "teachingPointId": 1,
    "submittedTime": "2024-01-01T10:30:00",
    "message": "评价提交成功"
  }
}
```

#### 查询评价列表
```bash
GET /api/evaluations?pageNum=1&pageSize=10&activityId=1
Authorization: Bearer <token>
```

#### 查询评价统计
```bash
GET /api/evaluations/statistics?activityId=1
Authorization: Bearer <token>
```

## 错误处理

| 错误码 | 错误信息 | 原因 |
|---|---|---|
| 1301 | 活动不存在 | 指定的活动不存在 |
| 1304 | 活动未结束，暂无法评价 | 活动状态不是 ENDED |
| 1501 | 二维码无效 | 二维码过期、禁用或签名错误 |
| 1601 | 教学点未参与过此活动，无法进行评价 | 没有打卡记录 |
| 1602 | 该教学点已评价 | 已有评价记录 |

## 数据库设计

### evaluations 表

```sql
CREATE TABLE `evaluations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `activity_id` BIGINT NOT NULL COMMENT '活动ID',
  `teaching_point_id` BIGINT NOT NULL COMMENT '教学点ID',
  `q1_satisfaction` TINYINT NOT NULL COMMENT '满意度（1-3）',
  `q2_practicality` TINYINT NOT NULL COMMENT '实用性（1-3）',
  `q3_quality` TINYINT NULL COMMENT '质量（1-3，可选）',
  `suggestion_text` VARCHAR(200) NULL COMMENT '建议（≤200字）',
  `submitted_time` DATETIME NOT NULL COMMENT '提交时间',
  `source_qrcode_id` BIGINT NOT NULL COMMENT '来源二维码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_evaluations_activity_teaching_point` (`activity_id`, `teaching_point_id`),
  INDEX `idx_evaluations_activity_submitted_time` (`activity_id`, `submitted_time`),
  INDEX `idx_evaluations_teaching_point` (`teaching_point_id`),
  CONSTRAINT `fk_evaluations_activity_id` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`),
  CONSTRAINT `fk_evaluations_teaching_point_id` FOREIGN KEY (`teaching_point_id`) REFERENCES `teaching_points` (`id`)
)
```

**关键设计**：
- 唯一约束防止重复评价
- 复合索引加速查询

## 模块架构

```
we-chat-evaluation/
├── entity/
│   └── Evaluation.java              # 实体类
├── dto/
│   ├── EvaluationSubmitRequest.java  # 请求 DTO
│   └── EvaluationQueryRequest.java   # 查询 DTO
├── vo/
│   ├── EvaluationSubmitResponseVO.java
│   ├── EvaluationVO.java
│   └── EvaluationStatisticsVO.java
├── mapper/
│   └── EvaluationMapper.java        # MyBatis Plus 接口
├── service/
│   ├── EvaluationService.java       # 业务接口
│   └── impl/
│       └── EvaluationServiceImpl.java # 实现类（核心逻辑）
└── controller/
    └── EvaluationController.java    # REST API
```

## 关键业务逻辑

### 评价提交流程
```
1. 验证二维码（JWT 签名 + 类型检查 + 过期检查）
   ↓
2. 验证活动（存在性 + 状态检查）
   ↓
3. 检查参与资格（查询打卡记录）
   ↓
4. 防重复检查（数据库唯一约束）
   ↓
5. 保存评价记录（@Transactional 事务保证）
   ↓
6. 返回成功响应
```

## 模块依赖

```
we-chat-evaluation
    ├── we-chat-common     # 公共工具、异常、常量
    ├── we-chat-auth       # JWT 验证、权限控制
    ├── we-chat-activity   # 活动信息查询
    ├── we-chat-qrcode     # 二维码验证
    └── we-chat-checkins   # 打卡记录查询
```

## 性能优化

- ✅ 复合索引 `(activity_id, submitted_time)` 加速查询
- ✅ SQL 聚合函数优化统计
- ✅ 幂等性设计（数据库约束）
- ✅ 事务处理保证一致性

## 安全机制

| 安全措施 | 实现方式 |
|---|---|
| 二维码防伪 | JWT 签名验证 |
| 防重复评价 | 数据库唯一约束 + 应用层检查 |
| 权限隔离 | 基于角色和县域 |
| 参与资格检查 | 验证打卡记录 |

## 快速开始

### 1. 构建项目
```bash
cd backend
mvn clean install
```

### 2. 启动应用
```bash
cd we-chat-web
mvn spring-boot:run
```

### 3. 测试接口
- Swagger 文档：http://localhost:8080/swagger-ui.html
- Knife4j 文档：http://localhost:8080/doc.html

## 参数校验

### 评价分数约束
- `q1_satisfaction`：1-3（必填）
- `q2_practicality`：1-3（必填）
- `q3_quality`：1-3（可选）

### 建议文本约束
- 最大长度：200 字符
- 格式：字符串（可选）

## 代码质量

- ✅ Javadoc 完整注释
- ✅ Google Java 代码风格
- ✅ 分层架构（Entity → DTO → VO）
- ✅ 统一异常处理和错误码

## 相关文档

- [测试用例](../doc/评价模块测试用例.md) - 完整的测试用例集合
- [系统架构](../doc/系统架构说明.md) - 系统整体架构
- [数据库设计](../doc/数据库设计文档.md) - 数据库详细设计
- [API 接口](../doc/api.md) - 全系统 API 文档
