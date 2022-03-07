package com.hyb.controller;

import com.hyb.pojo.Type;
import com.hyb.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AboutController {

    @Autowired
    private TypeService typeService;

    @GetMapping("/about")
    public String about(Model model) {
        List<Type> types = typeService.selectList();
        model.addAttribute("types", types);
        return "about";
    }
}
