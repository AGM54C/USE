package com.example1.demo2.service;

import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import com.example1.demo2.pojo.dto.UserDto;
import jakarta.validation.Valid;
public interface IPlanetAccessService {

    //按标题查找知识星球
    KnowledgePlanet findByTitle(String title);
    //随机查找知识星球
    KnowledgePlanet findRandomPlanet();
}
