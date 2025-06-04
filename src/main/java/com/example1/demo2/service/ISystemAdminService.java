package com.example1.demo2.service;

import com.example1.demo2.pojo.SystemAdmin;
import java.util.List;

public interface ISystemAdminService {

    /**
     * 检查用户是否为系统管理员
     * @param userId 用户ID
     * @return 是否为管理员
     */
    boolean isSystemAdmin(Integer userId);

    /**
     * 添加系统管理员
     * @param userId 用户ID
     * @param permissions 权限JSON
     * @param operatorId 操作者ID
     * @return 是否成功
     */
    boolean addSystemAdmin(Integer userId, String permissions, Integer operatorId);

    /**
     * 删除违规评论（星系）
     * @param commentId 评论ID
     * @param adminId 管理员ID
     * @param reason 删除原因
     * @return 是否成功
     */
    boolean deleteGalaxyComment(Integer commentId, Integer adminId, String reason);

    /**
     * 删除违规评论（星球）
     * @param commentId 评论ID
     * @param adminId 管理员ID
     * @param reason 删除原因
     * @return 是否成功
     */
    boolean deletePlanetComment(Integer commentId, Integer adminId, String reason);

    /**
     * 封禁用户
     * @param userId 被封禁用户ID
     * @param adminId 管理员ID
     * @param reason 封禁原因
     * @param duration 封禁时长（天）
     * @return 是否成功
     */
    boolean banUser(Integer userId, Integer adminId, String reason, Integer duration);

    /**
     * 解封用户
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @return 是否成功
     */
    boolean unbanUser(Integer userId, Integer adminId);

    /**
     * 获取所有系统管理员
     * @return 管理员列表
     */
    List<SystemAdmin> getAllSystemAdmins();
}