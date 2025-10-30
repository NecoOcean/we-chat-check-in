package com.wechat.checkin.qrcode.util;

import com.wechat.checkin.common.exception.BusinessException;
import com.wechat.checkin.common.response.ResultCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码令牌提供者
 * 负责二维码令牌的生成和验证（基于JWT）
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@Component
public class QrCodeTokenProvider {

    /**
     * 二维码令牌密钥
     */
    @Value("${qrcode.secret:wechat-checkin-qrcode-secret-key-2024}")
    private String qrcodeSecret;

    /**
     * 令牌发行者
     */
    @Value("${qrcode.issuer:wechat-checkin-qrcode}")
    private String issuer;

    /**
     * 生成二维码令牌
     *
     * @param qrcodeId 二维码ID
     * @param activityId 活动ID
     * @param type 二维码类型
     * @param expireTime 过期时间
     * @return 二维码令牌
     */
    public String generateToken(Long qrcodeId, Long activityId, String type, Date expireTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("qrcodeId", qrcodeId);
        claims.put("activityId", activityId);
        claims.put("type", type);

        Date now = new Date();

        return Jwts.builder()
                .claims(claims)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expireTime)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * 验证令牌是否有效
     *
     * @param token 二维码令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("二维码令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从令牌中获取二维码ID
     *
     * @param token 二维码令牌
     * @return 二维码ID
     */
    public Long getQrcodeIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object qrcodeIdObj = claims.get("qrcodeId");
        
        if (qrcodeIdObj instanceof Integer) {
            return ((Integer) qrcodeIdObj).longValue();
        } else if (qrcodeIdObj instanceof Long) {
            return (Long) qrcodeIdObj;
        }
        
        throw new BusinessException(ResultCode.QRCODE_INVALID, "二维码ID格式错误");
    }

    /**
     * 从令牌中获取活动ID
     *
     * @param token 二维码令牌
     * @return 活动ID
     */
    public Long getActivityIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object activityIdObj = claims.get("activityId");
        
        if (activityIdObj instanceof Integer) {
            return ((Integer) activityIdObj).longValue();
        } else if (activityIdObj instanceof Long) {
            return (Long) activityIdObj;
        }
        
        throw new BusinessException(ResultCode.QRCODE_INVALID, "活动ID格式错误");
    }

    /**
     * 从令牌中获取二维码类型
     *
     * @param token 二维码令牌
     * @return 二维码类型
     */
    public String getTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("type", String.class);
    }

    /**
     * 检查令牌是否过期
     *
     * @param token 二维码令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 从令牌中获取声明
     *
     * @param token 二维码令牌
     * @return 声明
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("二维码令牌已过期: {}", e.getMessage());
            throw new BusinessException(ResultCode.QRCODE_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的二维码令牌: {}", e.getMessage());
            throw new BusinessException(ResultCode.QRCODE_INVALID);
        } catch (MalformedJwtException e) {
            log.warn("二维码令牌格式错误: {}", e.getMessage());
            throw new BusinessException(ResultCode.QRCODE_INVALID);
        } catch (SecurityException e) {
            log.warn("二维码令牌签名验证失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.QRCODE_INVALID);
        } catch (IllegalArgumentException e) {
            log.warn("二维码令牌参数错误: {}", e.getMessage());
            throw new BusinessException(ResultCode.QRCODE_INVALID);
        }
    }

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = qrcodeSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

