package com.wechat.checkin.auth.security;

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
 * JWT令牌提供者
 * 负责JWT令牌的生成、验证和解析
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtTokenProvider {

    /**
     * JWT密钥
     */
    @Value("${jwt.secret:wechat-checkin-system-jwt-secret-key-2024}")
    private String jwtSecret;

    /**
     * JWT访问令牌过期时间（毫秒）
     */
    @Value("${jwt.access-token-expiration:86400000}")
    private long accessTokenExpiration;

    /**
     * JWT刷新令牌过期时间（毫秒）
     */
    @Value("${jwt.refresh-token-expiration:604800000}")
    private long refreshTokenExpiration;

    /**
     * 令牌发行者
     */
    @Value("${jwt.issuer:wechat-checkin-system}")
    private String issuer;

    /**
     * 生成访问令牌
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param role 角色
     * @param countyCode 县级代码
     * @return JWT访问令牌
     */
    public String generateAccessToken(Long userId, String username, String role, String countyCode) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        claims.put("countyCode", countyCode);
        claims.put("tokenType", "access");

        return generateToken(claims, accessTokenExpiration);
    }

    /**
     * 生成刷新令牌
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT刷新令牌
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tokenType", "refresh");

        return generateToken(claims, refreshTokenExpiration);
    }

    /**
     * 生成令牌
     *
     * @param claims 声明
     * @param expiration 过期时间
     * @return JWT令牌
     */
    private String generateToken(Map<String, Object> claims, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 从令牌中获取角色
     *
     * @param token JWT令牌
     * @return 角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 从令牌中获取县级代码
     *
     * @param token JWT令牌
     * @return 县级代码
     */
    public String getCountyCodeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("countyCode", String.class);
    }

    /**
     * 从令牌中获取令牌类型
     *
     * @param token JWT令牌
     * @return 令牌类型
     */
    public String getTokenTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("tokenType", String.class);
    }

    /**
     * 从令牌中获取过期时间
     *
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 验证令牌是否有效
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查令牌是否过期
     *
     * @param token JWT令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 从令牌中获取声明
     *
     * @param token JWT令牌
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
            log.warn("JWT令牌已过期: {}", e.getMessage());
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的JWT令牌: {}", e.getMessage());
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        } catch (MalformedJwtException e) {
            log.warn("JWT令牌格式错误: {}", e.getMessage());
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        } catch (SecurityException e) {
            log.warn("JWT令牌签名验证失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            log.warn("JWT令牌参数错误: {}", e.getMessage());
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
    }

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 获取访问令牌过期时间（秒）
     *
     * @return 过期时间（秒）
     */
    public long getAccessTokenExpirationInSeconds() {
        return accessTokenExpiration / 1000;
    }
}