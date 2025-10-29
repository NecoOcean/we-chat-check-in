package com.wechat.checkin.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.checkin.common.entity.OperationLog;
import com.wechat.checkin.common.mapper.OperationLogMapper;
import com.wechat.checkin.common.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现类
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    @Async("taskExecutor")
    public void saveAsync(OperationLog operationLog) {
        try {
            this.save(operationLog);
            log.debug("操作日志保存成功: {}", operationLog.getDescription());
        } catch (Exception e) {
            log.error("操作日志保存失败: {}", operationLog.getDescription(), e);
        }
    }
}