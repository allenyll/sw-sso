package com.sw.sso.jwt.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sw.sso.jwt.server.entity.AuthToken;
import com.sw.sso.jwt.server.entity.Customer;
import com.sw.sso.jwt.server.util.Result;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/9/10 8:12 下午
 * @Version:      1.0
 */
public interface ICustomerService extends IService<Customer> {

    /**
     * 微信小程序登陆
     * @param code
     * @return
     */
    AuthToken token(String code);

    /**
     * 根据openid 查询用户
     * @param openid
     * @return
     */
    Result<Customer> queryUserByOpenId(String openid);

    /**
     * 根据客户名称
     * @param username
     * @return
     */
    Customer selectUserByName(String username);
}
