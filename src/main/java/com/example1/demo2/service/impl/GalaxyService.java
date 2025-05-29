package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.GalaxyMapper;
import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.dto.KnowledgeGalaxyDto;
import com.example1.demo2.service.IGalaxyService;
import com.example1.demo2.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class GalaxyService implements IGalaxyService {
    @Autowired
    private GalaxyMapper galaxyMapper;

    @Override
    public KnowledgeGalaxy getKnowledgeGalaxyByName(String name) {
        return galaxyMapper.getKnowledgeGalaxyByName(name);
    }

    @Override
    public KnowledgeGalaxy getKnowledgeGalaxyById(String galaxyId) {
        return galaxyMapper.getKnowledgeGalaxyById(galaxyId);
    }

    @Override
    @Transactional
    public void createGalaxy(KnowledgeGalaxyDto galaxyDto) {
        // 设置星系ID格式（虽然在数据库中是自增整数，但DTO中保留了字符串格式的ID）
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
        galaxyDto.setGalaxyId(String.valueOf(galaxy.getGalaxyId()));
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
    public void addKnowledgePlanetToGalaxy(String galaxyId, String planetId) {
        // 将星球添加到星系中（更新星球表中的galaxy_id字段）
        galaxyMapper.addPlanetToGalaxy(galaxyId, planetId);
    }

    @Override
    public void removeKnowledgePlanetFromGalaxy(String galaxyId, String planetId) {
        // 从星系中移除星球（更新星球表中的galaxy_id字段为null）
        galaxyMapper.removePlanetFromGalaxy(galaxyId, planetId);
    }

    /**
     * 生成星系ID格式字符串
     * 格式：GLXY-YYYYMMDD-XXXX
     */
    private String generateGalaxyId() {
        LocalDate currentDate = LocalDate.now();
        String datePart = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "GLXY-" + datePart + "-" + randomPart;
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