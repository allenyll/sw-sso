package com.sw.sso.jwt.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.cache.util.CacheUtil;
import com.sw.sso.jwt.server.entity.AuthUser;
import com.sw.sso.jwt.server.entity.Customer;
import com.sw.sso.jwt.server.entity.User;
import com.sw.sso.jwt.server.entity.WxCodeResponse;
import com.sw.sso.jwt.server.factory.AuthUserFactory;
import com.sw.sso.jwt.server.properties.WeChatProperties;
import com.sw.sso.jwt.server.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.apache.HttpClientUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

/**
 * @Description:  微信用户
 * @Author:       allenyll
 * @Date:         2020/9/11 4:01 下午
 * @Version:      1.0
 */
@Slf4j
@Service("wxUserDetailsService")
public class WxUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    WeChatProperties weChatProperties;

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    CacheUtil cacheUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WxCodeResponse wxCodeResponse = getWxCodeSession(username);
        String openid = wxCodeResponse.getOpenid();
        if (StringUtils.isEmpty(openid)) {
            log.error("登陆出错:无openid信息, 返回结果: {}", wxCodeResponse);
            throw new UsernameNotFoundException("登录出错，未查询到openid信息");
        }

        // 根据openId查询是否存在用户
        Result<Customer> result = customerService.queryUserByOpenId(openid);
        if (!result.isSuccess()) {
            log.error("根据openId查询是否存在用户失败, 返回结果: {}", result);
            throw new UsernameNotFoundException("登录出错，根据openId查询是否存在用户失败");
        }
        Customer customer = result.getObject();
        Long customerId = customer.getId();
        if (customer == null) {
            customer = new Customer();
            String name = UUID.randomUUID().toString().replace("-", "");
            customer.setCustomerName(username);
            //用户名MD5然后再加密作为密码
            customer.setPassword(passwordEncoder.encode(name));
            customer.setOpenid(openid);
            customer.setAddTime(DateUtil.getCurrentDateTime());
            customerId = SnowflakeIdWorker.generateId();
            customerService.save(customer);
        }

        cacheUtil.set(AuthConstants.WX_SESSION_KEY + "_" + openid, wxCodeResponse.getSessionKey());
        cacheUtil.set(AuthConstants.WX_CURRENT_OPENID + "_" + openid, openid);
        User user = new User();
        user.setId(customerId);
        user.setUserName(customer.getCustomerName());
        user.setPassword(customer.getPassword());
        List<GrantedAuthority> authorities = new ArrayList<>();
        AuthUser authUser = AuthUserFactory.create(user, authorities);
        return authUser;
    }

    /**
     * code2session
     * @param code
     * @return
     */
    private WxCodeResponse getWxCodeSession(String code) {

//        String urlString = "?appid={appid}&secret={secret}&js_code={code}&grant_type={grantType}";
//        Map<String, Object> map = new HashMap<>();
//        map.put("appid", weChatProperties.getAppId());
//        map.put("secret", weChatProperties.getAppSecret());
//        map.put("code", code);
//        map.put("grantType", weChatProperties.getGrantType());
//        String response = restTemplate.getForObject(weChatProperties.getSessionHost() + urlString, String.class, map);

        String url = weChatProperties.getSessionHost();
        String param = "appid=" + weChatProperties.getAppId() + "&secret="+ weChatProperties.getAppSecret() +
                "&js_code=" + code + "&grant_type=" + weChatProperties.getGrantType();

        String response = HttpRequest.sendGet(url, param);
        ObjectMapper objectMapper = new ObjectMapper();
        WxCodeResponse wxCodeResponse;
        try {
            wxCodeResponse = objectMapper.readValue(response, WxCodeResponse.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            wxCodeResponse = null;
            e.printStackTrace();
        }

        log.info(wxCodeResponse.toString());
        if (null == wxCodeResponse) {
            throw new RuntimeException("调用微信接口失败");
        }
        if (wxCodeResponse.getErrcode() != null) {
            throw new RuntimeException(wxCodeResponse.getErrMsg());
        }

        return wxCodeResponse;
    }

}
