package com.example1.demo2.interceptors;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example1.demo2.pojo.User;
import com.example1.demo2.service.IUserService;
import com.example1.demo2.util.JWTUtil;
import com.example1.demo2.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Map;

//登录拦截器
@Component
public class Logininterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;
    @Override

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        //令牌认证
        String token=request.getHeader("Authorization");
        //验证Token
        try {
            Map<String,Object> claims= JWTUtil.ParseToken(token);

            Integer userId = (Integer) claims.get("userId");

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

