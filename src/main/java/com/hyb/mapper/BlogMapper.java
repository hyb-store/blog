package com.hyb.mapper;

import com.hyb.pojo.Blog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlogMapper {

    /**
     * 根据id查blog
     * @param id
     * @return
     */
    Blog selectOneById(Long id);

    /**
     * 根据title查blog
     * @param title
     * @return
     */
    Blog selectOneByTitle(String title);

    /**
     * 查询所有blog
     * @return
     */
    List<Blog> selectList();

    /**
     * 按照updateTime排序 查询size个
     * @param size
     * @return
     */
    List<Blog> selectListByUpdateTime(int size);

    /**
     * 首页搜索框查询
     * @param query
     * @return
     */
    List<Blog> selectListConditional(String query);

    /**
     * 根据条件查询
     * @param title
     * @param typeId
     * @param recommend
     * @return
     */
    List<Blog> selectListMultipleConditional(String title, Long typeId, boolean recommend);

    /**
     * 查询所有blog的create年份
     * @return
     */
    List<String> selectListYear();

    /**
     * 通过年份分组查询blog
     * @param year
     * @return
     */
    List<Blog> selectListByYear(String year);

    /**
     * views + 1
     * @param id
     */
    void updateViews(Long id);

    /**
     * 新增
     * @param blog
     */
    void insert(Blog blog);

    /**
     * 修改
     * @param blog
     */
    void update(Blog blog);

    /**
     * 删除id对应blog
     * @param id
     */
    void delete(Long id);
}
