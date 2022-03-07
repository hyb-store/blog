package com.hyb.service;


import com.github.pagehelper.PageInfo;
import com.hyb.pojo.Blog;

import java.util.List;
import java.util.Map;

public interface BlogService {
    Blog insert(Blog blog);

    Blog update(Long id, Blog blog);

    //访问量+1
    void incrView(Long id, Blog blog);

    void delete(Long id);

    Blog selectOne(Long id);

    Blog selectOne(String title);

    //按照updateTime排序 查询前面size个
    List<Blog> selectList(int size);

    //分页查询
    PageInfo<Blog> selectList(int page, int size);

    //分页查询所有已经发布的
    PageInfo<Blog> selectList(int page, int size, boolean published);

    //首页按照输入框的内容条件查询
    PageInfo<Blog> selectList(int page, int size, String query);

    //查询所有符合联合条件的
    PageInfo<Blog> selectList(int page, int size, String title, Long typeId, boolean recommend);

    //归档 key是年份 value是对应的所有blog
    Map<String, List<Blog>> selectMap();

}
