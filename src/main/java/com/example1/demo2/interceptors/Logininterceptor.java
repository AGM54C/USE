package com.example1.demo2.interceptors;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example1.demo2.pojo.User;
import com.example1.demo2.service.UserService;
import com.example1.demo2.util.JWTUtil;
import com.example1.demo2.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Map;

//登录拦截器
@Component
public class Logininterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Override

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        // 确保拦截器放行 OPTIONS 方法
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization,Content-Type");
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        //令牌认证
        String token=request.getHeader("Authorization");
        //验证Token
        try {
            Map<String,Object> claims= JWTUtil.ParseToken(token);

            Integer userId = (Integer) claims.get("userId");
            Integer tokenVersion = (Integer) claims.get("tokenVersion"); // 从claims中获取

            // 查询数据库中的最新版本
            User u = userService.findById(userId);

            //验证版本是否一致
            if (u == null || !u.getTokenVersion().equals(tokenVersion)) {
             throw new JWTVerificationException("Token已过期，请重新登录");
           }
            //将业务数据存储到Threadlocal中，多线程运行
            ThreadLocalUtil.set(claims);

            //放行
            return true;
        } catch (Exception e) {
            //http响应码为401,不放行
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    //清空ThreadLocal数据
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,Exception ex) throws Exception {
        //清空，防止内存泄漏
        ThreadLocalUtil.remove();
    }
}

