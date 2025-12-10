package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.GalaxyCommentMapper;
import com.example1.demo2.mapper.GalaxyMapper;
import com.example1.demo2.mapper.PlanetMapper;
import com.example1.demo2.mapper.GalaxyAdministratorMapper;
import com.example1.demo2.mapper.NotificationMapper;
import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.dto.KnowledgeGalaxyDto;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import com.example1.demo2.service.IGalaxyService;
import com.example1.demo2.service.IPlanetService;
import com.example1.demo2.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GalaxyService implements IGalaxyService {
    @Autowired
    private GalaxyMapper galaxyMapper;

    @Autowired
    private PlanetMapper planetMapper;

    @Autowired
    private GalaxyCommentMapper galaxyCommentMapper;

    // 使用 @Lazy 避免循环依赖
    @Autowired
    @Lazy
    private IPlanetService planetService;

    private final Object createLock = new Object();

    @Override
    public KnowledgeGalaxy getKnowledgeGalaxyByName(String name) {
        return galaxyMapper.getKnowledgeGalaxyByName(name);
    }

    @Override
    public KnowledgeGalaxy getKnowledgeGalaxyById(Integer galaxyId) {
        return galaxyMapper.getKnowledgeGalaxyById(galaxyId);
    }

    @Override
    @Transactional
    public void createGalaxy(KnowledgeGalaxyDto galaxyDto) {
        synchronized (createLock) {
            // 设置星系ID
            galaxyDto.setGalaxyId(generateGalaxyId());

            // 如果没有提供邀请码，则自动生成一个
            if (galaxyDto.getInviteCode() == null || galaxyDto.getInviteCode().isEmpty()) {
                galaxyDto.setInviteCode(generateInviteCode());
            }

            // 将DTO转换为实体对象
            KnowledgeGalaxy galaxy = ConvertUtil.convertDtoToKnowledgeGalaxy(galaxyDto);

            // 调用mapper插入数据
            galaxyMapper.add(galaxy);

            // 将生成的galaxyId回写到DTO中（数据库自增ID）
            galaxyDto.setGalaxyId(Integer.valueOf(galaxy.getGalaxyId()));
        }
    }
    @Override
    @Transactional
    public Integer generateGalaxyId() {
        // 生成一个新的星系ID
        Integer maxId = galaxyMapper.getMaxGalaxyId();
        if (maxId == null) {
            return 1; // 如果没有记录，则从1开始
        } else {
            return maxId + 1; // 否则在最大ID基础上加1
        }
    }

    @Override
    @Transactional
    public void updateGalaxy(KnowledgeGalaxyDto galaxyDto) {
        // DTO转实体
        KnowledgeGalaxy galaxy = ConvertUtil.convertDtoToKnowledgeGalaxy(galaxyDto);

        // 调用mapper更新数据
        galaxyMapper.update(galaxy);
    }

    /**
     * 删除星系 - 实现完整的级联删除
     * 删除顺序很重要，需要先删除依赖数据，再删除主数据
     *
     * 级联删除顺序：
     * 1. 删除星系评论的点赞记录
     * 2. 删除星系的所有评论
     * 3. 删除星系管理员记录
     * 4. 处理星系下的星球（可以选择删除或解除关联）
     * 5. 删除与星系相关的通知
     * 6. 最后删除星系本身
     */
    @Override
    @Transactional
    public void deleteGalaxy(Integer galaxyId) {
        // 检查星系是否存在
        KnowledgeGalaxy galaxy = galaxyMapper.getKnowledgeGalaxyById(galaxyId);
        if (galaxy == null) {
            throw new RuntimeException("星系不存在");
        }

        // 1. 先删除星系下所有星球及其关联数据
        // 获取星系下的所有星球
        List<KnowledgePlanet> planets = galaxyMapper.getPlanetsByGalaxyId(galaxyId);
        if (planets != null && !planets.isEmpty()) {
            for (KnowledgePlanet planet : planets) {
                // 调用星球服务的级联删除方法
                planetService.deleteWithComments(planet.getPlanetId());
            }
        }

        // 2. 删除星系评论的点赞记录
        // 必须在删除评论之前执行，否则会因外键约束失败
        galaxyMapper.deleteGalaxyCommentLikesByGalaxyId(galaxyId);

        // 3. 删除星系的所有评论
        galaxyMapper.deleteGalaxyComments(galaxyId);

        // 4. 删除星系管理员记录
        galaxyMapper.deleteGalaxyAdminsByGalaxyId(galaxyId);

        // 5. 删除与星系相关的通知
        galaxyMapper.deleteNotificationsByGalaxyId(galaxyId);

        // 6. 最后删除星系本身
        galaxyMapper.delete(galaxyId);
    }

    /**
     * 删除星系（不删除星球，只解除关联）
     * 这是一个更温和的删除方式，保留星球但解除与星系的关联
     */
    @Transactional
    public void deleteGalaxyKeepPlanets(Integer galaxyId) {
        // 检查星系是否存在
        KnowledgeGalaxy galaxy = galaxyMapper.getKnowledgeGalaxyById(galaxyId);
        if (galaxy == null) {
            throw new RuntimeException("星系不存在");
        }

        // 1. 解除星球与星系的关联（将galaxy_id设为null）
        galaxyMapper.detachPlanetsFromGalaxy(galaxyId);

        // 2. 删除星系评论的点赞记录
        galaxyMapper.deleteGalaxyCommentLikesByGalaxyId(galaxyId);

        // 3. 删除星系的所有评论
        galaxyMapper.deleteGalaxyComments(galaxyId);

        // 4. 删除星系管理员记录
        galaxyMapper.deleteGalaxyAdminsByGalaxyId(galaxyId);

        // 5. 删除与星系相关的通知
        galaxyMapper.deleteNotificationsByGalaxyId(galaxyId);

        // 6. 最后删除星系本身
        galaxyMapper.delete(galaxyId);
    }

    @Override
    @Transactional
    public KnowledgePlanetDto addKnowledgePlanetToGalaxy(Integer galaxyId, String planetId) {
        // 检查星球是否存在
        KnowledgePlanet planet = planetMapper.findByPlanetId(planetId);
        if (planet == null) {
            return null;
        }

        // 检查星球是否已经属于其他星系
        if (planet.getGalaxyId() != null && !planet.getGalaxyId().isEmpty()) {
            throw new RuntimeException("星球已经属于其他星系");
        }

        // 将星球添加到星系中
        galaxyMapper.addPlanetToGalaxy(galaxyId, planetId);

        // 更新星系的星球数量
        galaxyMapper.incrementPlanetCount(galaxyId);

        // 重新查询更新后的星球信息
        KnowledgePlanet updatedPlanet = planetMapper.findByPlanetId(planetId);

        // 转换为DTO并返回
        return ConvertUtil.convertKnowledgePlanetToDto(updatedPlanet);
    }

    @Override
    public void removeKnowledgePlanetFromGalaxy(Integer galaxyId, String planetId) {
        // 从星系中移除星球（更新星球表中的galaxy_id字段为null）
        galaxyMapper.removePlanetFromGalaxy(galaxyId, planetId);
        galaxyMapper.decrementPlanetCount(galaxyId);
    }

    @Override
    public Integer getPermission(String galaxyId) {
        // 获取星系的权限
        return galaxyMapper.getGalaxyPermissionById(galaxyId);
    }

    @Override
    public void updateGalaxyName(Integer galaxyId, String newName) {
        // 更新星系名称
        KnowledgeGalaxy galaxy = galaxyMapper.getKnowledgeGalaxyById(galaxyId);
        if (galaxy != null) {
            galaxy.setName(newName);
            galaxyMapper.update(galaxy);
        } else {
            throw new IllegalArgumentException("Galaxy with ID " + galaxyId + " does not exist.");
        }
    }

    @Override
    public void updateGalaxyLabel(Integer galaxyId, String newLabel) {
        // 更新星系标签
        KnowledgeGalaxy galaxy = galaxyMapper.getKnowledgeGalaxyById(galaxyId);
        if (galaxy != null) {
            galaxy.setLabel(newLabel);
            galaxyMapper.update(galaxy);
        } else {
            throw new IllegalArgumentException("Galaxy with ID " + galaxyId + " does not exist.");
        }
    }

    @Override
    public void updateGalaxyPermission(Integer galaxyId, Integer newPermission) {
        // 更新星系权限
        KnowledgeGalaxy galaxy = galaxyMapper.getKnowledgeGalaxyById(galaxyId);
        if (galaxy != null) {
            galaxy.setPermission(newPermission);
            galaxyMapper.update(galaxy);
        } else {
            throw new IllegalArgumentException("Galaxy with ID " + galaxyId + " does not exist.");
        }
    }

    /**
     * 生成邀请码
     * 格式：8位大写字母和数字的组合
     */
    private String generateInviteCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }

    @Override
    public boolean isGalaxyOwner(Integer galaxyId, Integer userId) {
        KnowledgeGalaxy galaxy = galaxyMapper.getKnowledgeGalaxyById(galaxyId);
        return galaxy != null && galaxy.getUserId().equals(userId);
    }

    @Override
    public List<KnowledgePlanet> getPlanetsByGalaxyId(Integer galaxyId) {
        // 获取指定星系下的所有星球
        return galaxyMapper.getPlanetsByGalaxyId(galaxyId);
    }

    @Override
    public int getPlanetCountByGalaxyId(Integer galaxyId) {
        // 获取指定星系下的星球数量
        List<KnowledgePlanet> planets = galaxyMapper.getPlanetsByGalaxyId(galaxyId);
        return planets != null ? planets.size() : 0;
    }

    /**
     * 删除违规的星系评论 - 实现级联删除
     * 删除评论时需要同时删除：
     * 1. 评论的子评论
     * 2. 评论和子评论的点赞记录
     * 3. 相关通知
     */
    @Override
    @Transactional
    public boolean deleteGalaxyComment(Integer commentId, Integer currentUserId) {
        // 检查评论是否存在
        KnowledgeGalaxy galaxy = galaxyMapper.getGalaxyByCommentId(commentId);
        if (galaxy == null) {
            throw new RuntimeException("评论不存在或已被删除");
        }

        // 检查当前用户是否有权限删除评论
        if (!galaxyMapper.isUserGalaxyAdmin(galaxy.getGalaxyId(), currentUserId)
                && !galaxyMapper.isUserSystemAdmin(currentUserId)) {
            throw new RuntimeException("您没有权限删除此评论");
        }

        // 级联删除评论
        deleteGalaxyCommentCascade(commentId);

        return true;
    }

    /**
     * 级联删除星系评论的内部方法
     * 处理评论、子评论、点赞记录和通知的删除
     */
    @Transactional
    public void deleteGalaxyCommentCascade(Integer commentId) {
        // 1. 获取所有子评论ID
        List<Integer> childCommentIds = galaxyCommentMapper.getChildCommentIds(commentId);

        // 2. 递归删除子评论
        if (childCommentIds != null && !childCommentIds.isEmpty()) {
            for (Integer childId : childCommentIds) {
                deleteGalaxyCommentCascade(childId);
            }
        }

        // 3. 删除评论的点赞记录
        galaxyCommentMapper.deleteLikesByCommentId(commentId);

        // 4. 删除与评论相关的通知
        galaxyCommentMapper.deleteNotificationsByCommentId(commentId);

        // 5. 删除评论本身
        galaxyCommentMapper.deleteComment(commentId);
    }

    @Override
    public boolean isGalaxyAdmin(Integer galaxyId, Integer currentUserId) {
        // 检查用户是否是星系管理员
        return galaxyMapper.isUserGalaxyAdmin(galaxyId, currentUserId);
    }
}