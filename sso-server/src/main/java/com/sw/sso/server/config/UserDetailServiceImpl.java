package com.sw.sso.server.config;

import com.sw.sso.server.entity.AuthUser;
import com.sw.sso.server.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:  加载用户信息
 * @Author:       allenyll
 * @Date:         2020/8/20 11:27 上午
 * @Version:      1.0
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private List<AuthUser> userList;

    @Resource
    PasswordEncoder passwordEncoder;

    /**
     * 初始化用户，测试
     */
    @PostConstruct
    public void initUser() {
        String password = passwordEncoder.encode("123456");
        userList = new ArrayList<>();
        userList.add(new AuthUser("1", "allenyll", password,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"), new Date()));
        userList.add(new AuthUser("2", "snu", password,
                AuthorityUtils.commaSeparatedStringToAuthorityList("client"), new Date()));
        userList.add(new AuthUser("3", "sss", password,
                AuthorityUtils.commaSeparatedStringToAuthorityList("client"), new Date()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<AuthUser> users = userList.stream().filter(user -> user.getUsername().equals(username)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(users)) {
            return users.get(0);
        } else {
            throw new UsernameNotFoundException("用户或密码错误！");
        }
    }
}
