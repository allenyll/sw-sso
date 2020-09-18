package com.sw.sso.jwt.server.controller;

import com.sw.sso.jwt.server.entity.AuthToken;
import com.sw.sso.jwt.server.service.impl.CustomerServiceImpl;
import com.sw.sso.jwt.server.util.Result;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/20 2:22 下午
 * @Version:      1.0
 */
@RestController
@RequestMapping("/wx/auth")
public class WxAuthController {

    @Autowired
    private CustomerServiceImpl customerService;
    /**
     * 微信小程序登录
     * @return
     */
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public Result<AuthToken> token(@RequestBody Map<String, String> param) {
        Result<AuthToken> result = new Result<>();
        String code = MapUtils.getString(param, "code");
        //校验参数
        if (StringUtils.isEmpty(code)) {
            throw new RuntimeException("小程序认证编码code不能为空");
        }

        AuthToken authToken = customerService.token(code);
        //CookieUtil.setCookie(response, cookieDomain, "/", "uid", authToken.getJti(), cookieMaxAge, false);

        result.setObject(authToken);
        return result;
    }

}
