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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/planet/access")  //localhost:8081/planet/access/**
@Validated
public class PlanetAccessController {
    @Autowired
    private IPlanetAccessService planetAccessService;
    //定向飞行
    @GetMapping("/search")
    public ResponseMessage<KnowledgePlanetDto> search(@Valid @RequestBody KnowledgePlanetDto planet) {

        //根据星球名查询
        KnowledgePlanet p = planetAccessService.findByTitle(planet.getTitle());
        if(p==null) {
            return ResponseMessage.error("星球不存在");
        }
        //转化为dto
        KnowledgePlanetDto dto = ConvertUtil.convertKnowledgePlanetToDto(p);
        return ResponseMessage.success(dto);
    }




}
