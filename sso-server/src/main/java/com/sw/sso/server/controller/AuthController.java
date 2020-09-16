package com.sw.sso.server.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/20 2:22 下午
 * @Version:      1.0
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/getUser")
    public Object getCurrentUser(Authentication authentication) {
        return authentication.getPrincipal();
    }
}
