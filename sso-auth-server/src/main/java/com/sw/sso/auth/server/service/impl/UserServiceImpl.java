package com.sw.sso.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sw.sso.auth.server.entity.User;
import com.sw.sso.auth.server.mapper.UserMapper;
import com.sw.sso.auth.server.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description:  用户<user>服务实现
 * @Author:       allenyll
 * @Date:         2020/5/4 8:50 下午
 * @Version:      1.0
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    UserMapper userMapper;

    @Override
    public List<String> getUserRoleMenuList(String username) {
        return userMapper.getUserRoleMenuList(username);
    }

    @Override
    public User selectUserByName(String userName) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("USER_NAME", userName);
        return userMapper.selectOne(wrapper);
    }
}
