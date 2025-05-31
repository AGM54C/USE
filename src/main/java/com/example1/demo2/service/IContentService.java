package com.example1.demo2.service;

import jakarta.validation.Valid;

public interface IContentService {

    void update(@Valid PlanetContentDto content);

    PlanetContent findByTitle(String title);

    void create(@Valid PlanetContentDto content);

    void delete(Integer contentId);
}
