package com.example1.demo2.service;

import com.example1.demo2.pojo.GalaxyAdministrator;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface IGalaxyAdminService {

    /**
     * 检查用户是否为星系管理员
     * @param galaxyId 星系ID
     * @param userId 用户ID
     * @return 是否为管理员
     */
    boolean isGalaxyAdmin(Integer galaxyId, Integer userId);

    /**
     * 添加星系管理员
     * @param galaxyId 星系ID
     * @param userId 被任命用户ID
     * @param appointedBy 任命者ID（必须是创建者）
     * @return 是否成功
     */
    boolean addGalaxyAdmin(Integer galaxyId, Integer userId, Integer appointedBy);

    /**
     * 撤销星系管理员
     * @param galaxyId 星系ID
     * @param userId 被撤销用户ID
     * @param operatorId 操作者ID（必须是创建者）
     * @return 是否成功
     */
    boolean revokeGalaxyAdmin(Integer galaxyId, Integer userId, Integer operatorId);

    /**
     * 获取星系的所有管理员
     * @param galaxyId 星系ID
     * @return 管理员列表
     */
    List<GalaxyAdministrator> getGalaxyAdmins(Integer galaxyId);

    /**
     * 获取用户管理的所有星系
     * @param userId 用户ID
     * @return 星系ID列表
     */
    List<Integer> getUserManagedGalaxies(Integer userId);

    /**
     * 检查用户是否有权限删除评论
     * @param galaxyId 星系ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean canDeleteComment(Integer galaxyId, Integer userId);

    void deleteComment(@NotNull Integer commentId);

    boolean autoBecomeAdmin(Integer galaxyId, String inviteCode,Integer userId);
}