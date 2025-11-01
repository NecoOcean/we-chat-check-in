package com.wechat.checkin.county.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.checkin.county.dto.CountyCreateRequest;
import com.wechat.checkin.county.dto.CountyQueryRequest;
import com.wechat.checkin.county.dto.CountyUpdateRequest;
import com.wechat.checkin.county.vo.CountyVO;

/**
 * 县域服务接口
 * 
 * @author WeChat Check-in System
 * @since 1.3.0
 */
public interface CountyService {

    /**
     * 创建县域
     * 
     * @param request 创建请求
     * @return 县域视图对象
     */
    CountyVO createCounty(CountyCreateRequest request);

    /**
     * 更新县域
     * 
     * @param code 县域编码
     * @param request 更新请求
     * @return 县域视图对象
     */
    CountyVO updateCounty(String code, CountyUpdateRequest request);

    /**
     * 删除县域（软删除）
     * 
     * @param code 县域编码
     */
    void deleteCounty(String code);

    /**
     * 启用县域
     * 
     * @param code 县域编码
     * @return 县域视图对象
     */
    CountyVO enableCounty(String code);

    /**
     * 禁用县域
     * 
     * @param code 县域编码
     * @return 县域视图对象
     */
    CountyVO disableCounty(String code);

    /**
     * 根据编码查询县域详情
     * 
     * @param code 县域编码
     * @return 县域视图对象
     */
    CountyVO getCountyByCode(String code);

    /**
     * 分页查询县域列表
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    Page<CountyVO> listCounties(CountyQueryRequest request);
}

