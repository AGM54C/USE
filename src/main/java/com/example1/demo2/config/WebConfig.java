package com.example1.demo2.config;

import com.example1.demo2.interceptors.Logininterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//拦截器，用于验证用户登录状态
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private Logininterceptor logininterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        //登录注册不拦截
        registry.addInterceptor(logininterceptor).excludePathPatterns("/user/register","/user/login");
    }
}
