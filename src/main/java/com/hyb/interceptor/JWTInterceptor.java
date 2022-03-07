package com.hyb.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.hyb.pojo.User;
import com.hyb.utils.CookieUtils;
import com.hyb.utils.JWTUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class JWTInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            //这里token通过cookie传输
            Cookie cookie = CookieUtils.get(request, "token");
            String token = cookie.getValue();
            JWTUtils.verifyToken(token);//验证token令牌
            //验证成功

            //服务器重启 session没了 但是token未失效  这是将用户信息重新设置到session中

            //拿到token payload中的username
            HttpSession session = request.getSession();

            String username = JWTUtils.parserToken(token, "username");
            String avatar = JWTUtils.parserToken(token, "avatar");
            String email = JWTUtils.parserToken(token, "email");
            String id = JWTUtils.parserToken(token, "id");
            Long userId = Long.parseLong(id);
            //放在session中
            User user = new User();
            user.setId(userId);
            user.setUsername(username);
            user.setAvatar(avatar);
            user.setEmail(email);
            session.setAttribute("user", user);
            return true;  //放行
        } catch (SignatureVerificationException e) {
            e.printStackTrace();
            System.out.println("无效签名");
        } catch (TokenExpiredException e) {
            e.printStackTrace();
            System.out.println("token过期");
        } catch (AlgorithmMismatchException e) {
            e.printStackTrace();
            System.out.println("token算法不一致");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("token无效");
        }

        CookieUtils.delete(request, response, "token");
        HttpSession session = request.getSession(); //清除用户信息
        session.removeAttribute("user");

        if (CookieUtils.get(request, "tokenInvalid") == null) {
            CookieUtils.set(response, "tokenInvalid", "请先登录", -1);
        }

        response.sendRedirect("/admin");
        return false;  //拦截
    }
}
