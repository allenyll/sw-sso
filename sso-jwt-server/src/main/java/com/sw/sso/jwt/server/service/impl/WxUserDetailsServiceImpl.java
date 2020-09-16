package com.sw.sso.jwt.server.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @Description:  微信用户
 * @Author:       allenyll
 * @Date:         2020/9/11 4:01 下午
 * @Version:      1.0
 */
@Service("wxUserDetailsService")
public class WxUserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }


}
