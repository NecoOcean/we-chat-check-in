package com.wechat.checkin.activity.mapper;

import com.wechat.checkin.activity.entity.Activity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 活动数据访问层
 */
@Mapper
public interface ActivityMapper {

    /**
     * 插入活动
     */
    @Insert("INSERT INTO activities (name, description, scope_county_code, start_time, end_time, " +
            "created_id, status, created_time, updated_time) " +
            "VALUES (#{name}, #{description}, #{scopeCountyCode}, #{startTime}, #{endTime}, " +
            "#{createdId}, #{status}, #{createdTime}, #{updatedTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Activity activity);

    /**
     * 根据ID查询活动
     */
    @Select("SELECT * FROM activities WHERE id = #{id}")
    @Results(id = "activityResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "scopeCountyCode", column = "scope_county_code"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "endTime", column = "end_time"),
            @Result(property = "endedTime", column = "ended_time"),
            @Result(property = "createdId", column = "created_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    Activity selectById(@Param("id") Long id);

    /**
     * 分页查询活动列表
     */
    @SelectProvider(type = ActivitySqlProvider.class, method = "selectActivityList")
    @ResultMap("activityResultMap")
    List<Activity> selectList(@Param("status") String status,
                               @Param("countyCode") String countyCode,
                               @Param("offset") Integer offset,
                               @Param("size") Integer size);

    /**
     * 统计活动总数
     */
    @SelectProvider(type = ActivitySqlProvider.class, method = "countActivities")
    long count(@Param("status") String status,
               @Param("countyCode") String countyCode);

    /**
     * 更新活动状态
     */
    @Update("UPDATE activities SET status = #{status}, " +
            "ended_time = #{endedTime}, updated_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("endedTime") java.time.LocalDateTime endedTime);

    /**
     * 根据活动ID统计参与教学点数量
     */
    @Select("SELECT COUNT(DISTINCT teaching_point_id) FROM checkins WHERE activity_id = #{activityId}")
    int countParticipatedTeachingPoints(@Param("activityId") Long activityId);

    /**
     * 根据活动ID统计总参与人数
     */
    @Select("SELECT COALESCE(SUM(attendee_count), 0) FROM checkins WHERE activity_id = #{activityId}")
    int countTotalAttendees(@Param("activityId") Long activityId);

    /**
     * 根据活动ID统计评价数量
     */
    @Select("SELECT COUNT(*) FROM evaluations WHERE activity_id = #{activityId}")
    int countEvaluations(@Param("activityId") Long activityId);

    /**
     * 动态SQL提供者
     */
    class ActivitySqlProvider {

        public String selectActivityList(@Param("status") String status,
                                          @Param("countyCode") String countyCode,
                                          @Param("offset") Integer offset,
                                          @Param("size") Integer size) {
            StringBuilder sql = new StringBuilder("SELECT * FROM activities WHERE 1=1");

            if (status != null && !status.isEmpty()) {
                sql.append(" AND status = #{status}");
            }

            if (countyCode != null && !countyCode.isEmpty()) {
                sql.append(" AND (scope_county_code = #{countyCode} OR scope_county_code IS NULL)");
            }

            sql.append(" ORDER BY created_time DESC");
            sql.append(" LIMIT #{size} OFFSET #{offset}");

            return sql.toString();
        }

        public String countActivities(@Param("status") String status,
                                       @Param("countyCode") String countyCode) {
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM activities WHERE 1=1");

            if (status != null && !status.isEmpty()) {
                sql.append(" AND status = #{status}");
            }

            if (countyCode != null && !countyCode.isEmpty()) {
                sql.append(" AND (scope_county_code = #{countyCode} OR scope_county_code IS NULL)");
            }

            return sql.toString();
        }
    }
}
