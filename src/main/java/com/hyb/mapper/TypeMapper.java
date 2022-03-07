package com.hyb.mapper;

import com.hyb.pojo.Type;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TypeMapper {
    /**
     *插入一个type
     * @param type
     */
    void insert(Type type);

    /**
     * 修改type
     * @param type
     */
    void update(Type type);

    /**
     * 删除type
     * @param id
     */
    void delete(Long id);

    /**
     * 根据id查询type
     * @param id
     * @return
     */
    Type selectOneById(Long id);

    /**
     * 根据name查询type
     * @param name
     * @return
     */
    Type selectOneByName(String name);

    /**
     * 查询所有type
     * @return
     */
    List<Type> selectList();

    /**
     * 查询所有及博客
     * @return
     */
    List<Type> selectListAndBlog();
}
