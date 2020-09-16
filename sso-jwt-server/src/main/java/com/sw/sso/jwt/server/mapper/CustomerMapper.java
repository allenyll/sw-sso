package com.sw.sso.jwt.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sw.sso.jwt.server.entity.Customer;
import org.springframework.stereotype.Repository;


/**
 * <p>
  * 会员表 Mapper 接口
 * </p>
 *
 * @author yu.leilei
 * @since 2018-10-22
 */
@Repository("customerMapper")
public interface CustomerMapper extends BaseMapper<Customer> {

}
