package com.example1.demo2.controller;

import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.PlanetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //接口返回对象，转化为json文本
@RequestMapping("/planet")  //localhost:8081/planet/**
@Validated
public class PlanetController {
    @Autowired
    private PlanetService planetService;

    //知识星球创建（必要参数为创建者ID，星球名，星球主题类型）
    @PostMapping("/create")         //localhost:8081/planet/create
    public ResponseMessage<String> create(@Valid @RequestBody KnowledgePlanetDto planet) {
        //查询星球
        KnowledgePlanet p = planetService.findByTitle(planet.getTitle());
        if(p!=null) {
            //星球名已经占用
            return ResponseMessage.error("星球名已被占用，请重新输入");
        }
        else{
            //没有占用
            planetService.create(planet);
            return ResponseMessage.success("星球"+planet.getTitle()+"成功创建");
        }
    }

}
