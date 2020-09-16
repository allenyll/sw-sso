package com.sw.sso.client.config;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @Description:  单点登录配置
 * @Author:       allenyll
 * @Date:         2020/8/25 3:13 下午
 * @Version:      1.0
 */
@Configuration
@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.logout().logoutSuccessUrl("http://localhost:8088/logout");
        http.authorizeRequests().anyRequest().authenticated();
        http.csrf().disable();
    }
}
