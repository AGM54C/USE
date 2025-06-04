package com.example1.demo2.controller;


import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import com.example1.demo2.pojo.dto.UserDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.IPlanetAccessService;
import com.example1.demo2.util.ConvertUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planet/access")  //localhost:8081/planet/access/**
@Validated
public class PlanetAccessController {
    @Autowired
    private IPlanetAccessService planetAccessService;
    /**
     * 定向飞行到指定星球
     * 前端请求方式：GET
     * 请求URL：localhost:8081/planet/access/search?planet=星球名(模糊查询)
     * 返回值：成功返回星球列表，失败返回错误信息
     */
    @GetMapping("/search")
    public ResponseMessage<List<KnowledgePlanetDto>> search(@RequestParam("planet") String planetName) {
        //查询星球
        List<KnowledgePlanet> planets = planetAccessService.findByTitle(planetName);
        if(planets.isEmpty()) {
            return ResponseMessage.error("没有找到相关星球");
        }
        //转化为dto列表
        List<KnowledgePlanetDto> dtoList = ConvertUtil.convertKnowledgePlanetListToDtoList(planets);
        return ResponseMessage.success(dtoList);
    }

    /**
     * 随机访问星球
     * 前端请求方式：GET
     * 请求URL：localhost:8081/planet/access/randomAccess
     * 返回值：成功返回星球信息，失败返回错误信息
     */
    @GetMapping("/randomAccess")
    public ResponseMessage<KnowledgePlanetDto> randomAccess() {
        //随机访问星球
        KnowledgePlanet p = planetAccessService.findRandomPlanet();
        if(p==null) {
            return ResponseMessage.error("没有可访问的星球");
        }
        if(p.getVisibility() != 1) {
            return ResponseMessage.error("星球不可见");
        }
        //转化为dto
        KnowledgePlanetDto dto = ConvertUtil.convertKnowledgePlanetToDto(p);
        return ResponseMessage.success(dto);
    }

    /**
     * 展示访问量前10且visibility为1的星球
     * 前端请求方式：GET
     * 请求URL：localhost:8081/planet/access/loadinghotplanets
     * 返回值：成功返回星球列表，失败返回错误信息
     */

    @GetMapping("/loadinghotplanets")
    public ResponseMessage<List<KnowledgePlanetDto>> loadinghotplanets() {
        //获取访问量前10的星球
        List<KnowledgePlanet> planets = planetAccessService.getTop10Planets();
        if(planets.isEmpty()) {
            return ResponseMessage.error("没有可访问的星球");
        }
        //转化为dto列表
        List<KnowledgePlanetDto> dtoList = ConvertUtil.convertKnowledgePlanetListToDtoList(planets);
        return ResponseMessage.success(dtoList);
    }

}
