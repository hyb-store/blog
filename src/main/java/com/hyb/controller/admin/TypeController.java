package com.hyb.controller.admin;

import com.github.pagehelper.PageInfo;

import com.hyb.pojo.Type;
import com.hyb.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TypeController {

    @Autowired
    private TypeService typeService;

    /**
     * 分页查询所有types 并跳转到admin/types.html
     * @param page
     * @param size
     * @param model
     * @return
     */
    @GetMapping("/types")
    public String indexPage(@RequestParam(name = "page", required = true, defaultValue = "1") Integer page,
                                @RequestParam(name = "size", required = true, defaultValue = "5") Integer size,
                                 Model model) {
        PageInfo<Type> pageInfo = typeService.selectList(page, size);
        model.addAttribute("pageInfo", pageInfo);
        return "admin/types";
    }

    /**
     * 跳转到更新type的页面，并且将要修改的type传过去
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/types/{id}")
    public String updatePage(@PathVariable Long id, Model model) {
        model.addAttribute("type", typeService.selectOne(id));
        return "admin/types-input";
    }

    /**
     * 跳转到添加type的页面
     * @param model
     * @return
     */
    @GetMapping("/types/input")
    public String insertPage(Model model) {
        model.addAttribute("type", new Type());
        return "admin/types-input";
    }

    /**
     * 添加type，并重定向到type首页
     * @param type
     * @param result
     * @param attributes
     * @return
     */
    // @Valid和BindingResult配套使用，@Valid用在参数前，BindingResult作为校验结果绑定返回。
    @PostMapping("/types")
    public String insert(@Valid Type type, BindingResult result, RedirectAttributes attributes) {
        if (typeService.selectOne(type.getName()) != null) {
            // 向BindingResult添加分类已存在的校验错误
            result.rejectValue("name", "nameError", "该分类已存在");
        }
        if (result.hasErrors()) {//bindingResult.hasErrors()判断是否校验通过
            return "admin/types-input";
        }
        Type t = typeService.insert(type);
        if (t == null) {
            attributes.addFlashAttribute("errMsg","新增失败");
        } else {
            attributes.addFlashAttribute("successMsg","新增成功");
        }
        return "redirect:/admin/types";  //返回到 /admin/type 请求 再去查询
    }


    /**
     * 更新type，并重定向到type首页
     * @param id
     * @param type
     * @param result
     * @param attributes
     * @return
     */
    @PutMapping("/types/{id}")
    public String update(@PathVariable Long id, @Valid Type type, BindingResult result, RedirectAttributes attributes) {
        if (typeService.selectOne(type.getName()) != null) {
            result.rejectValue("name", "nameError", "该分类已存在");
        }
        if (result.hasErrors()) {
            return "admin/types-input";
        }
        Type t = typeService.update(id, type);
        if (t == null) {
            attributes.addFlashAttribute("errMsg","更新失败");
        } else {
            attributes.addFlashAttribute("successMsg","更新成功");
        }
        return "redirect:/admin/types";  //返回到 /admin/type 请求 再去查询
    }

    /**
     * 删除type，并重定向到type首页
     * @param id
     * @param attributes
     * @return
     */
    @DeleteMapping("/types/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        typeService.delete(id);
        attributes.addFlashAttribute("successMsg", "删除成功");
        return "redirect:/admin/types";
    }
}
