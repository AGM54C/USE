package com.example1.demo2.service;

import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.dto.KnowledgeGalaxyDto;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import jakarta.validation.constraints.Positive;

import java.util.List;

public interface IGalaxyService {
    /**
     * 按名字搜索星系
     * @param name 星系名称
     * @return 返回找到的星系实体，如果未找到返回null
     */
    KnowledgeGalaxy getKnowledgeGalaxyByName(String name);

    /**
     * 按ID查找星系
     * @param galaxyId 星系ID
     * @return 返回找到的星系实体，如果未找到返回null
     */
    KnowledgeGalaxy getKnowledgeGalaxyById(Integer galaxyId);

    /**
     * 创建星系
     * @param galaxyDto 星系DTO对象，包含创建星系所需的信息
     */
    void createGalaxy(KnowledgeGalaxyDto galaxyDto);

    Integer generateGalaxyId();

    /**
     * 更新星系信息
     * @param galaxyDto 星系DTO对象，包含需要更新的信息
     */
    void updateGalaxy(KnowledgeGalaxyDto galaxyDto);

    /**
     * 删除星系
     * @param galaxyId 要删除的星系ID
     */
    void deleteGalaxy(Integer galaxyId);

    /**
     * 添加知识星球到星系
     * @param galaxyId 星系ID
     * @param planetId 星球ID
     * @return 添加成功后的星球DTO对象，如果失败则返回null
     */
    KnowledgePlanetDto addKnowledgePlanetToGalaxy(Integer galaxyId, String planetId);

    /**
     * 从知识星系移除知识星球
     * @param galaxyId 星系ID
     * @param planetId 星球ID
     */
    void removeKnowledgePlanetFromGalaxy(Integer galaxyId, String planetId);

    /**
     * 获取权限
     * @return 权限值，0为私有，1为公开
     */
    Integer getPermission(String galaxyId);

    /**
     * 更新星系名字
     * @param galaxyId 星系ID
     * @param newName 新的星系名称
     */
    void updateGalaxyName(Integer galaxyId, String newName);

    /**
     * 更新星系标签
     * @param galaxyId 星系ID
     * @param newLabel 新的星系标签
     */
    void updateGalaxyLabel(Integer galaxyId, String newLabel);

    /**
     * 更新星系权限
     * @param galaxyId 星系ID
     * @param newPermission 新的权限值
     */
    void updateGalaxyPermission(Integer galaxyId, Integer newPermission);

    boolean isGalaxyOwner(Integer galaxyId, Integer userId);


    List<KnowledgePlanet> getPlanetsByGalaxyId(@Positive(message = "星系ID必须为正数") Integer galaxyId);
}