package com.wechat.checkin.evaluation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.checkin.evaluation.entity.Evaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 评价Mapper接口
 * 用于数据库操作
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Mapper
public interface EvaluationMapper extends BaseMapper<Evaluation> {

    /**
     * 查询满意度平均分
     * 
     * @param activityId 活动ID
     * @return 平均分
     */
    @Select("SELECT AVG(CAST(q1_satisfaction AS DECIMAL(3,2))) FROM evaluations WHERE activity_id = #{activityId}")
    Double queryAvgSatisfaction(@Param("activityId") Long activityId);

    /**
     * 查询实用性平均分
     * 
     * @param activityId 活动ID
     * @return 平均分
     */
    @Select("SELECT AVG(CAST(q2_practicality AS DECIMAL(3,2))) FROM evaluations WHERE activity_id = #{activityId}")
    Double queryAvgPracticality(@Param("activityId") Long activityId);

    /**
     * 查询质量平均分
     * 
     * @param activityId 活动ID
     * @return 平均分
     */
    @Select("SELECT AVG(CAST(q3_quality AS DECIMAL(3,2))) FROM evaluations WHERE activity_id = #{activityId} AND q3_quality IS NOT NULL")
    Double queryAvgQuality(@Param("activityId") Long activityId);

    /**
     * 查询满意度评分为3的数量
     * 
     * @param activityId 活动ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM evaluations WHERE activity_id = #{activityId} AND q1_satisfaction = 3")
    Long querySatisfactionLevel3Count(@Param("activityId") Long activityId);

    /**
     * 查询满意度评分为2的数量
     * 
     * @param activityId 活动ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM evaluations WHERE activity_id = #{activityId} AND q1_satisfaction = 2")
    Long querySatisfactionLevel2Count(@Param("activityId") Long activityId);

    /**
     * 查询满意度评分为1的数量
     * 
     * @param activityId 活动ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM evaluations WHERE activity_id = #{activityId} AND q1_satisfaction = 1")
    Long querySatisfactionLevel1Count(@Param("activityId") Long activityId);

    /**
     * 查询包含建议的评价数量
     * 
     * @param activityId 活动ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM evaluations WHERE activity_id = #{activityId} AND suggestion_text IS NOT NULL AND suggestion_text != ''")
    Long querySuggestionCount(@Param("activityId") Long activityId);
}
