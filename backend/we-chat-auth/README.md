# 认证授权模块（we-chat-auth）

## 📝 模块说明

认证授权模块提供完整的JWT认证体系和权限控制功能，支持市级/县级管理员的登录、权限验证、数据权限隔离等。

## ✨ 主要功能

### 1. 用户认证
- ✅ 用户名密码登录
- ✅ JWT访问令牌生成（有效期24小时）
- ✅ 刷新令牌生成（有效期7天）
- ✅ 令牌自动刷新
- ✅ 用户登出
- ✅ 修改密码

### 2. 权限控制
- ✅ 基于角色的权限控制（RBAC）
- ✅ 市级/县级管理员角色隔离
- ✅ 县域数据权限隔离
- ✅ 接口级权限注解

### 3. 安全特性
- ✅ BCrypt密码加密
- ✅ JWT签名验证
- ✅ 令牌过期检查
- ✅ 请求拦截器链
- ✅ 数据权限过滤

## 📦 核心组件

### 控制器（Controller）
- `AuthController`: 认证相关接口（登录、登出、刷新、修改密码等）

### 服务（Service）
- `AuthService`: 认证服务接口
- `AuthServiceImpl`: 认证服务实现

### 安全组件（Security）
- `JwtTokenProvider`: JWT令牌生成和验证
- `UserPrincipal`: 用户主体（实现Spring Security UserDetails）
- `SecurityContextHolder`: 安全上下文工具类

### 拦截器（Interceptor）
- `AuthInterceptor`: 认证拦截器（验证JWT令牌）
- `DataPermissionInterceptor`: 数据权限拦截器（县域数据隔离）

### 注解（Annotation）
- `@RequireRole`: 角色权限注解
- `@RequirePermission`: 接口权限注解
- `@RequireDataPermission`: 数据权限注解

### 实体和DTO
- `Admin`: 管理员实体
- `LoginRequest`: 登录请求DTO
- `LoginResponse`: 登录响应VO
- `ChangePasswordRequest`: 修改密码请求DTO

## 🔐 权限注解使用

### @RequireRole - 角色权限控制

```java
// 仅市级管理员可访问
@RequireRole("city")
@PostMapping("/admins")
public Result<Long> createAdmin(@RequestBody CreateAdminRequest request) {
    // ...
}

// 市级或县级管理员均可访问
@RequireRole({"city", "county"})
@GetMapping("/activities")
public Result<List<ActivityVO>> listActivities() {
    // ...
}

// 启用县域权限隔离
@RequireRole(value = "county", countyIsolation = true)
@GetMapping("/data")
public Result<Object> getData() {
    // 县级管理员只能访问本县数据
}
```

### @RequirePermission - 接口权限控制

```java
@RequirePermission("activity:manage")
@PostMapping("/activities")
public Result<Long> createActivity(@RequestBody CreateActivityRequest request) {
    // ...
}

// 启用县级数据过滤
@RequirePermission(value = "data:view", countyDataFilter = true)
@GetMapping("/statistics")
public Result<Object> getStatistics() {
    // ...
}
```

### @RequireDataPermission - 数据权限控制

```java
@RequireDataPermission
@GetMapping("/county-data")
public Result<Object> getCountyData() {
    // 自动进行县域数据权限验证
    // 县级管理员只能访问本县数据
}
```

## 🛠️ 工具类使用

### SecurityContextHolder - 获取当前登录用户

```java
// 获取当前登录用户
UserPrincipal currentUser = SecurityContextHolder.getCurrentUser();

// 获取当前用户ID
Long userId = SecurityContextHolder.getCurrentUserId();

// 获取当前用户名
String username = SecurityContextHolder.getCurrentUsername();

// 获取当前用户角色
String role = SecurityContextHolder.getCurrentUserRole();

// 获取当前用户县域代码
String countyCode = SecurityContextHolder.getCurrentUserCountyCode();

// 判断是否为市级管理员
boolean isCityAdmin = SecurityContextHolder.isCityAdmin();

// 判断是否为县级管理员
boolean isCountyAdmin = SecurityContextHolder.isCountyAdmin();

// 检查是否可以访问指定县域数据
boolean canAccess = SecurityContextHolder.canAccessCountyData("PX");

// 验证必须是市级管理员（否则抛出异常）
SecurityContextHolder.requireCityAdmin();

// 验证可以访问指定县域数据（否则抛出异常）
SecurityContextHolder.requireCountyAccess("PX");
```

## 📋 API接口

### 1. 登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}

Response:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "id": 1,
      "username": "admin",
      "role": "city",
      "countyCode": null
    }
  }
}
```

### 2. 刷新令牌
```
POST /api/auth/refresh?refreshToken=eyJhbGciOiJIUzUxMiJ9...

Response: 同登录响应
```

### 3. 登出
```
POST /api/auth/logout
Authorization: Bearer {accessToken}

Response:
{
  "code": 200,
  "message": "操作成功"
}
```

### 4. 获取当前用户信息
```
GET /api/auth/me
Authorization: Bearer {accessToken}

Response:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "role": "city",
    "countyCode": null
  }
}
```

### 5. 验证令牌
```
GET /api/auth/validate?token=eyJhbGciOiJIUzUxMiJ9...

Response:
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

### 6. 修改密码
```
POST /api/auth/change-password
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "oldPassword": "oldPassword123",
  "newPassword": "newPassword123",
  "confirmPassword": "newPassword123"
}

Response:
{
  "code": 200,
  "message": "密码修改成功，请重新登录"
}
```

## ⚙️ 配置说明

在 `application.yml` 中配置JWT参数：

```yaml
jwt:
  # JWT签名密钥（至少32位）
  secret: wechat-checkin-system-jwt-secret-key-2024
  # 访问令牌有效期（毫秒，默认24小时）
  access-token-expiration: 86400000
  # 刷新令牌有效期（毫秒，默认7天）
  refresh-token-expiration: 604800000
  # 令牌签发者
  issuer: wechat-checkin-system
```

## 🔒 安全建议

1. **JWT密钥管理**
   - 生产环境使用环境变量配置JWT密钥
   - 密钥长度至少32位
   - 定期轮换密钥

2. **密码策略**
   - 最小长度6位
   - 使用BCrypt加密存储
   - 建议添加密码复杂度要求

3. **令牌管理**
   - 访问令牌短期有效（24小时）
   - 刷新令牌长期有效（7天）
   - 可选：实现令牌黑名单机制（Redis）

4. **HTTPS**
   - 生产环境必须使用HTTPS
   - 防止令牌在传输过程中被窃取

5. **登录保护**
   - 建议添加登录失败次数限制
   - 可选：添加验证码机制
   - 记录登录审计日志

## 🔄 拦截器执行顺序

```
请求 → AuthInterceptor（认证）→ DataPermissionInterceptor（数据权限）→ Controller
```

### 排除路径

以下路径不需要认证：
- `/api/auth/login` - 登录
- `/api/auth/refresh` - 刷新令牌
- `/api/auth/validate` - 验证令牌
- `/api/checkins/checkin` - 打卡（通过二维码令牌验证）
- `/api/checkins/evaluate` - 评价（通过二维码令牌验证）
- `/api/qrcodes/verify` - 二维码验证
- `/actuator/**` - 健康检查
- `/doc.html`, `/swagger-ui/**` - API文档

## 📚 扩展建议

### 1. 令牌黑名单（可选）
使用Redis实现令牌黑名单，支持主动失效：

```java
@Service
public class TokenBlacklistService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public void addToBlacklist(String token, long expirationSeconds) {
        redisTemplate.opsForValue().set(
            "blacklist:" + token, 
            "1", 
            expirationSeconds, 
            TimeUnit.SECONDS
        );
    }
    
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(
            redisTemplate.hasKey("blacklist:" + token)
        );
    }
}
```

### 2. 登录日志（推荐）
记录用户登录历史：

```java
@Async
public void recordLoginLog(Long userId, String username, String ip, boolean success) {
    LoginLog log = new LoginLog();
    log.setUserId(userId);
    log.setUsername(username);
    log.setIp(ip);
    log.setSuccess(success);
    log.setLoginTime(LocalDateTime.now());
    loginLogMapper.insert(log);
}
```

### 3. 验证码（可选）
防止暴力破解：

```java
@PostMapping("/login")
public Result<LoginResponse> login(
    @RequestBody LoginRequest request,
    HttpServletRequest httpRequest) {
    
    // 验证验证码
    if (!captchaService.verify(request.getCaptchaKey(), request.getCaptcha())) {
        throw new BusinessException(ResultCode.PARAM_ERROR, "验证码错误");
    }
    
    // 执行登录
    return Result.success(authService.login(request, IpUtils.getClientIp(httpRequest)));
}
```

## 🐛 常见问题

### Q1: 令牌过期怎么办？
A: 使用刷新令牌接口 `/api/auth/refresh` 获取新的访问令牌

### Q2: 如何强制用户重新登录？
A: 实现令牌黑名单机制，将用户的令牌加入黑名单

### Q3: 县级管理员访问其他县数据返回403？
A: 这是正常的权限控制，县级管理员只能访问本县数据

### Q4: 如何获取当前登录用户信息？
A: 使用 `SecurityContextHolder.getCurrentUser()` 工具方法

### Q5: 参与端接口需要认证吗？
A: 不需要，参与端接口通过二维码令牌验证，已在拦截器中排除

## 📝 更新日志

### v1.0.0 (2024-10-30)
- ✅ 实现JWT登录认证
- ✅ 实现令牌刷新机制
- ✅ 实现角色权限控制
- ✅ 实现数据权限隔离
- ✅ 添加修改密码功能
- ✅ 添加安全上下文工具类
- ✅ 完善API文档注解

