package com.sw.sso.jwt.server.controller;

import com.alibaba.fastjson.JSON;
import com.sw.sso.jwt.server.entity.AuthToken;
import com.sw.sso.jwt.server.service.IAuthService;
import com.sw.sso.jwt.server.util.CookieUtil;
import com.sw.sso.jwt.server.util.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/20 2:22 下午
 * @Version:      1.0
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    IAuthService authService;

    @Value("${auth.common.clientId}")
    private String clientId;

    @Value("${auth.common.clientSecret}")
    private String clientSecret;

    @Value("${auth.cookieDomain}")
    private String cookieDomain;


    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    private static final String MEDIA_TYPE = "application/javascript;charset=UTF-8";

    @GetMapping("/loginPage")
    public String loginPage(@RequestParam(value = "from",required = false,defaultValue = "") String from, Model model) {
        model.addAttribute("from",from);
        return "login";
    }

    /**
     * 后端登录
     * @param username
     * @param password
     * @param response
     * @return
     */
    @RequestMapping("login")
    @ResponseBody
    public Result<AuthToken> login(String username, String password, HttpServletResponse response) {
        Result<AuthToken> result = new Result<>();
        //校验参数
        if (StringUtils.isEmpty(username)){
            throw new RuntimeException("请输入用户名");
        }
        if (StringUtils.isEmpty(password)){
            throw new RuntimeException("请输入密码");
        }
        AuthToken authToken = new AuthToken();
        authService.login(authToken, username, password, clientId, clientSecret);

        CookieUtil.setCookie(response, cookieDomain, "/", "uid", authToken.getJti(), cookieMaxAge, false);

        result.setObject(authToken);
        return result;
    }

    /**
     * 认证token是否有效，跳转到指定连接
     * @param target
     * @param request
     * @param response
     */
    @ResponseBody
    @GetMapping("getAuthStatus")
    public void getAuthStatus(@RequestParam(value = "target",required = false,defaultValue = "") String target,
                                HttpServletRequest request, HttpServletResponse response) {
        Result<Map<String, Object>> result = authService.getAuthStatus(request, target, response);
        if (result.isSuccess() && result.getObject() != null) {
            CookieUtil.setCookie(response, cookieDomain, "/", "authToken", (String) result.getObject().get("authToken"), cookieMaxAge, false);
            // 跳转到指定地址
            try {
                System.out.println(target);
                response.sendRedirect(target);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断登陆是否有效
     * @param request
     * @param response
     */
    @RequestMapping(value = "authStatus", method = RequestMethod.GET, produces= MEDIA_TYPE)
    public void authStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Result<Map<String, Object>> result = authService.authStatus(request, response);
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "No-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("application/javascript;charset=UTF-8");
        String callback = request.getParameter("callback");
        if (StringUtils.isEmpty(callback)) {
            callback = "callback";
        }
        if (result.isSuccess()) {
            response.getWriter().write(callback + "({" + "\"isLogin\":" + true + "})" + ";");
            response.getWriter().flush();
        } else {
            response.getWriter().write(callback + "({" + "\"isLogin\":" + false + "})" + ";");
            response.getWriter().flush();
        }
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public void logout(HttpSession session, @RequestParam String service, HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        session.invalidate();
        try {
            response.sendRedirect(service);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
