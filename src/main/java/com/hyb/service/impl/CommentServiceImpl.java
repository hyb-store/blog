package com.hyb.service.impl;

import com.hyb.mapper.CommentMapper;
import com.hyb.pojo.Comment;
import com.hyb.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;


    private List<Comment> tempList = new ArrayList<>();

    /**
     * 查询给定id的blog下所有的没有父comment的comment
     * 并且将他们的子评论封装进去
     * @param blogId
     * @return
     */
    @Override
    public List<Comment> selectList(Long blogId) {
        //首先查询出所有没有parent的comment
        List<Comment> list = commentMapper.selectListByBlog(blogId);

        for (Comment comment : list) {
            //查询出子回复  即所有parentId = comment.id 的评论
            List<Comment> replyList = commentMapper.selectListReply(comment.getId());
            for (Comment reply : replyList) {
                tempList.add(reply);
                //子回复也可能有子回复
                recReply(reply);
            }
            comment.setReplyComment(tempList);

            for (Comment c : tempList) {
                c.getParentComment().setNickname(commentMapper.selectOne(c.getParentComment().getId()).getNickname());
            }

            tempList = new ArrayList<>();
        }
        
        return list;
    }

    private void recReply(Comment comment) {
        List<Comment> replyList = commentMapper.selectListReply(comment.getId());
        if (replyList.size() > 0) {
            for (Comment reply : replyList) {
                tempList.add(reply);  //如果不用全局变量  这个reply将无法返回
                if (commentMapper.selectListReply(reply.getId()).size() > 0) {
                    //子回复也可能有子回复
                    recReply(reply);
                }
            }
        }
    }

    @Override
    public void insert(Comment comment) {
        comment.setCreateTime(new Date());
        Long parentCommentId = comment.getParentComment().getId();
        if (parentCommentId == -1) {
            comment.setParentComment(null);
        } else {
            //查出父评论 封装
            Comment parentComment = commentMapper.selectOne(parentCommentId);
            comment.setParentComment(parentComment);
        }
        commentMapper.insert(comment);
    }


}
