package com.wechat.checkin.evaluation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wechat.checkin.evaluation.dto.EvaluationQueryRequest;
import com.wechat.checkin.evaluation.dto.EvaluationSubmitRequest;
import com.wechat.checkin.evaluation.entity.Evaluation;
import com.wechat.checkin.evaluation.vo.EvaluationStatisticsVO;
import com.wechat.checkin.evaluation.vo.EvaluationSubmitResponseVO;
import com.wechat.checkin.evaluation.vo.EvaluationVO;

/**
 * 评价服务接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public interface EvaluationService extends IService<Evaluation> {

    /**
     * 提交评价
     * 
     * @param request 评价提交请求
     * @return 评价提交响应
     */
    EvaluationSubmitResponseVO submitEvaluation(EvaluationSubmitRequest request);

    /**
     * 查询评价列表（分页）
     * 
     * @param request 查询请求
     * @param countyCode 县域编码（用于权限控制）
     * @return 评价列表
     */
    Page<EvaluationVO> queryEvaluationList(EvaluationQueryRequest request, String countyCode);

    /**
     * 查询评价统计
     * 
     * @param activityId 活动ID
     * @return 评价统计信息
     */
    EvaluationStatisticsVO queryEvaluationStatistics(Long activityId);
}
