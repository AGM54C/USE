package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.CommentMapper;
import com.example1.demo2.mapper.PlanetMapper;
import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import com.example1.demo2.service.IPlanetService;
import com.example1.demo2.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class PlanetService implements IPlanetService {
    @Autowired
    private PlanetMapper planetMapper;

    @Autowired
    private CommentMapper commentMapper;

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

    @Override
    public void updateTitle(String planetId, String newTitle) {
        planetMapper.updateTitle(planetId, newTitle);
    }

    @Override
    public void updateCoverUrl(String planetId, String newCoverUrl) {
        planetMapper.updateCoverUrl(planetId, newCoverUrl);
    }

    @Override
    public void updateVisibility(String planetId, Integer newVisibility) {
        planetMapper.updateVisibility(planetId, newVisibility);
    }


    @Override
    @Transactional
    public void addCommentToPlanet(String planetId, Long commentId) {
        commentMapper.updatePlanetId(commentId, planetId);
    }

    @Override
    @Transactional
    public void removeCommentFromPlanet(String planetId, Long commentId) {
        commentMapper.updatePlanetId(commentId, null);
    }

    @Override
    @Transactional
    public void delete(String planetId) {
        planetMapper.deleteById(planetId);
    }

    @Override
    @Transactional
    public void deleteWithComments(String planetId) {
        // 先删除关联评论
        commentMapper.deleteByPlanetId(planetId);
        // 再删除星球
        planetMapper.deleteById(planetId);
    }

    @Override
    public List<KnowledgePlanet> searchPlanets(String keyword) {
        return planetMapper.searchPlanets("%" + keyword + "%");
    }

    @Override
    public void updatedescription(String planetId, String description) {
        planetMapper.updatedescription(planetId, description);
    }

    @Override
    public void updatedetail(String planetId, String contentDetail) {
        planetMapper.updatedetail(planetId, contentDetail);
    }

    @Override
    public void updatebrightness(String planetId, Integer brightness) {
        planetMapper.updatebrightness(planetId, brightness);
    }

    @Override
    public void updatefuelvalue(String planetId, Integer fuelValue) {
        planetMapper.updatefuelvalue(planetId, fuelValue);
    }

    @Override
    public KnowledgePlanet findByPlanetId(String planetId) {
        return planetMapper.findByPlanetId(planetId);
    }

    private String generatePlanetId() {
        LocalDate currentDate = LocalDate.now();
        String datePart = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "PLNT-" + datePart + "-" + randomPart;
    }
}
