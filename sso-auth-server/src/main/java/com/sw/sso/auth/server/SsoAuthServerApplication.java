package com.sw.sso.auth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.sw")
@EnableDiscoveryClient
@SpringBootApplication
public class SsoAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsoAuthServerApplication.class, args);
    }

}
