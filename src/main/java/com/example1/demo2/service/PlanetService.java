package com.example1.demo2.service;

import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;

public interface PlanetService {
    //根据星球名字查找
    KnowledgePlanet findByTitle(String title);

    void create(KnowledgePlanetDto planet);
}
