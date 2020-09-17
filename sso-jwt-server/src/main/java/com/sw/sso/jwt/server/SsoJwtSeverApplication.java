package com.sw.sso.jwt.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/20 7:11 下午
 * @Version:      1.0
 */
@ComponentScan("com.sw")
@EnableDiscoveryClient
@SpringBootApplication
public class SsoJwtSeverApplication {

//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    public static void main(String[] args) {
        SpringApplication.run(SsoJwtSeverApplication.class, args);
    }

}
