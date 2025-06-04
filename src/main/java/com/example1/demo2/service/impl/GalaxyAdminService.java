package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.GalaxyAdministratorMapper;
import com.example1.demo2.mapper.GalaxyMapper;
import com.example1.demo2.mapper.UserMapper;
import com.example1.demo2.mapper.SystemAdminMapper;
import com.example1.demo2.pojo.GalaxyAdministrator;
import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.service.IGalaxyAdminService;
import com.example1.demo2.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GalaxyAdminService implements IGalaxyAdminService {

    @Autowired
    private GalaxyAdministratorMapper galaxyAdminMapper;

    @Autowired
    private GalaxyMapper galaxyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SystemAdminMapper systemAdminMapper;

    @Autowired
    private INotificationService notificationService;

    @Override
    public boolean isGalaxyAdmin(Integer galaxyId, Integer userId) {
        return galaxyAdminMapper.isGalaxyAdmin(galaxyId, userId);
    }

    @Override
    @Transactional
    public boolean addGalaxyAdmin(Integer galaxyId, Integer userId, Integer appointedBy) {
        // 获取星系信息
        KnowledgeGalaxy galaxy = galaxyMapper.getKnowledgeGalaxyById(galaxyId);
        if (galaxy == null) {
            throw new RuntimeException("星系不存在");
        }

        // 验证任命者是否为星系创建者
        if (!galaxy.getUserId().equals(appointedBy)) {
            throw new RuntimeException("只有星系创建者可以任命管理员");
        }

        // 检查是否已是管理员
        if (isGalaxyAdmin(galaxyId, userId)) {
            throw new RuntimeException("该用户已是星系管理员");
        }

        // 创建管理员权限JSON
        String permissions = "[\"DELETE_COMMENT\", \"MANAGE_CONTENT\"]";

        // 添加管理员
        galaxyAdminMapper.insertGalaxyAdmin(
                galaxyId, userId, 1, permissions, appointedBy
        );

        // 发送通知
        notificationService.sendSystemNotification(
                userId,
                "星系管理员任命",
                "您已被任命为星系「" + galaxy.getName() + "」的管理员"
        );

        return true;
    }

    @Override
    @Transactional
    public boolean revokeGalaxyAdmin(Integer galaxyId, Integer userId, Integer operatorId) {
        // 获取星系信息
        KnowledgeGalaxy galaxy = galaxyMapper.getKnowledgeGalaxyById(galaxyId);
        if (galaxy == null) {
            throw new RuntimeException("星系不存在");
        }

        // 验证操作者是否为星系创建者
        if (!galaxy.getUserId().equals(operatorId)) {
            throw new RuntimeException("只有星系创建者可以撤销管理员");
        }

        // 撤销管理员
        galaxyAdminMapper.revokeAdmin(galaxyId, userId);

        // 发送通知
        notificationService.sendSystemNotification(
                userId,
                "管理员权限撤销",
                "您在星系「" + galaxy.getName() + "」的管理员权限已被撤销"
        );

        return true;
    }

    @Override
    public List<GalaxyAdministrator> getGalaxyAdmins(Integer galaxyId) {
        return galaxyAdminMapper.getGalaxyAdmins(galaxyId);
    }

    @Override
    public List<Integer> getUserManagedGalaxies(Integer userId) {
        return galaxyAdminMapper.getUserManagedGalaxies(userId);
    }

    @Override
    public boolean canDeleteComment(Integer galaxyId, Integer userId) {
        // 获取星系信息
        KnowledgeGalaxy galaxy = galaxyMapper.getKnowledgeGalaxyById(galaxyId);
        if (galaxy == null) {
            return false;
        }

        // 检查是否为：1.星系创建者 2.星系管理员 3.系统管理员
        return galaxy.getUserId().equals(userId) ||
                isGalaxyAdmin(galaxyId, userId) ||
                systemAdminMapper.isSystemAdmin(userId);
    }
}