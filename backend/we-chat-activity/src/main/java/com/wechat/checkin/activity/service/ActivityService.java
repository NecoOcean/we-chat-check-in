package com.wechat.checkin.activity.service;

import com.wechat.checkin.activity.dto.ActivityQueryRequest;
import com.wechat.checkin.activity.dto.CreateActivityRequest;
import com.wechat.checkin.activity.vo.ActivityDetailVO;
import com.wechat.checkin.activity.vo.ActivityVO;
import com.wechat.checkin.common.response.PageResult;

/**
 * 活动服务接口
 */
public interface ActivityService {

    /**
     * 创建活动
     *
     * @param request 创建活动请求
     * @param adminId 管理员ID
     * @param adminRole 管理员角色
     * @param countyCode 管理员县域编码（县级管理员必填）
     * @return 活动ID
     */
    Long createActivity(CreateActivityRequest request, Long adminId, String adminRole, String countyCode);

    /**
     * 分页查询活动列表
     *
     * @param request 查询请求
     * @param adminRole 管理员角色
     * @param countyCode 管理员县域编码
     * @return 活动列表
     */
    PageResult<ActivityVO> listActivities(ActivityQueryRequest request, String adminRole, String countyCode);

    /**
     * 查询活动详情
     *
     * @param activityId 活动ID
     * @param adminRole 管理员角色
     * @param countyCode 管理员县域编码
     * @return 活动详情
     */
    ActivityDetailVO getActivityDetail(Long activityId, String adminRole, String countyCode);

    /**
     * 结束活动
     *
     * @param activityId 活动ID
     * @param adminId 管理员ID
     * @param adminRole 管理员角色
     * @param countyCode 管理员县域编码
     */
    void finishActivity(Long activityId, Long adminId, String adminRole, String countyCode);
}
