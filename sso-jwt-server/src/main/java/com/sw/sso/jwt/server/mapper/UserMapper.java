package com.sw.sso.jwt.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sw.sso.jwt.server.entity.User;

import java.util.List;
import java.util.Map;

/**
 * @Description:  用户<user>持久层接口
 * @Author:       allenyll
 * @Date:         2020/5/4 8:49 下午
 * @Version:      1.0
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户id获取该用户所在角色下的菜单
     * @param username 参数
     * @return 列表
     */
    List<Map<String, String>> getUserRoleMenuList(String username);

}
