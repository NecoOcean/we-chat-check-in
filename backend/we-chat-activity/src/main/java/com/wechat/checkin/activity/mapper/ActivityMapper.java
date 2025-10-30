package com.wechat.checkin.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.checkin.activity.entity.Activity;
import org.apache.ibatis.annotations.*;

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
}
