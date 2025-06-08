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
        try {
            // 获取星系信息
            KnowledgeGalaxy galaxy = galaxyMapper.getKnowledgeGalaxyById(galaxyId);
            if (galaxy == null) {
                throw new RuntimeException("星系不存在");
            }
            System.out.println("获取星系信息成功: " + galaxy.getName());

            // 验证任命者是否为星系创建者
            if (!galaxy.getUserId().equals(appointedBy)) {
                throw new RuntimeException("只有星系创建者可以任命管理员");
            }
            System.out.println("权限验证通过");

            // 检查是否已是管理员
            if (isGalaxyAdmin(galaxyId, userId)) {
                throw new RuntimeException("该用户已是星系管理员");
            }
            System.out.println("用户状态检查通过");

            // 创建管理员权限JSON
            String permissions = "[\"DELETE_COMMENT\", \"MANAGE_CONTENT\"]";

            // 添加管理员
            galaxyAdminMapper.insertGalaxyAdmin(
                    galaxyId, userId, 1, permissions, appointedBy
            );
            System.out.println("数据库插入完成");

            // 发送通知 - 加上try-catch来捕获可能的异常
            try {
                notificationService.sendSystemNotification(
                        userId,
                        "星系管理员任命",
                        "您已被任命为星系「" + galaxy.getName() + "」的管理员"
                );
                System.out.println("通知发送成功");
            } catch (Exception e) {
                System.err.println("通知发送失败: " + e.getMessage());
                // 可以选择是否让通知失败影响整个操作
                // throw e; // 如果要让通知失败导致整个操作失败
            }

            System.out.println("addGalaxyAdmin方法执行完成，返回true");
            return true;

        } catch (Exception e) {
            System.err.println("addGalaxyAdmin方法异常: " + e.getMessage());
            e.printStackTrace();
            throw e; // 重新抛出异常
        }
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

    @Override
    public void deleteComment(Integer commentId) {
        // 检查评论是否存在
        if (!galaxyAdminMapper.isCommentExists(commentId)) {
            throw new RuntimeException("评论不存在或已被删除");
        }

        // 删除评论
        galaxyAdminMapper.deleteComment(commentId);

        // 发送通知给评论作者
        Integer authorId = galaxyAdminMapper.getCommentAuthorId(commentId);
        if (authorId != null) {
            notificationService.sendSystemNotification(
                    authorId,
                    "评论删除通知",
                    "您的评论已被删除"
            );
        }
    }

    /**
     * 填写邀请码自动成为星系管理员
     * @param inviteCode 邀请码
     * @param userId 用户ID
     * @return 是否成功
     */
    @Override
    public boolean autoBecomeAdmin(String inviteCode, Integer userId) {
        // 检查星系是否存在
        KnowledgeGalaxy galaxy = galaxyMapper.getKnowledgeGalaxyByInviteCode(inviteCode);
        if (galaxy == null) {
            throw new RuntimeException("星系不存在");
        }

        // 检查邀请码是否正确
        if (!galaxy.getInviteCode().equals(inviteCode)) {
            throw new RuntimeException("邀请码不正确");
        }

        // 检查用户是否已是管理员
        if (isGalaxyAdmin(galaxy.getGalaxyId(), userId)) {
            throw new RuntimeException("您已是该星系的管理员");
        }


        // 添加管理员
        String permissions = "[\"DELETE_COMMENT\", \"MANAGE_CONTENT\"]";
        galaxyAdminMapper.insertGalaxyAdmin(
                galaxy.getGalaxyId(), userId, 1, permissions, galaxy.getUserId()
        );

        // 发送通知
        notificationService.sendSystemNotification(
                userId,
                "星系管理员任命",
                "您已通过邀请码成为星系「" + galaxy.getName() + "」的管理员"
        );

        return true;
    }
}