package com.example1.demo2.config;

import com.example1.demo2.interceptors.Logininterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // 精确匹配前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
