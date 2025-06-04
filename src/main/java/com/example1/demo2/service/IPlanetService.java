package com.example1.demo2.service;

import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import jakarta.validation.Valid;
import java.util.List;

public interface IPlanetService {
    //根据星球名字查找
    KnowledgePlanet findByTitle(String title);

    void create(KnowledgePlanetDto planet);

    void delete(String planetId);

    KnowledgePlanet findByPlanetId(String planetId);

    void updateTitle(String planetId, String contentTitle);

    void updateCoverUrl(String planetId, String coverUrl);

    void addCommentToPlanet(String planetId, Long commentId);

    void removeCommentFromPlanet(String planetId, Long commentId);

    void deleteWithComments(String planetId);

    List<KnowledgePlanet> searchPlanets(String keyword);

    void updatedescription(String planetId, String description);

    void updatedetail(String planetId, String contentDetail);

    void updatebrightness(String planetId, Integer brightness);

    void updatefuelvalue(String planetId, Integer fuelValue);

    KnowledgePlanet visitPlanet(String planetId, Integer userId);

    void publish(@Valid KnowledgePlanetDto planet);

    void updatevisibility(String planetId, Integer visibility);
}
