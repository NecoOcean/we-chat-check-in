package com.wechat.checkin.checkins.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wechat.checkin.checkins.dto.CheckinQueryRequest;
import com.wechat.checkin.checkins.dto.CheckinSubmitRequest;
import com.wechat.checkin.checkins.entity.Checkin;
import com.wechat.checkin.checkins.vo.CheckinStatisticsVO;
import com.wechat.checkin.checkins.vo.CheckinSubmitResponseVO;
import com.wechat.checkin.checkins.vo.CheckinVO;

/**
 * 打卡服务接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public interface CheckinService extends IService<Checkin> {

    /**
     * 提交打卡
     * 
     * @param request 打卡提交请求
     * @return 打卡提交响应
     */
    CheckinSubmitResponseVO submitCheckin(CheckinSubmitRequest request);

    /**
     * 分页查询打卡记录
     * 
     * @param request 查询请求
     * @return 打卡记录分页结果
     */
    Page<CheckinVO> queryCheckins(CheckinQueryRequest request);

    /**
     * 查询活动的打卡统计信息
     * 
     * @param activityId 活动ID
     * @return 参与教学点数和累计参与人数
     */
    CheckinStatisticsVO getCheckinStatistics(Long activityId);
}
