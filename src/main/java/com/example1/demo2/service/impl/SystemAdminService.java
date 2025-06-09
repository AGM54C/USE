package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.*;
import com.example1.demo2.pojo.*;
import com.example1.demo2.service.ISystemAdminService;
import com.example1.demo2.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class SystemAdminService implements ISystemAdminService {

    private static final Logger logger = LoggerFactory.getLogger(SystemAdminService.class);

    @Autowired
    private SystemAdminMapper systemAdminMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GalaxyCommentMapper galaxyCommentMapper;

    @Autowired
    private PlanetCommentMapper planetCommentMapper;

    @Autowired
    private INotificationService notificationService;

    @Override
    public boolean isSystemAdmin(Integer userId) {
        return systemAdminMapper.isSystemAdmin(userId);
    }

    @Override
    @Transactional
    public boolean addSystemAdmin(Integer userId, String permissions, Integer operatorId) {
        // 验证操作者权限（必须是超级管理员）
        if (!isSystemAdmin(operatorId)) {
            throw new RuntimeException("无权限执行此操作");
        }

        // 检查用户是否已是管理员
        if (isSystemAdmin(userId)) {
            throw new RuntimeException("用户已是系统管理员");
        }

        // 创建管理员记录
        SystemAdmin admin = new SystemAdmin();
        admin.setUserId(userId);
        admin.setPermissions(permissions);
        admin.setStatus(0);

        systemAdminMapper.insertSystemAdmin(admin);

        // 发送通知
        notificationService.sendSystemNotification(
                userId,
                "系统管理员任命",
                "您已被任命为系统管理员"
        );

        logger.info("用户 {} 被 {} 任命为系统管理员", userId, operatorId);

        return true;
    }

    @Override
    @Transactional
    public boolean deleteGalaxyComment(Integer commentId, Integer adminId, String reason) {
        // 验证管理员权限
        if (!isSystemAdmin(adminId)) {
            throw new RuntimeException("无权限执行此操作");
        }

        // 获取评论信息
        GalaxyComment comment = galaxyCommentMapper.getCommentById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        // 硬删除评论
        galaxyCommentMapper.deleteComment(commentId);

        // 发送通知给评论作者
        notificationService.sendSystemNotification(
                comment.getUser().getUserId(),
                "评论被删除",
                "您的评论因违规被系统管理员删除，原因：" + reason
        );

        logger.info("系统管理员 {} 删除了星系评论 {}，原因：{}", adminId, commentId, reason);

        return true;
    }

    @Override
    @Transactional
    public boolean deletePlanetComment(Integer commentId, Integer adminId, String reason) {
        // 验证管理员权限
        if (!isSystemAdmin(adminId)) {
            throw new RuntimeException("无权限执行此操作");
        }

        // 获取评论信息
        PlanetComment comment = planetCommentMapper.getCommentById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        // 硬删除评论
        planetCommentMapper.deleteComment(commentId);

        // 发送通知给评论作者
        notificationService.sendSystemNotification(
                comment.getUser().getUserId(),
                "评论被删除",
                "您的评论因违规被系统管理员删除，原因：" + reason
        );

        logger.info("系统管理员 {} 删除了星球评论 {}，原因：{}", adminId, commentId, reason);

        return true;
    }

    @Override
    @Transactional
    public boolean banUser(Integer userId, Integer adminId, String reason, Integer duration) {
        // 验证管理员权限
        if (!isSystemAdmin(adminId)) {
            throw new RuntimeException("无权限执行此操作");
        }

        // 不能封禁系统管理员
        if (isSystemAdmin(userId)) {
            throw new RuntimeException("不能封禁系统管理员");
        }

        // 封禁用户
        userMapper.banUser(userId);

        // 发送通知
        notificationService.sendSystemNotification(
                userId,
                "账号被封禁",
                "您的账号因违规被封禁" + duration + "天，原因：" + reason
        );

        // TODO: 实现定时解封功能

        logger.info("系统管理员 {} 封禁了用户 {}，时长：{}天，原因：{}",
                adminId, userId, duration, reason);

        return true;
    }

    @Override
    @Transactional
    public boolean unbanUser(Integer userId, Integer adminId) {
        // 验证管理员权限
        if (!isSystemAdmin(adminId)) {
            throw new RuntimeException("无权限执行此操作");
        }

        // 解封用户
        userMapper.unbanUser(userId);

        // 发送通知
        notificationService.sendSystemNotification(
                userId,
                "账号已解封",
                "您的账号已被解封，可以正常使用"
        );

        logger.info("系统管理员 {} 解封了用户 {}", adminId, userId);

        return true;
    }

    @Override
    public List<SystemAdmin> getAllSystemAdmins() {
        return systemAdminMapper.getAllActiveAdmins();
    }

    @Override
    public boolean deleteSystemAdmin(Integer adminId, Integer operatorId) {
        // 验证操作者权限（必须是超级管理员）
        if (!isSystemAdmin(operatorId)) {
            throw new RuntimeException("无权限执行此操作");
        }

        // 检查是否为系统管理员
        if (!isSystemAdmin(adminId)) {
            throw new RuntimeException("用户不是系统管理员");
        }

        // 删除管理员记录
        systemAdminMapper.deleteSystemAdmin(adminId);

        // 发送通知
        notificationService.sendSystemNotification(
                adminId,
                "系统管理员权限被撤销",
                "您的系统管理员权限已被撤销"
        );

        logger.info("用户 {} 的系统管理员权限被 {} 撤销", adminId, operatorId);

        return true;
    }
}
