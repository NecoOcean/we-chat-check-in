package com.wechat.checkin.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.checkin.activity.entity.Activity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 活动数据访问层
 * 继承MyBatis Plus的BaseMapper，自动拥有CRUD能力
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    // BaseMapper 已提供以下方法，无需重复定义：
    // - insert(Activity entity): 插入实体
    // - selectById(Long id): 根据ID查询
    // - updateById(Activity entity): 根据ID更新
    // - deleteById(Long id): 根据ID删除
    // - selectList(Wrapper wrapper): 条件查询
    // - selectPage(Page page, Wrapper wrapper): 分页查询
    // 更多方法请参考 MyBatis Plus 官方文档

    /**
     * 根据活动ID统计参与教学点数量
     * 
     * @param activityId 活动ID
     * @return 参与教学点数量
     */
    @Select("SELECT COUNT(DISTINCT teaching_point_id) FROM checkins WHERE activity_id = #{activityId}")
    int countParticipatedTeachingPoints(@Param("activityId") Long activityId);

    /**
     * 根据活动ID统计总参与人数
     * 
     * @param activityId 活动ID
     * @return 总参与人数
     */
    @Select("SELECT COALESCE(SUM(attendee_count), 0) FROM checkins WHERE activity_id = #{activityId}")
    int countTotalAttendees(@Param("activityId") Long activityId);

    /**
     * 根据活动ID统计评价数量
     * 
     * @param activityId 活动ID
     * @return 评价数量
     */
    @Select("SELECT COUNT(*) FROM evaluations WHERE activity_id = #{activityId}")
    int countEvaluations(@Param("activityId") Long activityId);

    /**
     * 按县域统计打卡情况（v1.1.0新增）
     * 
     * @param activityId 活动ID
     * @return 县域统计列表
     */
    @Select("SELECT " +
            "COALESCE(tp.county_code, 'UNKNOWN') as countyCode, " +
            "COALESCE(ct.name, '未分类') as countyName, " +
            "COUNT(DISTINCT c.teaching_point_id) as participatingPoints, " +
            "COALESCE(SUM(c.attendee_count), 0) as totalAttendees, " +
            "COUNT(*) as totalCheckins " +
            "FROM checkins c " +
            "LEFT JOIN teaching_points tp ON c.teaching_point_id = tp.id " +
            "LEFT JOIN counties ct ON tp.county_code = ct.code " +
            "WHERE c.activity_id = #{activityId} " +
            "GROUP BY tp.county_code " +
            "ORDER BY tp.county_code")
    List<java.util.Map<String, Object>> selectCountyCheckinStatistics(@Param("activityId") Long activityId);

    /**
     * 查询活动的所有打卡详情（v1.1.0新增）
     * 
     * @param activityId 活动ID
     * @return 打卡详情列表
     */
    @Select("SELECT " +
            "c.id, " +
            "c.teaching_point_id as teachingPointId, " +
            "COALESCE(tp.name, '未知教学点') as teachingPointName, " +
            "COALESCE(tp.county_code, 'UNKNOWN') as countyCode, " +
            "COALESCE(ct.name, '未分类') as countyName, " +
            "c.attendee_count as attendeeCount, " +
            "c.submitted_time as submittedTime " +
            "FROM checkins c " +
            "LEFT JOIN teaching_points tp ON c.teaching_point_id = tp.id " +
            "LEFT JOIN counties ct ON tp.county_code = ct.code " +
            "WHERE c.activity_id = #{activityId} " +
            "ORDER BY tp.county_code, c.submitted_time DESC")
    List<java.util.Map<String, Object>> selectCheckinDetails(@Param("activityId") Long activityId);
}
