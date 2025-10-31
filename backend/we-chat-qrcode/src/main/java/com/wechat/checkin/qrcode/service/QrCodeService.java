package com.wechat.checkin.qrcode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wechat.checkin.qrcode.dto.GenerateQrCodeRequest;
import com.wechat.checkin.qrcode.dto.QrCodeQueryRequest;
import com.wechat.checkin.qrcode.vo.QrCodeVO;
import com.wechat.checkin.qrcode.vo.QrCodeVerifyResultVO;

/**
 * 二维码服务接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
public interface QrCodeService {

    /**
     * 为活动生成二维码
     *
     * @param activityId 活动ID
     * @param request 生成请求
     * @return 二维码信息
     */
    QrCodeVO generateQrCode(Long activityId, GenerateQrCodeRequest request);

    /**
     * 分页查询二维码列表
     *
     * @param request 查询请求
     * @return 二维码列表
     */
    Page<QrCodeVO> listQrCodes(QrCodeQueryRequest request);

    /**
     * 根据ID查询二维码详情
     *
     * @param id 二维码ID
     * @return 二维码详情
     */
    QrCodeVO getQrCodeById(Long id);

    /**
     * 禁用二维码
     *
     * @param id 二维码ID
     */
    void disableQrCode(Long id);

    /**
     * 批量禁用二维码
     *
     * @param ids 二维码ID列表
     */
    void disableQrCodesBatch(java.util.List<Long> ids);

    /**
     * 验证二维码（参与端使用）
     *
     * @param token 二维码令牌
     * @return 验证结果
     */
    QrCodeVerifyResultVO verifyQrCode(String token);

    /**
     * 根据令牌获取二维码信息
     *
     * @param token 二维码令牌
     * @return 二维码信息
     */
    QrCodeVO getQrCodeByToken(String token);

    /**
     * 按类型禁用二维码（新增）
     *
     * @param activityId 活动ID
     * @param type 二维码类型
     */
    void disableQrCodesByType(Long activityId, String type);

    /**
     * 按类型禁用除外的所有二维码（新增）
     * 
     * 示例：禁用打卡二维码但保留评价二维码
     * disableQrCodesExcept(activityId, "evaluation")
     *
     * @param activityId 活动ID
     * @param excludedTypes 需要保留的二维码类型（不禁用）
     */
    void disableQrCodesExcept(Long activityId, String... excludedTypes);

    /**
     * 验证特定类型的二维码（新增）
     * 
     * 相比 verifyQrCode，此方法额外检查二维码类型是否匹配
     *
     * @param token 二维码令牌
     * @param expectedType 期望的二维码类型
     * @return 验证结果
     */
    QrCodeVerifyResultVO verifyQrCodeOfType(String token, String expectedType);
}

