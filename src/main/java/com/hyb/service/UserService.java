package com.hyb.service;

import com.hyb.pojo.User;

public interface UserService {
    User selectOne(String username);
}