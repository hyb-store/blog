package com.hyb.service;

import com.hyb.pojo.Comment;

import java.util.List;

public interface CommentService {

    void insert(Comment comment);

    List<Comment> selectList(Long blogId);

}
