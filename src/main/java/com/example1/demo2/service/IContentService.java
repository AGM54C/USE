package com.example1.demo2.service;

import com.example1.demo2.pojo.PlanetContent;
import com.example1.demo2.pojo.dto.PlanetContentDto;
import jakarta.validation.Valid;

public interface IContentService {

    void update(@Valid PlanetContentDto content);

    PlanetContent findByTitle(String title);

    void create(@Valid PlanetContentDto content);

    void delete(Integer contentId);
}
