package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.GalaxyMapper;
import com.example1.demo2.mapper.PlanetCommentMapper;
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
    private PlanetCommentMapper commentMapper;

    @Autowired
    private GalaxyMapper galaxyMapper;

    @Autowired
    private SystemAdminService systemAdminService;

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
    @Transactional
    public void addCommentToPlanet(String planetId, Long commentId) {
        commentMapper.updatePlanetId(commentId, planetId);
    }

    @Override
    @Transactional
    public void removeCommentFromPlanet(String planetId, Long commentId) {
        commentMapper.updatePlanetId(commentId, null);
    }

    /**
     * 简单删除星球（不处理关联数据）
     * 注意：此方法可能因为外键约束而失败，建议使用 deleteWithComments 方法
     */
    @Override
    @Transactional
    public void delete(String planetId) {
        // 直接调用带级联删除的方法
        deleteWithComments(planetId);
    }

    /**
     * 删除星球及其所有关联数据 - 实现完整的级联删除
     *
     * 级联删除顺序：
     * 1. 删除星球评论的点赞记录
     * 2. 删除星球的所有评论
     * 3. 删除与星球相关的通知
     * 4. 更新所属星系的星球计数（如果属于某个星系）
     * 5. 最后删除星球本身
     */
    @Override
    @Transactional
    public void deleteWithComments(String planetId) {
        // 检查星球是否存在
        KnowledgePlanet planet = planetMapper.findByPlanetId(planetId);
        if (planet == null) {
            // 星球不存在，直接返回（幂等性设计）
            return;
        }

        // 获取星球所属的星系ID（用于后续更新星系的星球计数）
        String galaxyIdStr = planet.getGalaxyId();
        Integer galaxyId = null;
        if (galaxyIdStr != null && !galaxyIdStr.isEmpty()) {
            try {
                galaxyId = Integer.parseInt(galaxyIdStr);
            } catch (NumberFormatException e) {
                // galaxyId 格式不正确，忽略
            }
        }

        // 1. 删除星球所有评论的点赞记录
        // 必须在删除评论之前执行，否则会因外键约束失败
        planetMapper.deletePlanetCommentLikesByPlanetId(planetId);

        // 2. 删除星球的所有评论（包括子评论）
        // 由于评论有自引用关系（子评论引用父评论），
        // 直接删除所有该星球的评论即可
        commentMapper.deleteByPlanetId(planetId);

        // 3. 删除与星球相关的通知
        planetMapper.deleteNotificationsByPlanetId(planetId);

        // 4. 删除星球本身
        planetMapper.deleteById(planetId);

        // 5. 更新所属星系的星球计数
        if (galaxyId != null) {
            galaxyMapper.decrementPlanetCount(galaxyId);
        }
    }

    /**
     * 删除星球评论 - 实现级联删除
     * 删除评论时需要同时删除：
     * 1. 评论的子评论
     * 2. 评论和子评论的点赞记录
     * 3. 相关通知
     */
    @Transactional
    public void deletePlanetCommentCascade(Integer commentId) {
        // 1. 获取所有子评论ID
        List<Integer> childCommentIds = commentMapper.getChildCommentIds(commentId);

        // 2. 递归删除子评论
        if (childCommentIds != null && !childCommentIds.isEmpty()) {
            for (Integer childId : childCommentIds) {
                deletePlanetCommentCascade(childId);
            }
        }

        // 3. 删除评论的点赞记录
        commentMapper.deleteLikesByCommentId(commentId);

        // 4. 删除与评论相关的通知
        commentMapper.deleteNotificationsByCommentId(commentId);

        // 5. 删除评论本身
        commentMapper.deleteComment(commentId);
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
    public void publish(KnowledgePlanetDto planet) {
        planetMapper.publish(planet.getPlanetId());
    }

    @Override
    public void updatevisibility(String planetId, Integer visibility) {
        planetMapper.updatevisibility(planetId, visibility);
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

    @Override
    public KnowledgePlanet visitPlanet(String planetId, Integer userId) {
        // 1. 校验星球存在
        KnowledgePlanet planet = planetMapper.findByPlanetId(planetId);
        if (planet == null) {
            throw new IllegalArgumentException("星球不存在");
        }

        // 2. 校验可见性
        Integer visibility = planet.getVisibility();
        boolean isCreator = userId != null && userId.equals(planet.getUserId());
        boolean isSystemAdmin = userId != null && systemAdminService.isSystemAdmin(userId);

        if (visibility == 0) { // 私有星球
            if (!isCreator && !isSystemAdmin) {
                throw new IllegalArgumentException("星球为私有，无访问权限");
            }
        }

        // 3. 处理访问量统计（系统管理员访问不计入统计）
        if (!isSystemAdmin) {
            planetMapper.updatevisitCount(planetId);
        }

        // 4. 返回更新后的星球信息
        return planetMapper.findByPlanetId(planetId);
    }

}