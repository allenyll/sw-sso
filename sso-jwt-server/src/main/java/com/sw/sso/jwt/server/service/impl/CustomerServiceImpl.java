package com.sw.sso.jwt.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.cache.util.CacheUtil;
import com.sw.sso.jwt.server.entity.AuthToken;
import com.sw.sso.jwt.server.entity.Customer;
import com.sw.sso.jwt.server.entity.WxCodeResponse;
import com.sw.sso.jwt.server.mapper.CustomerMapper;
import com.sw.sso.jwt.server.properties.WeChatProperties;
import com.sw.sso.jwt.server.service.ICustomerService;
import com.sw.sso.jwt.server.util.AuthConstants;
import com.sw.sso.jwt.server.util.BaseConstants;
import com.sw.sso.jwt.server.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author yu.leilei
 * @since 2018-10-22
 */
@Slf4j
@Service("customerService")
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    WxUserDetailsServiceImpl wxUserDetailsService;

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${auth.wx.appId}")
    private String clientId;

    @Value("${auth.wx.appSecret}")
    private String clientSecret;

    @Override
    public AuthToken token(String code) {
        UserDetails userDetails = wxUserDetailsService.loadUserByUsername(code);
        String username = userDetails.getUsername();
        String password = passwordEncoder.encode(username);
        AuthToken authToken = authService.login(username, password, clientId, clientSecret);
        return authToken;
    }

    @Override
    public Result<Customer> queryUserByOpenId(String openid) {
        Result<Customer> result = new Result<>();
        String currentOpenId = cacheUtil.get(AuthConstants.WX_CURRENT_OPENID + "_" + openid);
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        if (openid.equals(currentOpenId)) {
            wrapper.eq("openid", openid);
            Customer customer = customerMapper.selectOne(wrapper);
            result.setObject(customer);
        } else {
            result.fail("当前登录用户不正确");
        }
        return result;
    }


}
