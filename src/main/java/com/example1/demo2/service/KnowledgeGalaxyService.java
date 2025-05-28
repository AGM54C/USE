package com.example1.demo2.service;

import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.knowledgeGalaxyDto;
import com.example1.demo2.pojo.KnowledgePlanet;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface KnowledgeGalaxyService {

    // 根据星系名称查询
    KnowledgeGalaxy findByName(String name);

    // 创建星系
    void create(knowledgeGalaxyDto galaxyDto);

    // 根据ID查询星系
    KnowledgeGalaxy findById(Integer galaxyId);

    // 检查用户是否为星系成员
    boolean isMember(Integer galaxyId, Integer userId);

    // 转换实体为DTO
    knowledgeGalaxyDto convertToDto(KnowledgeGalaxy galaxy);

    // 更新星系信息
    void update(knowledgeGalaxyDto galaxyDto);

    // 根据创建者ID查询星系列表
    List<knowledgeGalaxyDto> findByCreatorId(Integer creatorId);

    // 查询用户加入的星系列表
    List<knowledgeGalaxyDto> findJoinedGalaxies(Integer userId);

    // 添加成员
    void addMember(Integer galaxyId, Integer userId);

    @Transactional
    //添加成员
    void addMember(Integer galaxyId, User userId);

    //添加星球
    void addPlanet(Integer galaxyId, KnowledgePlanet knowledgePlanet);

    // 移除星球
    void removePlanet(Integer galaxyId, KnowledgePlanet knowledgePlanet);

    // 移除成员
    void removeMember(Integer galaxyId, User userId);

    @Transactional
    void removeMember(Integer galaxyId, Integer userId);

    // 删除星系
    void delete(Integer galaxyId);

    // 搜索公开星系
    List<knowledgeGalaxyDto> searchPublicGalaxies(String keyword);

    // 更新最近活动时间
    void updateLastActivityTime(Integer galaxyId);
}