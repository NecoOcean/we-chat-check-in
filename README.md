# WeChat 打卡小程序（版本 5 对齐）

本项目实现“管理精准化 + 参与轻量化”的活动打卡与评价平台：
- 管理端（市/县级管理员）：账号登录后发起活动、结束活动、生成与管理二维码、查看看板与评价汇总/明细、管理县级账号（仅市级）。
- 参与端（教学点管理员）：扫码打卡或评价，无需登录，极简操作。

## 功能模块

- **活动管理**: 发起、详情、结束
- **用户管理**: 市级创建与管理县级账号
- **教学点管理**: 创建、查询、更新、删除教学点（✨v1.2.0新增）
- **二维码管理**: 打卡/评价二维码生成、有效期与状态管理（禁用）
- **打卡提交**: 扫码进入选择教学点与人数，实时入库
- **活动评价**: 扫码进入选择题与建议，汇总与明细可查
- **数据看板**: 总览卡片与图表，10分钟自动刷新（待实现）
- **审计日志**: 关键操作留痕（待实现）

## 技术栈

### 核心框架
- **Java**: JDK 17
- **Spring Boot**: 3.5.7
- **Spring Security**: 集成于Spring Boot 3.5.7
- **构建工具**: Maven 多模块（Maven Wrapper）

### 数据持久化
- **数据库**: MySQL 8.0.33
- **ORM框架**: 
  - MyBatis 3.0.5 (mybatis-spring-boot-starter)
  - MyBatis Plus 3.5.14 (mybatis-plus-spring-boot3-starter)

### 安全认证
- **JWT**: JJWT 0.12.6 (jjwt-api, jjwt-impl, jjwt-jackson)
- **密码加密**: BCrypt (Spring Security Crypto)

### 工具库
- **Hutool**: 5.8.22 (全工具集)
- **MapStruct**: 1.5.5.Final (对象映射)
- **Lombok**: 1.18.30 (代码简化)
- **Jackson**: 随Spring Boot提供 (JSON处理)

### API文档
- **SpringDoc OpenAPI**: 2.8.8
- **Knife4j**: 4.4.0 (支持Jakarta EE)

### 其他组件
- **Spring Boot Validation**: 参数校验
- **Spring Boot Actuator**: 健康检查与监控
- **Spring Boot AOP**: 切面编程支持

## 仓库结构

```
backend/
├── we-chat-common/          # 公共模块（工具类、异常、响应封装、常量、枚举）
├── we-chat-auth/            # 认证授权模块（JWT、登录、权限校验）
├── we-chat-activity/        # 活动管理模块（活动CRUD、统计）
├── we-chat-qrcode/          # 二维码管理模块（生成、验证、状态管理）
├── we-chat-checkins/        # 打卡参与模块（打卡提交、查询、统计）
├── we-chat-evaluation/      # 活动评价模块（评价提交、查询、统计）
├── we-chat-admin/           # 用户管理模块（管理员账号管理）
├── we-chat-teaching-point/  # 教学点管理模块（教学点CRUD）✨v1.2.0新增
├── we-chat-web/             # Web启动模块（主启动类、配置、API接口）
└── pom.xml                  # Maven父项目配置
sql/                         # 数据库脚本（建表、初始化数据）
doc/                         # 项目文档（设计方案、API、数据库、架构、任务清单）
README.md                    # 项目说明
```

## 快速开始

### 1. 环境要求
- JDK 17+
- Maven 3.6+（或使用项目自带的Maven Wrapper）
- MySQL 8.0+

### 2. 数据库初始化
```bash
# 执行SQL脚本
mysql -u root -p < sql/create_tables.sql
mysql -u root -p < sql/init_admin_data.sql
```

### 3. 配置环境变量
在 `backend/we-chat-web/src/main/resources/application-dev.yml` 中配置：
- `DB_URL`: 数据库连接字符串
- `JWT_SECRET`: JWT令牌签名密钥
- `REDIS_URL`: Redis缓存服务地址（可选）
- `BASE_URL`: 后端服务基础URL（用于生成二维码深链）

### 4. 构建与启动
```bash
# 进入后端目录
cd backend

# 使用Maven Wrapper构建（推荐）
./mvnw clean install

# 或使用本地Maven
mvn clean install

# 启动应用
cd we-chat-web
../mvnw spring-boot:run

# 或使用本地Maven
mvn spring-boot:run
```

### 5. 访问接口文档
启动成功后访问：
- Swagger UI: http://localhost:8080/swagger-ui.html
- Knife4j文档: http://localhost:8080/doc.html

### 6. 项目文档
参阅 `doc/` 目录下的详细文档：
- `打卡小程序（版本 5）设计方案.md` - 整体设计方案
- `系统架构说明.md` - 系统架构设计
- `数据库设计文档.md` - 数据库设计详情
- `api.md` - API接口文档
- `各模块实现的具体功能.md` - 各模块功能说明
- `项目v1.2.0版本更新说明.md` - 最新版本更新说明 ✨新增
- `教学点管理模块开发完成报告.md` - 教学点模块开发报告 ✨新增
- `后端任务清单.md` - 后端开发任务
- `SpringBoot多模块项目基础架构搭建详细步骤.md` - 架构搭建指南

### 7. 最新更新（v1.2.0）
✨ **新增教学点管理模块**
- 7个REST API接口
- 完整的CRUD功能
- 市级/县级权限隔离
- 软删除策略
- 详细文档：查看 `backend/we-chat-teaching-point/README.md`

## 部署与运维（摘要）

- 建议容器化部署（Docker）与反向代理（Nginx）。
- 按环境管理配置（开发/测试/生产）。
- 监控接口成功率、时延与错误码分布；日志归档与审计保留。

## 许可与贡献

- 欢迎提交 Issue 与 PR 改进文档与实现。