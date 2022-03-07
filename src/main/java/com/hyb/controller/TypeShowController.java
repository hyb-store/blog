package com.hyb.controller;

import com.github.pagehelper.PageInfo;
import com.hyb.pojo.Blog;
import com.hyb.pojo.Type;
import com.hyb.service.BlogService;
import com.hyb.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Set;

@Controller
public class TypeShowController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private BlogService blogService;

    /**
     * 查询所有type，并且查询所有type下的所有blog，按照数量排序
     * 并且对blog分页
     * @param id
     * @param page
     * @param size
     * @param model
     * @return
     */
    @GetMapping({"/types/{id}", "/types"})
    public String show(@PathVariable(required = false) Long id,
                    @RequestParam(name = "page", required = true, defaultValue = "1") Integer page,
                    @RequestParam(name = "size", required = true, defaultValue = "5") Integer size,
                    Model model) {
        if (id == null) {
            id = -1L;
        }
        //首先查询所有type
        Map<Type, Integer> map = typeService.selectList(10000);

        if (id == -1) {  //从导航栏点击过去 默认-1
            Set<Type> types = map.keySet();
            for (Type type : types) {
                if (type != null) {
                    id = type.getId();  //默认取blog最多的type
                    break;
                }
            }
        }

        //查询给定id的type下所有的blog 对blog分页
        PageInfo<Blog> pageInfo = blogService.selectList(page, size, null, id, true);
        model.addAttribute("typeMap", map);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("activeId", id);
        return "types";
    }
}
