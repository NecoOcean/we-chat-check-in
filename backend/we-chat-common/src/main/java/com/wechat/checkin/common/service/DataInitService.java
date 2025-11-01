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

import java.util.List;
import java.util.Map;

/**
 * 数据初始化服务
 * 在应用启动时自动初始化系统必需的基础数据
 * 包括：县域数据、管理员账号等
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

    /**
     * 崇左市县域数据
     * 格式：县域代码（邮政编码）- 县域名称
     */
    private static final Map<String, String> CHONGZUO_COUNTIES = Map.of(
            "532200", "江州区",
            "532100", "扶绥县",
            "532500", "宁明县",
            "532400", "龙州县",
            "532300", "大新县",
            "532800", "天等县",
            "532600", "凭祥市"
    );

    @Override
    public void run(String... args) throws Exception {
        log.info("==================================================");
        log.info("开始执行系统数据初始化...");
        log.info("==================================================");
        
        try {
            // 1. 初始化县域数据
            initCounties();
            
            // 2. 初始化管理员账号
            initAdminUser();
            
            log.info("==================================================");
            log.info("系统数据初始化完成");
            log.info("==================================================");
        } catch (Exception e) {
            log.error("数据初始化失败", e);
            // 不抛出异常，避免影响应用启动
        }
    }

    /**
     * 初始化县域数据
     * 自动检查并初始化崇左市各县域数据
     */
    @Transactional
    public void initCounties() {
        log.info("【1/2】开始初始化县域数据...");

        try {
            // 检查counties表是否存在
            if (!isTableExists("counties")) {
                log.warn("counties表不存在，跳过县域数据初始化");
                return;
            }

            int insertCount = 0;
            int updateCount = 0;
            int skipCount = 0;

            for (Map.Entry<String, String> entry : CHONGZUO_COUNTIES.entrySet()) {
                String code = entry.getKey();
                String name = entry.getValue();

                try {
                    // 检查县域是否已存在
                    Integer count = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM counties WHERE code = ?",
                            Integer.class,
                            code
                    );

                    if (count != null && count > 0) {
                        // 已存在，检查是否需要更新
                        String existingName = jdbcTemplate.queryForObject(
                                "SELECT name FROM counties WHERE code = ?",
                                String.class,
                                code
                        );

                        if (!name.equals(existingName)) {
                            // 名称不同，更新
                            jdbcTemplate.update(
                                    "UPDATE counties SET name = ?, updated_time = NOW() WHERE code = ?",
                                    name, code
                            );
                            log.info("  ✓ 更新县域: {} - {} (原名称: {})", code, name, existingName);
                            updateCount++;
                        } else {
                            log.debug("  ○ 县域已存在且数据一致: {} - {}", code, name);
                            skipCount++;
                        }
                    } else {
                        // 不存在，插入新记录
                        jdbcTemplate.update(
                                "INSERT INTO counties (code, name, status, created_time, updated_time) " +
                                        "VALUES (?, ?, 'enabled', NOW(), NOW())",
                                code, name
                        );
                        log.info("  ✓ 新增县域: {} - {}", code, name);
                        insertCount++;
                    }
                } catch (Exception e) {
                    log.error("  × 处理县域数据失败: {} - {}, 错误: {}", code, name, e.getMessage());
                }
            }

            log.info("  县域数据初始化完成 - 新增: {} 条, 更新: {} 条, 跳过: {} 条", 
                    insertCount, updateCount, skipCount);

            // 显示当前所有县域
            List<Map<String, Object>> counties = jdbcTemplate.queryForList(
                    "SELECT code, name, status FROM counties ORDER BY code"
            );
            log.info("  当前系统中的县域列表 (共{}个):", counties.size());
            counties.forEach(county -> 
                    log.debug("    - {} : {} ({})", 
                            county.get("code"), 
                            county.get("name"), 
                            county.get("status"))
            );

        } catch (Exception e) {
            log.error("县域数据初始化失败", e);
            throw new RuntimeException("县域数据初始化失败", e);
        }
    }

    /**
     * 初始化管理员用户
     */
    @Transactional
    public void initAdminUser() {
        log.info("【2/2】开始初始化管理员账号...");
        
        try {
            // 检查admins表是否存在
            if (!isTableExists("admins")) {
                log.warn("admins表不存在，跳过管理员账号初始化");
                return;
            }

            // 检查是否已存在市级管理员
            String checkSql = "SELECT COUNT(*) FROM admins WHERE username = ? AND role = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, ADMIN_USERNAME, ADMIN_ROLE);
            
            if (count != null && count > 0) {
                log.info("  ○ 市级管理员账号已存在，跳过创建: {}", ADMIN_USERNAME);
                return;
            }

            // 创建市级管理员账号
            String insertSql = """
                INSERT INTO admins (username, password, role, county_code, status, created_time) 
                VALUES (?, ?, ?, NULL, 'enabled', NOW())
                """;
            
            String hashedPassword = passwordEncoder.encode(ADMIN_PASSWORD);
            int result = jdbcTemplate.update(insertSql, ADMIN_USERNAME, hashedPassword, ADMIN_ROLE);
            
            if (result > 0) {
                log.info("  ✓ 成功创建市级管理员账号: {} (密码: {})", ADMIN_USERNAME, ADMIN_PASSWORD);
            } else {
                log.warn("  × 市级管理员账号创建失败");
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
            String updateSql = "UPDATE admins SET password = ?, updated_time = NOW() WHERE username = ? AND role = ?";
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

    /**
     * 检查数据库表是否存在
     *
     * @param tableName 表名
     * @return true-存在，false-不存在
     */
    private boolean isTableExists(String tableName) {
        try {
            jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM " + tableName + " LIMIT 1",
                    Integer.class
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}