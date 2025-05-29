package com.example1.demo2.controller;

import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.IPlanetService;
import com.example1.demo2.util.ConvertUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@RestController //接口返回对象，转化为json文本
@RequestMapping("/planet")  //localhost:8081/planet/**
@Validated
public class PlanetController {
    @Autowired
    private IPlanetService planetService;

    //知识星球创建（必要参数为创建者ID，星球名，星球主题类型，返回值：星球id）
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
            return ResponseMessage.success(planet.getPlanetId());
        }
    }

    //查看星球信息（必要参数：星球名，返回值：星球信息）
    @GetMapping("/planetinfo")
    public ResponseMessage<KnowledgePlanetDto> planetinfo(@Valid @RequestBody KnowledgePlanetDto planet) {
        //根据星球名查询
        KnowledgePlanet p = planetService.findByTitle(planet.getTitle());
        if(p==null) {
            return ResponseMessage.error("星球不存在");
        }
        //转化为dto
        KnowledgePlanetDto dto =ConvertUtil.convertKnowledgePlanetToDto(p);
        return ResponseMessage.success(dto);
    }

    //更新星球信息(title,url,visibility,description等，返回值：成功或失败信息）
    @PutMapping("/update")
    public ResponseMessage update(@Valid @RequestBody KnowledgePlanetDto planet) {
        KnowledgePlanet p = planetService.findByTitle(planet.getTitle());
        if(p==null) {
            return ResponseMessage.error("星球不存在");
        }
        //需要根据id查找，这里添加
        planet.setPlanetId(p.getPlanetId());
        // 判断星球信息是否有变化
        boolean isChanged = false;
        if (!Objects.equals(planet.getTitle(),p.getTitle())) isChanged = true;
        if (!Objects.equals(planet.getCoverUrl(),p.getCoverUrl())) isChanged = true;
        if (!Objects.equals(planet.getVisibility(),p.getVisibility())) isChanged = true;
        if (!Objects.equals(planet.getDescription(),p.getDescription())) isChanged = true;
        if (!isChanged) {
            return ResponseMessage.success("没有需要修改的信息！");
        }

        //更新信息
        planetService.update(planet);
        return ResponseMessage.success("已更新星球信息！");
    }

    //注销星球（必要参数：星球id，返回值：星球id）
    @DeleteMapping("/delete")       //localhost:8081/planet/delete
    public ResponseMessage<String> delete(@Valid @RequestBody KnowledgePlanetDto planet) {
        //通过id删除星球
        KnowledgePlanet p = planetService.findByPlanetId(planet.getPlanetId());
        if(p==null) {
            //星球不存在
            return ResponseMessage.error("星球不存在或已被删除");
        }
        else{
            //删除星球
            planetService.delete(p.getPlanetId());
            return ResponseMessage.success(planet.getPlanetId());
        }
    }
}
