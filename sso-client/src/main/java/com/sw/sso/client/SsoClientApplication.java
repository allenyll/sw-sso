package com.sw.sso.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;

/**
 * @Description:  客户端启动类
 * @Author:       allenyll
 * @Date:         2020/8/26 9:13 上午
 * @Version:      1.0
 */
@SpringBootApplication
public class SsoClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsoClientApplication.class, args);
    }

}
