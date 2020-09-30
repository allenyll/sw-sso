package com.sw.sso.jwt.server.service;

import com.sw.sso.jwt.server.entity.AuthToken;
import com.sw.sso.jwt.server.util.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/20 4:02 下午
 * @Version:      1.0
 */
public interface IAuthService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @param clientId 客户端
     * @param clientSecret 客户端密码
     * @return 登录信息
     */
    AuthToken login(AuthToken authToken, String username, String password, String clientId, String clientSecret);

    /**
     * 登录状态认证
     * @param request 请求响应
     * @param target 跳转连接
     * @return
     */
    Result<Map<String, Object>> getAuthStatus(HttpServletRequest request, String target, HttpServletResponse response);

    /**
     * 判断登陆是否有效
     * @param request
     * @param response
     * @return
     */
    Result<Map<String, Object>> authStatus(HttpServletRequest request, HttpServletResponse response);
}
