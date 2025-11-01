package com.wechat.checkin.teachingpoint.service;

import com.wechat.checkin.common.response.PageResult;
import com.wechat.checkin.teachingpoint.dto.CreateTeachingPointRequest;
import com.wechat.checkin.teachingpoint.dto.TeachingPointQueryRequest;
import com.wechat.checkin.teachingpoint.dto.UpdateTeachingPointRequest;
import com.wechat.checkin.teachingpoint.vo.TeachingPointVO;

/**
 * 教学点服务接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public interface TeachingPointService {

    /**
     * 创建教学点
     * 
     * @param request 创建请求
     * @return 新创建的教学点ID
     */
    Long createTeachingPoint(CreateTeachingPointRequest request);

    /**
     * 更新教学点信息
     * 
     * @param id 教学点ID
     * @param request 更新请求
     */
    void updateTeachingPoint(Long id, UpdateTeachingPointRequest request);

    /**
     * 删除教学点（软删除）
     * 
     * @param id 教学点ID
     */
    void deleteTeachingPoint(Long id);

    /**
     * 启用教学点
     * 
     * @param id 教学点ID
     */
    void enableTeachingPoint(Long id);

    /**
     * 禁用教学点
     * 
     * @param id 教学点ID
     */
    void disableTeachingPoint(Long id);

    /**
     * 查询教学点详情
     * 
     * @param id 教学点ID
     * @return 教学点信息
     */
    TeachingPointVO getTeachingPointDetail(Long id);

    /**
     * 分页查询教学点列表
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<TeachingPointVO> listTeachingPoints(TeachingPointQueryRequest request);
}

