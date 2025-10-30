package com.wechat.checkin.qrcode.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.ResultCode;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成工具类
 * 使用ZXing库生成二维码图片
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
public class QrCodeGenerator {

    /**
     * 默认二维码宽度
     */
    private static final int DEFAULT_WIDTH = 300;

    /**
     * 默认二维码高度
     */
    private static final int DEFAULT_HEIGHT = 300;

    /**
     * 生成二维码（返回Base64编码）
     *
     * @param content 二维码内容
     * @return Base64编码的二维码图片
     */
    public static String generateQrCodeBase64(String content) {
        return generateQrCodeBase64(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 生成二维码（返回Base64编码）
     *
     * @param content 二维码内容
     * @param width 宽度
     * @param height 高度
     * @return Base64编码的二维码图片
     */
    public static String generateQrCodeBase64(String content, int width, int height) {
        try {
            // 配置二维码参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 高容错率
            hints.put(EncodeHintType.MARGIN, 1); // 边距

            // 生成二维码矩阵
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // 将矩阵转换为图片并输出到字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            // 转换为Base64编码
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            log.debug("二维码生成成功: content={}, size={}x{}", content.substring(0, Math.min(50, content.length())), width, height);

            return "data:image/png;base64," + base64Image;

        } catch (WriterException | IOException e) {
            log.error("二维码生成失败: content={}", content, e);
            throw new BusinessException(ResultCode.QRCODE_GENERATE_FAILED, "二维码生成失败: " + e.getMessage());
        }
    }

    /**
     * 验证二维码内容格式
     *
     * @param content 二维码内容
     * @return 是否有效
     */
    public static boolean validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        // 这里可以添加更多的内容格式验证
        return true;
    }
}

