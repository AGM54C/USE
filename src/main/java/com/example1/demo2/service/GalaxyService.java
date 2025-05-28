package com.example1.demo2.service;

import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.dto.KnowledgeGalaxyDto;

public interface GalaxyService {
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
    KnowledgeGalaxy getKnowledgeGalaxyById(String galaxyId);

    /**
     * 创建星系
     * @param galaxyDto 星系DTO对象，包含创建星系所需的信息
     */
    void createGalaxy(KnowledgeGalaxyDto galaxyDto);

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
     */
    void addKnowledgePlanetToGalaxy(String galaxyId, String planetId);

    /**
     * 从知识星系移除知识星球
     * @param galaxyId 星系ID
     * @param planetId 星球ID
     */
    void removeKnowledgePlanetFromGalaxy(String galaxyId, String planetId);

    /**
     * 添加评论到星系
     * @param galaxyId 星系ID
     * @param commentId 评论ID
     * @param
     * @return 返回找到的星系实体，如果未找到返回null
     */
    KnowledgeGalaxy getKnowledgeGalaxyByInviteCode(String inviteCode);
}