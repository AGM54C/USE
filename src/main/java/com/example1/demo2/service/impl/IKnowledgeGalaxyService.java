package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.KnowledgeGalaxyMapper;
import com.example1.demo2.mapper.GalaxyMemberMapper;
import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.dto.knowledgeGalaxyDto;
import com.example1.demo2.service.KnowledgeGalaxyService;
import com.example1.demo2.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IKnowledgeGalaxyService implements KnowledgeGalaxyService {

    @Autowired
    private KnowledgeGalaxyMapper knowledgeGalaxyMapper;

    @Autowired
    private GalaxyMemberMapper galaxyMemberMapper;

    @Override
    public KnowledgeGalaxy findByName(String name) {
        return knowledgeGalaxyMapper.findByName(name);
    }

    @Override
    @Transactional
    public void create(knowledgeGalaxyDto galaxyDto) {
        KnowledgeGalaxy galaxy = ConvertUtil.convertDtoToGalaxy(galaxyDto);
        galaxy.setMemberCount(1);
        galaxy.setLastActivityTime(new Date());
        knowledgeGalaxyMapper.add(galaxy);

        // 创建者自动成为成员
        galaxyMemberMapper.addMember(galaxy.getGalaxyId(), galaxy.getCreatorId(), "创建者");


    }

    @Override
    public KnowledgeGalaxy findById(Integer galaxyId) {
        return knowledgeGalaxyMapper.findById(galaxyId);
    }

    @Override
    public boolean isMember(Integer galaxyId, Integer userId) {
        return galaxyMemberMapper.isMember(galaxyId, userId);
    }

    @Override
    public knowledgeGalaxyDto convertToDto(KnowledgeGalaxy galaxy) {
        return ConvertUtil.convertGalaxyToDto(galaxy);
    }

    @Override
    public void update(knowledgeGalaxyDto galaxyDto) {
        KnowledgeGalaxy galaxy = ConvertUtil.convertDtoToGalaxy(galaxyDto);
        knowledgeGalaxyMapper.update(galaxy);
    }

    @Override
    public List<knowledgeGalaxyDto> findByCreatorId(Integer creatorId) {
        List<KnowledgeGalaxy> galaxies = knowledgeGalaxyMapper.findByCreatorId(creatorId);
        return galaxies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<knowledgeGalaxyDto> findJoinedGalaxies(Integer userId) {
        List<KnowledgeGalaxy> galaxies = knowledgeGalaxyMapper.findJoinedGalaxies(userId);
        return galaxies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addMember(Integer galaxyId, Integer userId) {
        galaxyMemberMapper.addMember(galaxyId, userId, "普通成员");
        knowledgeGalaxyMapper.incrementMemberCount(galaxyId);
        updateLastActivityTime(galaxyId);
    }

    @Override
    @Transactional
    public void removeMember(Integer galaxyId, Integer userId) {
        galaxyMemberMapper.removeMember(galaxyId, userId);
        knowledgeGalaxyMapper.decrementMemberCount(galaxyId);
        updateLastActivityTime(galaxyId);
    }

    @Override
    @Transactional
    public void delete(Integer galaxyId) {
        galaxyMemberMapper.deleteByGalaxyId(galaxyId);
        knowledgeGalaxyMapper.delete(galaxyId);
    }

    @Override
    public List<knowledgeGalaxyDto> searchPublicGalaxies(String keyword) {
        List<KnowledgeGalaxy> galaxies = knowledgeGalaxyMapper.searchPublicGalaxies(keyword);
        return galaxies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateLastActivityTime(Integer galaxyId) {
        knowledgeGalaxyMapper.updateLastActivityTime(galaxyId, new Date());
    }
}