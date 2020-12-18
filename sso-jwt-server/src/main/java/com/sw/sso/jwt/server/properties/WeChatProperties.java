package com.sw.sso.jwt.server.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
* @Title: WeChatProperties
* @Package com.sw.wechat.properties
* @author yu.leilei
* @date 2018/10/19 11:44
* @version V1.0
*/
@Data
@Configuration
@PropertySource("classpath:application.yml")
public class WeChatProperties {

    @Value("${auth.wx.sessionHost}")
    private String sessionHost;

    @Value("${auth.wx.appId}")
    private String appId;

    @Value("${auth.wx.appSecret}")
    private String appSecret;

    @Value("${auth.wx.grantType}")
    private String grantType;

    @Value("${auth.wx2.appId}")
    private String appId2;

    @Value("${auth.wx2.appSecret}")
    private String appSecret2;

}
