package com.sw.sso.jwt.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.sw.cache.util.CacheUtil;
import com.sw.sso.jwt.server.entity.*;
import com.sw.sso.jwt.server.factory.AuthUserFactory;
import com.sw.sso.jwt.server.mapper.CustomerMapper;
import com.sw.sso.jwt.server.properties.WeChatProperties;
import com.sw.sso.jwt.server.service.ICustomerService;
import com.sw.sso.jwt.server.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    WeChatProperties weChatProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${auth.wx.appId}")
    private String clientId;

    @Value("${auth.wx.appSecret}")
    private String clientSecret;

    private static final Set<String> SCOPE;

    static {
		SCOPE = Sets.newHashSet();
		SCOPE.add("read");
		SCOPE.add("write");
    }

    @Override
    public AuthToken token(String code) {
        AuthToken authToken = new AuthToken();
        UserDetails userDetails = loadUserByJsCode(code, authToken);
        String username = userDetails.getUsername();
        String password = passwordEncoder.encode(username);
        authService.login(authToken, username, password, clientId, clientSecret);
        return authToken;
    }

    private UserDetails loadUserByJsCode(String code,  AuthToken authToken) throws UsernameNotFoundException {
        WxCodeResponse wxCodeResponse = getWxCodeSession(code);
        String openid = wxCodeResponse.getOpenid();
        authToken.setOpenid(openid);
        cacheUtil.set(AuthConstants.WX_CURRENT_OPENID + "_" + openid, openid);
        if (StringUtils.isEmpty(openid)) {
            log.error("登陆出错:无openid信息, 返回结果: {}", wxCodeResponse);
            throw new UsernameNotFoundException("登录出错，未查询到openid信息");
        }

        // 根据openId查询是否存在用户
        Result<Customer> result = this.queryUserByOpenId(openid);
        if (!result.isSuccess()) {
            log.error("根据openId查询是否存在用户失败, 返回结果: {}", result);
            throw new UsernameNotFoundException("登录出错，根据openId查询是否存在用户失败");
        }
        Customer customer = result.getObject();
        Long customerId;
        if (customer == null) {
            customer = new Customer();
            String name = UUID.randomUUID().toString().replace("-", "");
            customer.setCustomerName(name);
            //用户名MD5然后再加密作为密码
            customer.setPassword(passwordEncoder.encode(name));
            customer.setOpenid(openid);
            customer.setAddTime(DateUtil.getCurrentDateTime());
            customerId = SnowflakeIdWorker.generateId();
            customerMapper.insert(customer);
        } else {
            customerId = customer.getId();
        }

        cacheUtil.set(AuthConstants.WX_SESSION_KEY + "_" + openid, wxCodeResponse.getSessionKey());
        User user = new User();
        user.setId(customerId);
        user.setUserName(customer.getCustomerName());
        user.setAccount(customer.getCustomerName());
        user.setPassword(customer.getPassword());
        List<GrantedAuthority> authorities = new ArrayList<>();
        AuthUser authUser = AuthUserFactory.create(user, authorities);
        return authUser;
    }

    private WxCodeResponse getWxCodeSession(String code) {

        String urlString = "?appid={appid}&secret={secret}&js_code={code}&grant_type={grantType}";
        Map<String, Object> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppId());
        map.put("secret", weChatProperties.getAppSecret());
        map.put("code", code);
        map.put("grantType", weChatProperties.getGrantType());
        String response = restTemplate.getForObject(weChatProperties.getSessionHost() + urlString, String.class, map);

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

    @Override
    public Customer selectUserByName(String username) {
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        wrapper.eq("customer_name", username);
        Customer customer = customerMapper.selectOne(wrapper);
        return customer;
    }

}
