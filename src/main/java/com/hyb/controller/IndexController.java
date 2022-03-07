package com.hyb.controller;

import com.github.pagehelper.PageInfo;
import com.hyb.pojo.Blog;
import com.hyb.pojo.Type;
import com.hyb.service.BlogService;
import com.hyb.service.TypeService;
import com.hyb.utils.MarkdownUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    BlogService blogService;

    @Autowired
    TypeService typeService;

    @GetMapping({"/", "/index"})
    public String indexPage(@RequestParam(name = "page", required = true, defaultValue = "1") Integer page,
                            @RequestParam(name = "size", required = true, defaultValue = "6") Integer size, Model model) {
        PageInfo<Blog> pageInfo = blogService.selectList(page, size, true);//只查询发布的  不查询保存的
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("recommendBlog", blogService.selectList(6));//查询最新更新的6个blog
        Map<Type, Integer> map = typeService.selectList(6);//查询blog最多的前6个type
        model.addAttribute("typeMap", map);
        return "index";
    }

    @PostMapping("/search")
    public String search(@RequestParam(name = "page", required = true, defaultValue = "1") Integer page,
                         @RequestParam(name = "size", required = true, defaultValue = "6") Integer size,
                         String query, Model model) {
        model.addAttribute("pageInfo", blogService.selectList(page, size, query));
        model.addAttribute("query", query);
        return "search";
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id, Model model) {
        Blog blog = blogService.selectOne(id);
        blogService.incrView(id, blog);
        blog.setContent(MarkdownUtils.markdownToHtmlExtensions(blog.getContent()));
        model.addAttribute("blog", blog);
        return "blog";
    }

    @GetMapping("/footer/newBlog")
    public String footer(Model model){
        List<Blog> list = blogService.selectList(3);
        model.addAttribute("newBlogs", list);
        return "commons/bar :: newBlogList";
    }
}
