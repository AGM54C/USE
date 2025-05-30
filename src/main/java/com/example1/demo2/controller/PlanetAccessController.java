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

@RestController
@RequestMapping("/planet/access")  //localhost:8081/planet/access/**
@Validated
public class PlanetAccessController {
    @Autowired
    private IPlanetAccessService planetAccessService;
    /**
     * 定向飞行到指定星球
     * 前端请求方式：GET
     * 请求URL：localhost:8081/planet/access/search?planet=星球名
     * 返回值：成功返回星球信息，失败返回错误信息
     */

    //定向飞行
    @GetMapping("/search")
    public ResponseMessage<KnowledgePlanetDto> search(@Valid @RequestParam  String planetName) {

        //根据星球名查询
        KnowledgePlanet p = planetAccessService.findByTitle(planetName);
        if(p==null) {
            return ResponseMessage.error("星球不存在");
        }
        //转化为dto
        KnowledgePlanetDto dto = ConvertUtil.convertKnowledgePlanetToDto(p);
        return ResponseMessage.success(dto);
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
        //转化为dto
        KnowledgePlanetDto dto = ConvertUtil.convertKnowledgePlanetToDto(p);
        return ResponseMessage.success(dto);
    }

}
