package com.sw.sso.jwt.server.service.impl;

import com.sw.cache.util.CacheUtil;
import com.sw.cache.util.StringUtil;
import com.sw.sso.jwt.server.entity.AuthToken;
import com.sw.sso.jwt.server.service.IAuthService;
import com.sw.sso.jwt.server.util.*;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:  授权登录业务
 * @Author:       allenyll
 * @Date:         2020/8/24 11:19 上午
 * @Version:      1.0
 */
@Slf4j
@Service("authService")
public class AuthServiceImpl implements IAuthService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CacheUtil cacheUtil;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Value("${auth.ttl}")
    private long ttl;

    @Override
    public AuthToken login(AuthToken authToken, String username, String password, String clientId, String clientSecret) {
        ServiceInstance serviceInstance = loadBalancerClient.choose("sso-auth");
        URI uri = serviceInstance.getUri();
        String url = uri + "/oauth/token";

        // 使用密码模式获取令牌
        MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.put("username", Collections.singletonList(username));
        bodyMap.put("password", Collections.singletonList(password));
        bodyMap.put("grant_type", Collections.singletonList("password"));
        bodyMap.put("scope", Collections.singletonList("all"));
        bodyMap.put("client_id", Collections.singletonList(clientId));
        bodyMap.put("client_secret", Collections.singletonList(clientSecret));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "multipart/form-data");
        // headers.set("authorization", AuthUtil.getHttpBasic(clientId, clientSecret));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(bodyMap, headers);

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                super.handleError(response);
            }
        });

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map data = response.getBody();
        log.info("授权返回令牌信息：{}", data);

        if (CollectionUtils.isEmpty(data) || data.get(AuthConstants.ACCESS_TOKEN) == null
                || data.get(AuthConstants.REFRESH_TOKEN) == null || data.get(AuthConstants.JTI) == null) {
            //申请令牌失败
            throw new RuntimeException("申请令牌失败");
        }

        authToken.setAccessToken((String) data.get(AuthConstants.ACCESS_TOKEN));
        authToken.setRefreshToken((String) data.get(AuthConstants.REFRESH_TOKEN));
        authToken.setJti((String) data.get(AuthConstants.JTI));

        cacheUtil.set(authToken.getJti(), authToken.getAccessToken(), ttl);

        return authToken;
    }

    @Override
    public Result<Map<String, Object>> getAuthStatus(HttpServletRequest request, String target, HttpServletResponse response) {
        Result<Map<String, Object>> result = new Result();
        // 从Cookie中获取jti
        Map<String, String> cookieMap = CookieUtil.getCookie(request, "uid");
        String uid = "";
        if (!CollectionUtils.isEmpty(cookieMap)) {
            uid = cookieMap.get("uid");
        }
        Map<String, Object> resultMap = new HashMap<>(5);
        if (StringUtils.isNotEmpty(uid)) {
            // 根据uid从redis中获取token
            String authToken = cacheUtil.get(uid, String.class);
            try {
                // 如果令牌存在,解析jwt令牌,判断该令牌是否合法,如果令牌不合法,则向客户端返回错误提示信息
                Claims claims = JwtUtil.verifyToken(authToken);
            } catch (Exception e) {
                e.printStackTrace();
                result.fail("令牌解析失败");
                return result;
            }
            resultMap.put("authToken", authToken);
            resultMap.put("uid", uid);
            result.setObject(resultMap);
        }
        return result;
    }

    @Override
    public Result<Map<String, Object>> authStatus(HttpServletRequest request, HttpServletResponse response) {
        Result<Map<String, Object>> result = new Result();
        // 从Cookie中获取jti
        Map<String, String> cookieMap = CookieUtil.getCookie(request, "uid");
        String uid = "";
        if (!CollectionUtils.isEmpty(cookieMap)) {
            uid = cookieMap.get("uid");
        }
        Map<String, Object> resultMap = new HashMap<>(5);
        if (StringUtils.isNotEmpty(uid)) {
            // 根据uid从redis中获取token
            String authToken = cacheUtil.get(uid, String.class);
            if (StringUtil.isEmpty(authToken)) {
                result.fail("token缓存失效");
                return result;
            } else {
                try {
                    // 如果令牌存在,解析jwt令牌,判断该令牌是否合法,如果令牌不合法,则向客户端返回错误提示信息
                    Claims claims = JwtUtil.verifyToken(authToken);
                } catch (Exception e) {
                    e.printStackTrace();
                    result.fail("令牌解析失败");
                    return result;
                }
            }
            resultMap.put("callback", request.getParameter("callback"));
            return result;
        } else {
            result.fail("令牌解析失败");
            return result;
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 从Cookie中获取jti
        Map<String, String> cookieMap = CookieUtil.getCookie(request, "uid");
        String uid = "";
        if (!CollectionUtils.isEmpty(cookieMap)) {
            uid = cookieMap.get("uid");
        }
        if (StringUtils.isNotEmpty(uid)) {
            cacheUtil.remove(uid);
        }
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("52c970fbbf76dd8a5ea4b942fca5f6b3"));
    }
}
