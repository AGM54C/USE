package com.example1.demo2.service;

import com.example1.demo2.pojo.GalaxyMember;
import com.example1.demo2.pojo.dto.GalaxyMemberDto;
import java.util.List;
import java.util.Map;

public interface GalaxyMemberService {

    // ==================== 基础查询操作 ====================

    /**
     * 根据成员ID查询成员信息
     * @param memberId 成员ID
     * @return 成员DTO对象，如果不存在返回null
     */
    GalaxyMemberDto getMemberInfo(Integer memberId);

    /**
     * 根据星系ID和用户ID查询成员信息
     * @param galaxyId 星系ID
     * @param userId 用户ID
     * @return 成员实体对象，如果不存在返回null
     */
    GalaxyMember getMember(Integer galaxyId, Integer userId);

    /**
     * 获取星系成员列表（分页）
     * @param galaxyId 星系ID
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return 成员DTO列表
     */
    List<GalaxyMemberDto> getMemberList(Integer galaxyId, Integer page, Integer pageSize);

    /**
     * 获取星系成员总数
     * @param galaxyId 星系ID
     * @return 成员总数
     */
    Integer getMemberCount(Integer galaxyId);

    /**
     * 获取星系中特定角色的成员列表
     * @param galaxyId 星系ID
     * @param roleType 角色类型（0-普通成员，1-管理员，2-创建者）
     * @return 成员DTO列表
     */
    List<GalaxyMemberDto> getMembersByRole(Integer galaxyId, Integer roleType);

    // ==================== 成员管理操作 ====================

    /**
     * 添加新成员到星系
     * @param galaxyId 星系ID
     * @param userId 用户ID
     * @param roleType 角色类型（默认为0-普通成员）
     * @return 创建的成员实体
     */
    GalaxyMember addMember(Integer galaxyId, Integer userId, Integer roleType);

    /**
     * 批量邀请成员
     * @param galaxyId 星系ID
     * @param userIds 用户ID列表
     * @return 成功邀请的数量
     */
    Integer batchInvite(Integer galaxyId, List<Integer> userIds);

    /**
     * 移除成员
     * @param galaxyId 星系ID
     * @param userId 用户ID
     */
    void removeMember(Integer galaxyId, Integer userId);

    /**
     * 批量移除成员
     * @param galaxyId 星系ID
     * @param userIds 用户ID列表
     * @return 成功移除的数量
     */
    Integer batchRemove(Integer galaxyId, List<Integer> userIds);

    // ==================== 角色和权限管理 ====================

    /**
     * 更新成员角色
     * @param galaxyId 星系ID
     * @param userId 用户ID
     * @param newRoleType 新的角色类型
     */
    void updateMemberRole(Integer galaxyId, Integer userId, Integer newRoleType);

    /**
     * 更新成员权限
     * @param galaxyId 星系ID
     * @param userId 用户ID
     * @param permissions 权限JSON字符串
     */
    void updateMemberPermissions(Integer galaxyId, Integer userId, String permissions);

    /**
     * 获取成员权限列表
     * @param galaxyId 星系ID
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> getMemberPermissions(Integer galaxyId, Integer userId);

    /**
     * 检查成员是否拥有特定权限
     * @param galaxyId 星系ID
     * @param userId 用户ID
     * @param permission 权限名称
     * @return true如果拥有该权限，否则false
     */
    boolean hasPermission(Integer galaxyId, Integer userId, String permission);

    // ==================== 成员状态管理 ====================

    /**
     * 更新成员状态
     * @param galaxyId 星系ID
     * @param userId 用户ID
     * @param status 新状态（0-正常，1-禁用）
     */
    void updateMemberStatus(Integer galaxyId, Integer userId, Integer status);

    /**
     * 批量更新成员状态
     * @param galaxyId 星系ID
     * @param userIds 用户ID列表
     * @param status 新状态
     * @return 成功更新的数量
     */
    Integer batchUpdateStatus(Integer galaxyId, List<Integer> userIds, Integer status);

    // ==================== 统计和分析 ====================

    /**
     * 获取用户的成员统计信息
     * @param userId 用户ID
     * @return 统计信息Map，包含加入的星系数、创建的星系数、管理的星系数等
     */
    Map<String, Object> getMemberStats(Integer userId);

    /**
     * 获取星系的成员统计信息
     * @param galaxyId 星系ID
     * @return 统计信息Map，包含总人数、各角色人数、活跃人数等
     */
    Map<String, Object> getGalaxyMemberStats(Integer galaxyId);

    /**
     * 获取用户在所有星系中的成员信息
     * @param userId 用户ID
     * @return 成员DTO列表
     */
    List<GalaxyMemberDto> getUserMemberships(Integer userId);

    // ==================== 工具方法 ====================

    /**
     * 转换实体为DTO
     * @param member 成员实体
     * @return 成员DTO
     */
    GalaxyMemberDto convertToDto(GalaxyMember member);

    /**
     * 转换DTO为实体
     * @param memberDto 成员DTO
     * @return 成员实体
     */
    GalaxyMember convertToEntity(GalaxyMemberDto memberDto);

    /**
     * 检查用户是否为星系成员
     * @param galaxyId 星系ID
     * @param userId 用户ID
     * @return true如果是成员，否则false
     */
    boolean isMember(Integer galaxyId, Integer userId);

    /**
     * 检查用户是否为星系管理员或创建者
     * @param galaxyId 星系ID
     * @param userId 用户ID
     * @return true如果是管理员或创建者，否则false
     */
    boolean isAdminOrCreator(Integer galaxyId, Integer userId);

    // ==================== 清理和维护 ====================

    /**
     * 清理星系的所有成員（通常在删除星系时调用）
     * @param galaxyId 星系ID
     */
    void removeAllMembers(Integer galaxyId);

    /**
     * 清理用户的所有成员关系（通常在删除用户时调用）
     * @param userId 用户ID
     */
    void removeUserMemberships(Integer userId);

    /**
     * 更新成员最后活动时间
     * @param galaxyId 星系ID
     * @param userId 用户ID
     */
    void updateLastActivityTime(Integer galaxyId, Integer userId);
}