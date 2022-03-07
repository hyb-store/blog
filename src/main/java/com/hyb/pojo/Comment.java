package com.hyb.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//评论
public class Comment implements Serializable {
    private Long id;
    private String nickname; //昵称
    private String content; //内容
    private String avatar; //头像地址
    private Date createTime; //评论时间
    private boolean isAdminComment; //是否是管理员评论

    private Blog blog; //对一

    private List<Comment> replyComment; //子回复

    private Comment parentComment; //父回复

    public Comment() {
    }

    public void setAdminComment(boolean adminComment) {
        isAdminComment = adminComment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public void setReplyComment(List<Comment> replyComment) {
        this.replyComment = replyComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getContent() {
        return content;
    }

    public String getAvatar() {
        return avatar;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Blog getBlog() {
        return blog;
    }

    public List<Comment> getReplyComment() {
        return replyComment;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public boolean isAdminComment() {
        return isAdminComment;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", content='" + content + '\'' +
                ", avatar='" + avatar + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
