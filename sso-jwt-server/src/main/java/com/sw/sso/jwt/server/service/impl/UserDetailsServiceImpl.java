package com.sw.sso.jwt.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.sw.sso.jwt.server.entity.AuthUser;
import com.sw.sso.jwt.server.entity.User;
import com.sw.sso.jwt.server.factory.AuthUserFactory;
import com.sw.sso.jwt.server.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    ClientDetailsService clientDetailsService;

    @Autowired
    IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用HTTP_BASIC认证，HTTP_BASIC中存储了CLIENT_ID和CLIENT_SECRET，开始认证client_id和client_secret
        if(authentication == null){
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if(clientDetails!=null){
                //秘钥
                String clientSecret = clientDetails.getClientSecret();
                //静态方式
                //return new User(username,new BCryptPasswordEncoder().encode(clientSecret), AuthorityUtils.commaSeparatedStringToAuthorityList(""));
                //数据库查找方式
                return new org.springframework.security.core.userdetails.User(username, clientSecret, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }

        User user = userService.selectUserByName(username);
        if (user == null) {
            log.error("用户{}不存在", username);
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        List<Map<String, String>> permissions = userService.getUserRoleMenuList(username);

        List<GrantedAuthority> authorities = AuthUserFactory.mapToGrantedAuthorities(permissions);
        AuthUser authUser = AuthUserFactory.create(user, authorities);
        log.info("登录成功！用户: {}", JSON.toJSONString(authUser));
        return authUser;
    }
}
