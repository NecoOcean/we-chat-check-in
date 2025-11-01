package com.wechat.checkin.teachingpoint.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.checkin.teachingpoint.entity.TeachingPoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 教学点数据访问层
 * 继承MyBatis Plus的BaseMapper，自动拥有CRUD能力
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Mapper
public interface TeachingPointMapper extends BaseMapper<TeachingPoint> {

    // BaseMapper 已提供以下方法，无需重复定义：
    // - insert(TeachingPoint entity): 插入实体
    // - selectById(Long id): 根据ID查询
    // - updateById(TeachingPoint entity): 根据ID更新
    // - deleteById(Long id): 根据ID删除
    // - selectList(Wrapper wrapper): 条件查询
    // - selectPage(Page page, Wrapper wrapper): 分页查询
    // 更多方法请参考 MyBatis Plus 官方文档

    /**
     * 根据教学点名称统计数量（排除指定ID）
     * 用于检查教学点名称在同一县域内是否重复
     * 
     * @param name 教学点名称
     * @param countyCode 县域编码
     * @param excludeId 要排除的教学点ID（更新时使用）
     * @return 统计数量
     */
    @Select("SELECT COUNT(*) FROM teaching_points " +
            "WHERE name = #{name} AND county_code = #{countyCode} " +
            "AND id != #{excludeId} AND status != 'deleted'")
    int countByNameAndCountyExcludeId(@Param("name") String name, 
                                      @Param("countyCode") String countyCode,
                                      @Param("excludeId") Long excludeId);

    /**
     * 根据教学点名称和县域统计数量
     * 用于检查教学点名称在同一县域内是否已存在
     * 
     * @param name 教学点名称
     * @param countyCode 县域编码
     * @return 统计数量
     */
    @Select("SELECT COUNT(*) FROM teaching_points " +
            "WHERE name = #{name} AND county_code = #{countyCode} " +
            "AND status != 'deleted'")
    int countByNameAndCounty(@Param("name") String name, 
                             @Param("countyCode") String countyCode);

    /**
     * 根据县域编码查询县域名称
     * 
     * @param countyCode 县域编码
     * @return 县域名称，如果不存在返回null
     */
    @Select("SELECT name FROM counties WHERE code = #{countyCode} AND status = 'enabled'")
    String getCountyNameByCode(@Param("countyCode") String countyCode);
}

