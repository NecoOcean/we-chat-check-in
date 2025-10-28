# WeChat Check-in System Backend

微信打卡小程序后端系统，基于Spring Boot 3.5.7 + MyBatis Plus 3.5.14构建的多模块项目。

## 技术栈

- **JDK**: 17
- **Spring Boot**: 3.5.7
- **Spring Security**: 6.x
- **MyBatis Plus**: 3.5.14
- **MySQL**: 8.0+
- **Maven**: 3.8+

## 项目结构

```
we-chat-check-in/
├── we-chat-common/          # 公共模块（工具类、常量、异常定义）
├── we-chat-auth/            # 认证授权模块
├── we-chat-activity/        # 活动管理模块
├── we-chat-qrcode/          # 二维码管理模块
├── we-chat-checkins/        # 参与打卡模块
├── we-chat-evaluation/      # 活动评价模块
├── we-chat-dashboard/       # 数据看板模块
├── we-chat-admin/           # 用户管理模块
├── we-chat-audit/           # 审计日志模块
└── we-chat-web/             # Web启动模块（Controller层）
```

## 模块职责

- **we-chat-common**: 通用工具类、响应封装、异常处理、常量定义
- **we-chat-auth**: JWT令牌管理、登录鉴权、角色权限（市/县）校验
- **we-chat-activity**: 活动CRUD、状态管理、与二维码服务联动
- **we-chat-qrcode**: 打卡与评价二维码生成、状态管理、签名校验
- **we-chat-checkins**: 打卡提交、幂等处理、数据校验
- **we-chat-evaluation**: 评价提交、参与资格校验、统计汇总
- **we-chat-dashboard**: 平台总览、活动统计、数据聚合
- **we-chat-admin**: 县级账号管理（仅市级权限）
- **we-chat-audit**: 关键操作审计、日志记录
- **we-chat-web**: 统一Controller、全局异常处理、接口文档

## 开发环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- IDE: IntelliJ IDEA 2023+ (推荐)

## 快速开始

### 1. 环境准备

确保已安装JDK 17和Maven 3.8+：

```bash
java -version
mvn -version
```

### 2. 数据库准备

创建MySQL数据库：

```sql
CREATE DATABASE wechat_checkin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置文件

复制配置文件模板并修改数据库连接信息：

```bash
cp src/main/resources/application-dev.yml.template src/main/resources/application-dev.yml
```

### 4. 编译运行

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run -pl we-chat-web
```

## API文档

项目启动后，访问以下地址查看API文档：

- Swagger UI: http://localhost:8080/swagger-ui.html
- Knife4j UI: http://localhost:8080/doc.html

## 项目特性

- **多模块架构**: 清晰的模块划分，便于维护和扩展
- **权限隔离**: 市级/县级权限隔离，数据安全可靠
- **二维码安全**: 签名验证、防伪防篡改
- **幂等处理**: 打卡数据幂等，避免重复提交
- **审计日志**: 关键操作全程记录
- **API文档**: 完整的接口文档，便于前端对接

## 开发规范

- 遵循阿里巴巴Java开发手册
- 使用统一的代码格式化规则
- 必须编写单元测试，覆盖率不低于80%
- 所有接口必须有完整的API文档注释

## 版本历史

- v1.0.0: 初始版本，基础架构搭建