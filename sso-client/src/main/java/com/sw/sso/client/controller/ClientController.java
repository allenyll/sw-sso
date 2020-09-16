package com.sw.sso.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/25 3:16 下午
 * @Version:      1.0
 */
@Controller
@RequestMapping("client")
public class ClientController {

    @RequestMapping("getClient")
    public String getClient() {
        return "client";
    }
}
