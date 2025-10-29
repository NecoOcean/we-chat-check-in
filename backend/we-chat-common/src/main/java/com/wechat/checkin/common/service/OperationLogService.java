package com.wechat.checkin.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wechat.checkin.common.entity.OperationLog;

/**
 * 操作日志服务接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 异步保存操作日志
     * 
     * @param operationLog 操作日志
     */
    void saveAsync(OperationLog operationLog);
}