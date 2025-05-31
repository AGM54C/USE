package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.GalaxyMapper;
import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.dto.KnowledgeGalaxyDto;
import com.example1.demo2.service.IGalaxyService;
import com.example1.demo2.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GalaxyService implements IGalaxyService {
    @Autowired
    private GalaxyMapper galaxyMapper;

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

    @Override
    @Transactional
    public void deleteGalaxy(Integer galaxyId) {
        galaxyMapper.delete(galaxyId);
    }

    @Override
    @Transactional
    public void addKnowledgePlanetToGalaxy(Integer galaxyId, String planetId) {
        // 将星球添加到星系中（更新星球表中的galaxy_id字段）
        galaxyMapper.addPlanetToGalaxy(galaxyId, planetId);
    }

    @Override
    public void removeKnowledgePlanetFromGalaxy(Integer galaxyId, String planetId) {
        // 从星系中移除星球（更新星球表中的galaxy_id字段为null）
        galaxyMapper.removePlanetFromGalaxy(galaxyId, planetId);
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
}