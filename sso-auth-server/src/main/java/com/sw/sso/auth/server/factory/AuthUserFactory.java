package com.sw.sso.auth.server.factory;

import com.sw.sso.auth.server.entity.AuthUser;
import com.sw.sso.auth.server.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 创建自定义AuthUser工厂，用户根据用户创建自定义AuthUser
 * @Author: yu.leilei
 * @Date: 下午 4:31 2018/5/24 0024
 */
public class AuthUserFactory {

    public AuthUserFactory() {
    }

    /**
     * 根据系统用户创建子订单jwtUser
     * @param user 用户
     * @param authorities 权限
     * @return authUser
     */
    public static AuthUser create(User user, List<GrantedAuthority> authorities) {
        return new AuthUser(
                user.getId(),
                user.getAccount(),
                user.getPassword(),
                authorities,
                user.getLastPasswordResetDate()
        );
    }

    public static List<GrantedAuthority> mapToGrantedAuthorities(List<String> authorities) {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
