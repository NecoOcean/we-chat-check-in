# 二维码管理模块 (we-chat-qrcode)

## 模块简介

二维码管理模块负责生成、管理和验证活动的打卡和评价二维码。使用JWT技术对二维码进行签名和验证，使用ZXing库生成二维码图片。

## 核心功能

### 1. 二维码生成
- 为活动生成打卡二维码（checkin）
- 为活动生成评价二维码（evaluation）
- 支持自定义过期时间（默认7天）
- 自动生成签名令牌（JWT）
- 生成Base64编码的二维码图片

### 2. 二维码管理
- 分页查询二维码列表
- 支持按活动、类型、状态过滤
- 查询二维码详情
- 禁用二维码功能

### 3. 二维码验证
- 验证二维码有效性（无需登录）
- 检查令牌签名
- 检查过期时间
- 检查二维码状态
- 返回验证结果和失败原因

## 技术栈

- **ZXing**: 二维码图片生成库（版本 3.5.2）
- **JWT**: 二维码令牌签名和验证（版本 0.12.6）
- **MyBatis Plus**: 数据库操作
- **Spring Boot**: Web框架和依赖注入

## 主要组件

### Entity（实体类）
- `QrCode`: 二维码实体，对应数据库表 `qrcodes`

### Mapper（数据访问层）
- `QrCodeMapper`: 二维码数据访问接口

### Service（服务层）
- `QrCodeService`: 二维码服务接口
- `QrCodeServiceImpl`: 二维码服务实现

### Controller（控制器）
- `QrCodeController`: 二维码管理API接口

### DTO（数据传输对象）
- `GenerateQrCodeRequest`: 生成二维码请求
- `QrCodeQueryRequest`: 查询二维码请求
- `VerifyQrCodeRequest`: 验证二维码请求

### VO（视图对象）
- `QrCodeVO`: 二维码响应对象
- `QrCodeVerifyResultVO`: 二维码验证结果

### Util（工具类）
- `QrCodeGenerator`: 二维码图片生成工具
- `QrCodeTokenProvider`: 二维码令牌提供者（JWT签名和验证）

## API接口

### 管理端接口（需要登录）

#### 1. 为活动生成二维码
```http
POST /api/activities/{id}/qrcodes
Content-Type: application/json

{
  "type": "checkin",
  "expireTime": "2024-12-31T23:59:59"
}
```

**权限**: 市级/县级管理员

#### 2. 查询二维码列表
```http
GET /api/qrcodes?activityId=1&type=checkin&status=enabled&current=1&size=10
```

**权限**: 市级/县级管理员

#### 3. 查询二维码详情
```http
GET /api/qrcodes/{id}
```

**权限**: 市级/县级管理员

#### 4. 禁用二维码
```http
PATCH /api/qrcodes/{id}/disable
```

**权限**: 市级/县级管理员

### 参与端接口（无需登录）

#### 5. 验证二维码
```http
GET /api/qrcodes/verify?token=eyJhbGciOiJIUzUxMiJ9...
```

**权限**: 无需登录

#### 6. 获取二维码信息
```http
GET /api/qrcodes/info?token=eyJhbGciOiJIUzUxMiJ9...
```

**权限**: 无需登录

## 配置说明

在 `application-dev.yml` 中配置二维码相关参数：

```yaml
qrcode:
  secret: wechat-checkin-qrcode-secret-key-2024-development-environment
  issuer: wechat-checkin-qrcode
  base-url: http://localhost:8080
  default-expiration-days: 7  # 默认过期时间（天）
```

### 配置项说明

- `secret`: 二维码JWT签名密钥（生产环境必须修改）
- `issuer`: 二维码令牌发行者
- `base-url`: 二维码访问基础URL
- `default-expiration-days`: 默认过期天数

## 数据库表

### qrcodes 表结构

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| activity_id | BIGINT | 关联活动ID |
| type | VARCHAR(20) | 二维码类型（checkin/evaluation） |
| token | TEXT | 二维码令牌（含签名） |
| expire_time | DATETIME | 过期时间 |
| disabled_time | DATETIME | 禁用时间 |
| status | VARCHAR(20) | 状态（enabled/disabled/deleted） |
| created_time | DATETIME | 创建时间 |
| updated_time | DATETIME | 更新时间 |

## 业务流程

### 二维码生成流程
1. 管理员发起生成二维码请求
2. 系统验证二维码类型和活动权限
3. 创建二维码记录
4. 生成JWT令牌（包含二维码ID、活动ID、类型、过期时间）
5. 生成二维码URL
6. 使用ZXing生成二维码图片（Base64格式）
7. 返回二维码信息

### 二维码验证流程
1. 参与者扫描二维码
2. 小程序获取二维码令牌
3. 调用验证接口
4. 系统验证JWT签名
5. 检查令牌是否过期
6. 查询数据库验证二维码状态
7. 返回验证结果

## 安全性设计

1. **JWT签名**: 使用HS512算法对二维码令牌进行签名，防止伪造
2. **过期控制**: 二维码设置过期时间，自动失效
3. **状态管理**: 支持手动禁用二维码
4. **高容错率**: 二维码生成使用高容错级别（Level H），提高扫码成功率

## 使用示例

### 生成二维码

```java
@Autowired
private QrCodeService qrCodeService;

public void generateQrCode() {
    GenerateQrCodeRequest request = new GenerateQrCodeRequest();
    request.setType("checkin");
    request.setExpireTime(LocalDateTime.now().plusDays(7));
    
    QrCodeVO qrCode = qrCodeService.generateQrCode(1L, request);
    
    System.out.println("二维码ID: " + qrCode.getId());
    System.out.println("二维码URL: " + qrCode.getUrl());
    System.out.println("二维码图片: " + qrCode.getQrCodeImage());
}
```

### 验证二维码

```java
@Autowired
private QrCodeService qrCodeService;

public void verifyQrCode(String token) {
    QrCodeVerifyResultVO result = qrCodeService.verifyQrCode(token);
    
    if (result.getValid()) {
        System.out.println("验证成功，活动ID: " + result.getActivityId());
    } else {
        System.out.println("验证失败: " + result.getReason());
    }
}
```

## 注意事项

1. **密钥安全**: 生产环境必须修改 `qrcode.secret` 配置
2. **过期时间**: 建议根据活动周期设置合理的过期时间
3. **二维码禁用**: 活动结束或异常情况下及时禁用二维码
4. **图片大小**: 默认生成 300x300 像素的二维码图片
5. **Base64长度**: 二维码图片较大，建议按需生成，避免频繁传输

## 依赖说明

```xml
<!-- ZXing二维码库 -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
</dependency>
```

## 后续优化

1. 支持二维码批量生成
2. 支持二维码使用次数限制
3. 添加二维码扫描统计功能
4. 支持二维码样式自定义（Logo、颜色等）
5. 优化二维码图片压缩算法

## 相关文档

- [系统架构说明](../../doc/系统架构说明.md)
- [API接口文档](../../doc/api.md)
- [数据库设计文档](../../doc/数据库设计文档.md)

