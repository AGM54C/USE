package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.PlanetMapper;
import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import com.example1.demo2.service.PlanetService;
import com.example1.demo2.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class IPlanetService implements PlanetService {
    @Autowired
    private PlanetMapper planetMapper;

    @Override
    public KnowledgePlanet findByTitle(String title) {
        return planetMapper.findByTitle(title);
    }

    @Override
    public void create(KnowledgePlanetDto planet) {
        planet.setPlanetId(generatePlanetId());
        KnowledgePlanet p = ConvertUtil.convertDtoToKnowledgePlanet(planet);
        planetMapper.add(p);
    }

    private String generatePlanetId() {
        LocalDate currentDate = LocalDate.now();
        String datePart = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "PLNT-" + datePart + "-" + randomPart;
    }
}
