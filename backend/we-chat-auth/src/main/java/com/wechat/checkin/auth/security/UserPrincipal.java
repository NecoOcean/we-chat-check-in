package com.wechat.checkin.auth.security;

import com.wechat.checkin.auth.entity.Admin;
import com.wechat.checkin.common.enums.StatusEnum;
import com.wechat.checkin.common.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 用户主体类
 * 实现Spring Security的UserDetails接口
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 角色
     */
    private String role;

    /**
     * 县级代码
     */
    private String countyCode;

    /**
     * 用户状态
     */
    private StatusEnum status;

    /**
     * 权限列表
     */
    private Collection<String> roles;

    /**
     * 接口权限列表
     */
    private Collection<String> permissions;

    /**
     * 从Admin实体创建UserPrincipal
     *
     * @param admin 管理员实体
     * @return UserPrincipal
     */
    public static UserPrincipal create(Admin admin) {
        return UserPrincipal.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .password(admin.getPasswordHash())
                .role(admin.getRole())
                .countyCode(admin.getCountyCode())
                .status(admin.getStatus())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 根据角色返回权限
        String authority = "ROLE_" + role.toUpperCase();
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return StatusEnum.ENABLED.equals(status);
    }

    /**
     * 检查是否为市级管理员
     *
     * @return 是否为市级管理员
     */
    public boolean isCityAdmin() {
        return UserRoleEnum.CITY.getValue().equals(role);
    }

    /**
     * 检查是否为县级管理员
     *
     * @return 是否为县级管理员
     */
    public boolean isCountyAdmin() {
        return UserRoleEnum.COUNTY.getValue().equals(role);
    }
}