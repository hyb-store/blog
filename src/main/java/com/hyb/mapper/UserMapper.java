package com.hyb.mapper;

import com.hyb.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectOne(String username);
}
