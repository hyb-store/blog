package com.hyb.controller;

import com.hyb.pojo.Blog;
import com.hyb.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class ArchivesController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/archives")
    public String archives(Model model) {
        Map<String, List<Blog>> map = blogService.selectMap();
        Set<String> strings = map.keySet();
        int counts = 0;
        for (String s : strings) {
            counts += map.get(s).size();
        }
        model.addAttribute("map", map);

        Set<Map.Entry<String, List<Blog>>> entries = map.entrySet();
        for (Map.Entry<String, List<Blog>> entry : entries) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

        model.addAttribute("counts", counts); //拿到blog总数
        return "archives";
    }
}
