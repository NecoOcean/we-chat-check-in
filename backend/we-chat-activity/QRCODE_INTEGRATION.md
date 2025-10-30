# 活动管理模块 - 二维码自动生成集成说明

## 更新概述

本次更新为活动管理模块集成了二维码自动生成功能，实现了活动创建时自动生成打卡和评价二维码，活动结束时自动禁用二维码的完整流程。

## 主要变更

### 1. 依赖更新

#### `pom.xml`
添加了 `we-chat-qrcode` 模块依赖：

```xml
<dependency>
    <groupId>com.wechat.checkin</groupId>
    <artifactId>we-chat-qrcode</artifactId>
</dependency>
```

### 2. 服务层增强

#### `ActivityServiceImpl.java`

**新增依赖注入：**
```java
private final QrCodeService qrCodeService;
private final QrCodeMapper qrCodeMapper;
```

**新增功能方法：**

1. **`generateQrCodesForActivity(Activity activity)`**
   - 功能：为活动自动生成打卡和评价二维码
   - 时机：活动创建成功后自动调用
   - 特点：
     - 生成两种类型的二维码（打卡/评价）
     - 默认过期时间为活动结束后7天
     - 生成失败不影响活动创建（容错设计）
   
2. **`disableQrCodesForActivity(Long activityId)`**
   - 功能：禁用活动的所有二维码
   - 时机：活动结束时自动调用
   - 特点：
     - 批量禁用所有类型的二维码
     - 单个禁用失败不影响整体流程
     - 详细的日志记录

3. **`getQrCodesForActivity(Long activityId)`**
   - 功能：查询活动的二维码列表
   - 时机：查询活动详情时调用
   - 特点：
     - 按类型排序（打卡在前）
     - 转换为VO返回给前端

**修改的方法：**

1. **`createActivity(...)`**
   ```java
   // 活动创建成功后
   activityMapper.insert(activity);
   
   // 自动生成打卡二维码和评价二维码
   try {
       generateQrCodesForActivity(activity);
       log.info("活动二维码自动生成成功，活动ID: {}", activity.getId());
   } catch (Exception e) {
       log.error("活动二维码生成失败，活动ID: {}", activity.getId(), e);
       // 二维码生成失败不影响活动创建
   }
   ```

2. **`finishActivity(...)`**
   ```java
   // 更新活动状态
   activityMapper.update(null, updateWrapper);
   
   // 自动禁用该活动的所有二维码
   try {
       disableQrCodesForActivity(activityId);
       log.info("活动二维码已自动禁用，活动ID: {}", activityId);
   } catch (Exception e) {
       log.error("禁用活动二维码失败，活动ID: {}", activityId, e);
       // 二维码禁用失败不影响活动结束
   }
   ```

3. **`getActivityDetail(...)`**
   ```java
   // 查询活动的二维码列表
   List<QrCodeVO> qrCodes = getQrCodesForActivity(activityId);
   
   // 构建返回结果（包含二维码信息）
   return ActivityDetailVO.builder()
           .activity(convertToVO(activity))
           .participatedCount(participatedCount)
           .totalAttendees(totalAttendees)
           .evaluationCount(evaluationCount)
           .qrCodes(qrCodes)  // 新增
           .build();
   ```

### 3. VO层增强

#### `ActivityDetailVO.java`

**新增字段：**
```java
@Schema(description = "二维码列表（包含打卡和评价二维码）")
private List<QrCodeVO> qrCodes;
```

## 业务流程

### 创建活动流程

```
1. 管理员提交创建活动请求
   ↓
2. 验证参数
   ↓
3. 插入活动记录到数据库
   ↓
4. 自动生成打卡二维码（过期时间：活动结束+7天）
   ↓
5. 自动生成评价二维码（过期时间：活动结束+7天）
   ↓
6. 返回活动ID
```

**容错机制：**
- 二维码生成失败不会回滚活动创建
- 失败时记录错误日志
- 管理员可后续手动生成

### 结束活动流程

```
1. 管理员提交结束活动请求
   ↓
2. 验证权限和状态
   ↓
3. 更新活动状态为ENDED
   ↓
4. 查询该活动的所有二维码
   ↓
5. 批量禁用所有二维码
   ↓
6. 返回成功
```

**容错机制：**
- 单个二维码禁用失败不影响其他二维码
- 所有禁用失败不会回滚活动结束
- 详细记录每个二维码的禁用情况

### 查询活动详情流程

```
1. 管理员查询活动详情
   ↓
2. 验证权限
   ↓
3. 查询活动基本信息
   ↓
4. 查询统计数据
   ↓
5. 查询二维码列表（新增）
   ↓
6. 返回完整的活动详情
```

## API响应示例

### 查询活动详情响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "activity": {
      "id": 1,
      "name": "2024年春季教学活动",
      "description": "...",
      "status": "ongoing",
      "startTime": "2024-03-01T09:00:00",
      "endTime": "2024-03-31T18:00:00"
    },
    "participatedCount": 15,
    "totalAttendees": 120,
    "evaluationCount": 85,
    "qrCodes": [
      {
        "id": 1,
        "activityId": 1,
        "type": "checkin",
        "token": "eyJhbGciOiJIUzUxMiJ9...",
        "url": "http://localhost:8080/api/qrcodes/verify?token=...",
        "expireTime": "2024-04-07T18:00:00",
        "status": "enabled",
        "createdTime": "2024-03-01T08:00:00"
      },
      {
        "id": 2,
        "activityId": 1,
        "type": "evaluation",
        "token": "eyJhbGciOiJIUzUxMiJ9...",
        "url": "http://localhost:8080/api/qrcodes/verify?token=...",
        "expireTime": "2024-04-07T18:00:00",
        "status": "enabled",
        "createdTime": "2024-03-01T08:00:00"
      }
    ]
  }
}
```

## 技术特点

### 1. 事务管理
- `createActivity` 使用 `@Transactional` 保证事务性
- 二维码生成失败不影响主流程（使用try-catch隔离）
- `finishActivity` 使用 `@Transactional` 保证数据一致性

### 2. 容错设计
- 二维码生成/禁用失败不中断主流程
- 详细的异常日志记录
- 支持后续手动操作补救

### 3. 自动化
- 无需手动干预，活动创建即生成二维码
- 活动结束自动禁用二维码
- 提高工作效率，减少人为错误

### 4. 数据完整性
- 活动详情包含完整的二维码信息
- 便于前端直接展示和使用
- 支持二维码的生命周期管理

## 配置说明

### 二维码过期时间
当前默认为活动结束后7天，在代码中配置：

```java
LocalDateTime expireTime = activity.getEndTime().plusDays(7);
```

如需调整，可修改此值或提取为配置项。

### 日志级别
- INFO: 主要流程日志（活动创建、二维码生成等）
- DEBUG: 详细操作日志（单个二维码禁用）
- WARN: 非关键错误（单个二维码禁用失败）
- ERROR: 关键错误（整体二维码生成失败）

## 测试建议

### 单元测试

1. **测试活动创建并自动生成二维码**
   ```java
   @Test
   public void testCreateActivityWithQrCodes() {
       // 创建活动
       Long activityId = activityService.createActivity(...);
       
       // 验证二维码已生成
       List<QrCode> qrCodes = qrCodeMapper.selectList(
           new LambdaQueryWrapper<QrCode>()
               .eq(QrCode::getActivityId, activityId)
       );
       
       assertEquals(2, qrCodes.size());
       assertTrue(qrCodes.stream().anyMatch(qr -> qr.getType() == QrCodeTypeEnum.CHECKIN));
       assertTrue(qrCodes.stream().anyMatch(qr -> qr.getType() == QrCodeTypeEnum.EVALUATION));
   }
   ```

2. **测试活动结束并自动禁用二维码**
   ```java
   @Test
   public void testFinishActivityAndDisableQrCodes() {
       // 结束活动
       activityService.finishActivity(activityId, ...);
       
       // 验证二维码已禁用
       List<QrCode> qrCodes = qrCodeMapper.selectList(
           new LambdaQueryWrapper<QrCode>()
               .eq(QrCode::getActivityId, activityId)
       );
       
       assertTrue(qrCodes.stream().allMatch(
           qr -> qr.getStatus() == QrCodeStatusEnum.DISABLED
       ));
   }
   ```

### 集成测试

1. 测试完整的活动生命周期
2. 测试二维码生成失败的容错
3. 测试活动详情查询返回二维码信息

## 影响范围

### 受影响的模块
- ✅ `we-chat-activity` - 活动管理模块（主要修改）
- ✅ `we-chat-qrcode` - 二维码管理模块（依赖）

### 不受影响的模块
- `we-chat-auth` - 认证授权模块
- `we-chat-common` - 公共模块
- `we-chat-web` - Web模块

### 数据库影响
- 无需新增表或字段
- 利用现有的 `qrcodes` 表
- 数据关联通过 `activity_id` 外键

## 后续优化建议

1. **配置化**
   - 将二维码过期时间提取为配置项
   - 支持不同活动类型使用不同的过期策略

2. **异步处理**
   - 将二维码生成改为异步处理
   - 使用消息队列解耦

3. **批量操作优化**
   - 优化批量禁用二维码的性能
   - 使用批量更新SQL

4. **通知机制**
   - 二维码生成失败时通知管理员
   - 二维码即将过期时发送提醒

5. **监控告警**
   - 添加二维码生成成功率监控
   - 二维码使用情况统计

## 版本信息

- 更新时间: 2024-10-30
- 更新版本: v1.1.0
- 更新内容: 集成二维码自动生成功能

## 相关文档

- [二维码管理模块说明](../we-chat-qrcode/README.md)
- [系统架构说明](../../doc/系统架构说明.md)
- [API接口文档](../../doc/api.md)

