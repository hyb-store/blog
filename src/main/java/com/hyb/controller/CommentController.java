package com.hyb.controller;


import com.hyb.pojo.Comment;
import com.hyb.pojo.User;
import com.hyb.service.BlogService;
import com.hyb.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    @Value("${comment.avatar}")
    private String avatar;

    @GetMapping("/comments/{blogId}")
    public String commentList(@PathVariable Long blogId, Model model) {
        List<Comment> list = commentService.selectList(blogId);
        model.addAttribute("comments", list);  //根据blogId查询出所有没有parentComment的Comment
        return "blog :: commentList";
    }

    @PostMapping("/comments")
    public String insert(Comment comment, HttpServletRequest request) {
        Long blogId = comment.getBlog().getId();
        comment.setBlog(blogService.selectOne(blogId));

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null) { //管理员
            String avatar = user.getAvatar();
            String username = user.getUsername();
            comment.setAvatar(avatar);
            comment.setNickname(username);
            comment.setAdminComment(true);
        } else { //游客
            comment.setAvatar(avatar);
            comment.setAdminComment(false);
        }
        commentService.insert(comment);

        return "redirect:/comments/" + blogId;
    }
}
