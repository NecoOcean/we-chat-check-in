package com.wechat.checkin.common.service;

import com.wechat.checkin.common.enums.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据初始化服务
 * 在应用启动时自动初始化系统必需的基础数据
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Order(1) // 确保在其他初始化服务之前执行
public class DataInitService implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    /**
     * 系统内置管理员账号配置
     */
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_ROLE = UserRoleEnum.CITY.getValue();

    @Override
    public void run(String... args) throws Exception {
        log.info("开始执行数据初始化...");
        
        try {
            initAdminUser();
            log.info("数据初始化完成");
        } catch (Exception e) {
            log.error("数据初始化失败", e);
            throw e;
        }
    }

    /**
     * 初始化管理员用户
     */
    @Transactional
    public void initAdminUser() {
        try {
            // 检查是否已存在市级管理员
            String checkSql = "SELECT COUNT(*) FROM admins WHERE username = ? AND role = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, ADMIN_USERNAME, ADMIN_ROLE);
            
            if (count != null && count > 0) {
                log.info("市级管理员账号已存在，跳过创建");
                return;
            }

            // 创建市级管理员账号
            String insertSql = """
                INSERT INTO admins (username, password_hash, role, county_code, status, created_at) 
                VALUES (?, ?, ?, NULL, 'enabled', NOW())
                """;
            
            String hashedPassword = passwordEncoder.encode(ADMIN_PASSWORD);
            int result = jdbcTemplate.update(insertSql, ADMIN_USERNAME, hashedPassword, ADMIN_ROLE);
            
            if (result > 0) {
                log.info("成功创建市级管理员账号: {}", ADMIN_USERNAME);
            } else {
                log.warn("市级管理员账号创建失败");
            }
            
        } catch (Exception e) {
            log.error("初始化管理员用户失败", e);
            throw new RuntimeException("初始化管理员用户失败", e);
        }
    }

    /**
     * 重置管理员密码（仅用于开发和测试环境）
     */
    @Transactional
    public void resetAdminPassword() {
        try {
            String updateSql = "UPDATE admins SET password_hash = ?, updated_at = NOW() WHERE username = ? AND role = ?";
            String hashedPassword = passwordEncoder.encode(ADMIN_PASSWORD);
            int result = jdbcTemplate.update(updateSql, hashedPassword, ADMIN_USERNAME, ADMIN_ROLE);
            
            if (result > 0) {
                log.info("管理员密码重置成功: {}", ADMIN_USERNAME);
            } else {
                log.warn("管理员密码重置失败，用户不存在");
            }
        } catch (Exception e) {
            log.error("重置管理员密码失败", e);
            throw new RuntimeException("重置管理员密码失败", e);
        }
    }
}