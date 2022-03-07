package com.hyb.mapper;

import com.hyb.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 查询给定id的blog下所有的没有父comment的comment
     * @param blogId
     * @return
     */
    List<Comment> selectListByBlog(Long blogId);

    /**
     * 查询出给定id的comment的子评论
     * @param id
     * @return
     */
    List<Comment> selectListReply(Long id);

    /**
     * 通过id查询
     * @param id
     * @return
     */
    Comment selectOne(Long id);

    /**
     * 插入comment
     * @param comment
     */
    void insert(Comment comment);

}
