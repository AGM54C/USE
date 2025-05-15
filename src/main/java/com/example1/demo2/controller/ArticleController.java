package com.example1.demo2.controller;

import com.example1.demo2.pojo.dto.ResponseMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @GetMapping("/list")
    //使用拦截器获取登陆状态
    public ResponseMessage<String> list(@RequestHeader(name="Authorization") String token, HttpServletResponse response) {
        return ResponseMessage.success("文章数据...");
    }
}
