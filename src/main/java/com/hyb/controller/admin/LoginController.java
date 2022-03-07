package com.hyb.controller.admin;

import com.hyb.pojo.User;
import com.hyb.service.UserService;
import com.hyb.utils.CookieUtils;
import com.hyb.utils.JWTUtils;
import com.hyb.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String loginPage(HttpServletRequest request, HttpServletResponse response) {
        Cookie token = CookieUtils.get(request, "token");
        if (token != null) {
            //验证token
            try {
                JWTUtils.verifyToken(token.getValue());
                CookieUtils.delete(request, response, "tokenInvalid");
                return "redirect:/admin/index";
            } catch (Exception e) { //发生异常说明token失效
                e.printStackTrace();
            }
            if (CookieUtils.get(request, "tokenInvalid") == null) {
                CookieUtils.set(response, "tokenInvalid", "请先登录", -1);
            }
            CookieUtils.delete(request, response, "token");
            HttpSession session = request.getSession();
            session.removeAttribute("user");
        }
        return "admin/login";
    }

    /*
    因为使用重定向的跳转方式的情况下，跳转到的地址无法获取 request 中的值。RedirecAtrributes 很好的解决了这个问题。

       1. redirectAttributes.addAttributie("param", value);
        这种方法相当于在重定向链接地址追加传递的参数。以上重定向的方法等同于 return "redirect:/hello?param=value" ，
        注意这种方法直接将传递的参数暴露在链接地址上，非常的不安全，慎用。

       2. redirectAttributes.addFlashAttributie("param", value);
        这种方法是隐藏了参数，链接地址上不直接暴露，但是能且只能在重定向的 “页面” 获取 param 参数值。
        其原理就是将设置的属性放到 session 中，session 中的属性在跳到页面后马上销毁。

        注意：这种方式在页面中可以正常获取，但是跳转目标是控制器方法的情况下，需要使用 @ModelAttribute 注解绑定参数后才能获取。
     */
    @PostMapping("/login")
    public String login(String username, String password, HttpServletRequest request, HttpServletResponse response, RedirectAttributes attributes) {
        User user = userService.selectOne(username);

        if (user == null) {
            attributes.addFlashAttribute("message", "用户不存在！");
            return "redirect:/admin";
        }

        if (!user.getPassword().equals(MD5Utils.string2MD5(password))) { //密码正确
            attributes.addFlashAttribute("message", "密码错误！");
            return "redirect:/admin";
        }

        //验证通过
        //生成token
        String token = JWTUtils.createToken(user);
        //将token存储在cookie中
        CookieUtils.set(response, "token", token, -1);
        user.setPassword(null);
        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        CookieUtils.delete(request, response, "tokenInvalid");

        return "redirect:/admin/index";
    }

    @GetMapping("/index")
    public String mainPage() {
        return "admin/index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.delete(request, response, "token");
        CookieUtils.delete(request, response, "tokenInvalid");
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        return "redirect:/admin";
    }

}