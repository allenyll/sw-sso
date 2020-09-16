package com.sw.sso.jwt.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sw.sso.jwt.server.entity.Customer;
import com.sw.sso.jwt.server.mapper.CustomerMapper;
import com.sw.sso.jwt.server.service.ICustomerService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author yu.leilei
 * @since 2018-10-22
 */
@Service("customerService")
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

}
