package com.sw.sso.auth.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sw.sso.auth.server.entity.User;

import java.util.List;

/**
 * @Description:  用户<User>服务接口
 * @Author:       allenyll
 * @Date:         2020/5/4 8:47 下午
 * @Version:      1.0
 */
public interface IUserService extends IService<User> {

    /**
     * 根据用户信息获取菜单
     * @param username 查询参数
     * @return 菜单列表
     */
    List<String> getUserRoleMenuList(String username);

    /**
     * 根据用户名查询用户
     * @param userName 用户名
     * @return 用户
     */
    User selectUserByName(String userName);
}
