## SpringBoot多模块项目基础架构搭建详细步骤
### 第一阶段：核心基础模块（高优先级）
步骤1：创建Maven父项目和基础配置

- 创建根目录pom.xml文件，配置多模块管理
- 配置Spring Boot版本、依赖管理、插件配置
- 创建.gitignore、README.md等基础文件
步骤2：创建we-chat-common公共模块

- 工具类、常量定义、异常处理类
- 统一响应封装、分页封装
- 基础实体类、枚举定义
步骤3：创建we-chat-auth认证授权模块

- JWT令牌管理、登录鉴权逻辑
- 角色权限校验（市级/县级）
- Spring Security配置
步骤4：创建we-chat-web启动模块

- 主启动类、Controller层
- 全局异常处理、跨域配置
- Swagger接口文档配置
步骤5：配置数据库连接和多环境配置

- MySQL数据库连接配置
- HikariCP连接池配置
- JPA配置、多环境配置文件
### 第二阶段：业务核心模块（中优先级）
步骤6：创建we-chat-activity活动管理模块

- 活动CRUD操作、状态管理
- 与二维码服务联动
步骤7：创建we-chat-qrcode二维码管理模块

- 二维码生成（ZXing）、Base64存储
- 状态管理、签名校验
步骤8：创建we-chat-checkins参与打卡模块

- 打卡提交、幂等处理
- 数据校验、业务逻辑
步骤9：创建we-chat-evaluation活动评价模块

- 评价提交、参与资格校验
- 统计汇总功能
### 第三阶段：辅助功能模块（低优先级）
步骤10：创建we-chat-dashboard数据看板模块

- 平台总览、活动统计
- 数据聚合查询
步骤11：创建we-chat-admin用户管理模块

- 县级账号管理（仅市级权限）
步骤12：创建we-chat-audit审计日志模块

- 关键操作审计、日志记录
## 技术栈配置要点

### 核心框架
- **Java 版本**：JDK 17
- **Spring Boot**：3.5.7
- **Spring Security**：随Spring Boot 3.5.7提供
- **构建工具**：Maven 多模块（Maven Wrapper）

### 数据持久化
- **数据库**：MySQL 8.0.33
- **ORM 框架**：
  - MyBatis：3.0.5（mybatis-spring-boot-starter）
  - MyBatis Plus：3.5.14（mybatis-plus-spring-boot3-starter）
  - MyBatis Plus JSqlParser：3.5.14

### 安全认证
- **Spring Security**：集成于Spring Boot 3.5.7
- **JWT**：JJWT 0.12.6
  - jjwt-api
  - jjwt-impl
  - jjwt-jackson

### 工具库
- **Hutool**：5.8.22（全工具集）
- **MapStruct**：1.5.5.Final（对象映射）
- **Lombok**：1.18.30（代码简化）
- **Jackson**：随Spring Boot提供（JSON处理）

### API文档
- **SpringDoc OpenAPI**：2.8.8（springdoc-openapi-starter-webmvc-ui）
- **Knife4j**：4.4.0（knife4j-openapi3-jakarta-spring-boot-starter）

### 测试框架
- **Spring Boot Test**：随Spring Boot提供
- **Spring Security Test**：随Spring Security提供

### 其他组件
- **Spring Boot Validation**：参数校验
- **Spring Boot Actuator**：健康检查与监控
- **Spring Boot AOP**：切面编程支持

### Maven插件
- **maven-compiler-plugin**：3.11.0
- **maven-surefire-plugin**：3.0.0
- **spring-boot-maven-plugin**：3.5.7

### 注意事项
- 项目使用 **Jakarta EE** 规范（Spring Boot 3.x要求）
- 二维码功能（ZXing）暂未启用，在父pom中已注释
- 编码统一使用 **UTF-8**
## 执行原则
1. 严格按优先级执行 ：先完成高优先级模块，确保核心功能可用
2. 模块间依赖管理 ：common模块为基础，其他模块依赖common
3. 逐步验证 ：每个步骤完成后进行编译测试，确保无误再继续
4. 配置统一管理 ：在父pom中统一管理版本和依赖