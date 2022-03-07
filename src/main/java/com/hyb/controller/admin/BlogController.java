package com.hyb.controller.admin;

import com.github.pagehelper.PageInfo;
import com.hyb.pojo.Blog;
import com.hyb.pojo.User;
import com.hyb.service.BlogService;
import com.hyb.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @GetMapping("/blogs")
    public String list(@RequestParam(name = "page", required = true, defaultValue = "1") Integer page,
                       @RequestParam(name = "size", required = true, defaultValue = "5") Integer size,
                       Model model) {
        PageInfo<Blog> pageInfo = blogService.selectList(page, size);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("types", typeService.selectList());
        return "admin/blogs";
    }

    @GetMapping("/blogs/input")
    public String inputPage(Model model) {
        model.addAttribute("blog", new Blog());
        model.addAttribute("types", typeService.selectList());
        return "admin/blogs-input";
    }

    @GetMapping("/blogs/{id}")
    public String updatePage(@PathVariable Long id, Model model) {
        Blog blog = blogService.selectOne(id);
        model.addAttribute("blog", blog);
        model.addAttribute("types", typeService.selectList());
        return "admin/blogs-input";
    }

    @PostMapping("/blogs/search")
    public String search(@RequestParam(name = "page", required = true, defaultValue = "1") Integer page,
                         @RequestParam(name = "size", required = true, defaultValue = "5") Integer size,
                         Model model, String title, Long typeId, boolean recommend) {
        PageInfo<Blog> pageInfo = blogService.selectList(page, size, title, typeId, recommend);
        model.addAttribute("pageInfo", pageInfo);
        return "admin/blogs :: blogList";
    }

    @PostMapping("/blogs/input")
    public String insert(@Valid Blog blog, BindingResult result, RedirectAttributes attributes, HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");
        blog.setUser(user);
        blog.setType(typeService.selectOne(blog.getType().getId()));
        Blog b = null;
        if (blog.getId() != null) {  //修改的时候blog id 不为null
            System.out.println(blog);
            b = blogService.update(blog.getId(), blog);
        } else {  //新增 id为null
            if (blogService.selectOne(blog.getTitle()) != null) {
                result.rejectValue("title", "titleError", "该标题已存在");
            }
            if (result.hasErrors()) {
                System.out.println(result.getAllErrors().toString());
                model.addAttribute("types", typeService.selectList());
                return "admin/blogs-input";
            }
            b = blogService.insert(blog);
        }

        if (b == null) {
            attributes.addFlashAttribute("errMsg", "操作失败");
        } else {
            attributes.addFlashAttribute("successMsg", "操作成功");
        }

        return "redirect:/admin/blogs";
    }

    @DeleteMapping("/blogs/{id}")
    public String delete(@PathVariable Long id) {
        blogService.delete(id);
        return "redirect:/admin/blogs";
    }

}
