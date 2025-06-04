package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.PlanetAccessMapper;
import com.example1.demo2.mapper.PlanetMapper;
import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.service.IPlanetAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanetAccessService implements IPlanetAccessService {

    @Autowired
    private PlanetAccessMapper planetAccessMapper;

    @Override
    public KnowledgePlanet findByTitle(String title) {
        return planetAccessMapper.findByTitle(title);
    }

    @Override
    public KnowledgePlanet findRandomPlanet() {
        return planetAccessMapper.findRandomPlanet();
    }

    @Override
    public List<KnowledgePlanet> getTop10Planets() {
        return planetAccessMapper.getTop10Planets();
    }
}
