package com.hyb.service.impl;

import com.hyb.mapper.UserMapper;
import com.hyb.pojo.User;
import com.hyb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectOne(String username) {
        return userMapper.selectOne(username);
    }
}