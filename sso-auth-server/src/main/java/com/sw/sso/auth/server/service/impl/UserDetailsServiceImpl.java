package com.sw.sso.auth.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.sw.cache.util.CacheUtil;
import com.sw.sso.auth.server.entity.AuthUser;
import com.sw.sso.auth.server.entity.User;
import com.sw.sso.auth.server.factory.AuthUserFactory;
import com.sw.sso.auth.server.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:  加载用户信息
 * @Author:       allenyll
 * @Date:         2020/8/20 11:27 上午
 * @Version:      1.0
 */
@Slf4j
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    IUserService userService;

    @Autowired
    CacheUtil cacheUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.selectUserByName(username);
        if (user == null) {
            log.error("用户{}不存在", username);
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        List<String> permissions = userService.getUserRoleMenuList(username);

        List<GrantedAuthority> authorities = AuthUserFactory.mapToGrantedAuthorities(permissions);
        AuthUser authUser = AuthUserFactory.create(user, authorities);
        log.info("登录成功！用户: {}", JSON.toJSONString(authUser));
        return authUser;
    }
}
