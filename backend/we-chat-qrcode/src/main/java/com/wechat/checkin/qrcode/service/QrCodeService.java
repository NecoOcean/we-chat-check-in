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
}

