package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.GalaxyMemberMapper;
import com.example1.demo2.mapper.KnowledgeGalaxyMapper;
import com.example1.demo2.mapper.UserMapper;
import com.example1.demo2.pojo.GalaxyMember;
import com.example1.demo2.pojo.dto.GalaxyMemberDto;
import com.example1.demo2.service.GalaxyMemberService;
import com.example1.demo2.util.ConvertUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IGalaxyMemberService implements GalaxyMemberService {

    @Autowired
    private GalaxyMemberMapper galaxyMemberMapper;

    @Autowired
    private KnowledgeGalaxyMapper knowledgeGalaxyMapper;

    @Autowired
    private UserMapper userMapper;

    // 用于处理JSON格式的权限数据
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== 基础查询操作实现 ====================

    @Override
    public GalaxyMemberDto getMemberInfo(Integer memberId) {
        GalaxyMember member = galaxyMemberMapper.findById(memberId);
        if (member == null) {
            return null;
        }
        return convertToDto(member);
    }

    @Override
    public GalaxyMember getMember(Integer galaxyId, Integer userId) {
        return galaxyMemberMapper.findByGalaxyIdAndUserId(galaxyId, userId);
    }

    @Override
    public List<GalaxyMemberDto> getMemberList(Integer galaxyId, Integer page, Integer pageSize) {
        // 计算分页偏移量
        int offset = (page - 1) * pageSize;
        List<GalaxyMember> members = galaxyMemberMapper.findByGalaxyIdWithPaging(galaxyId, offset, pageSize);

        // 转换为DTO并返回
        return members.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Integer getMemberCount(Integer galaxyId) {
        return galaxyMemberMapper.countByGalaxyId(galaxyId);
    }

    @Override
    public List<GalaxyMemberDto> getMembersByRole(Integer galaxyId, Integer roleType) {
        List<GalaxyMember> members = galaxyMemberMapper.findByGalaxyIdAndRoleType(galaxyId, roleType);
        return members.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ==================== 成员管理操作实现 ====================

    @Override
    @Transactional
    public GalaxyMember addMember(Integer galaxyId, Integer userId, Integer roleType) {
        // 创建新的成员记录
        GalaxyMember member = new GalaxyMember();
        member.setGalaxyId(galaxyId);
        member.setUserId(userId);
        member.setRoleType(roleType);
        member.setJoinTime(new Date());
        member.setStatus(0); // 默认状态为正常

        // 根据角色类型设置默认权限
        String defaultPermissions = getDefaultPermissionsByRole(roleType);
        member.setOperationPermissions(defaultPermissions);

        // 保存成员记录
        galaxyMemberMapper.insert(member);

        // 更新星系成员计数和活动时间
        knowledgeGalaxyMapper.incrementMemberCount(galaxyId);
        knowledgeGalaxyMapper.updateLastActivityTime(galaxyId, new Date());

        return member;
    }

    @Override
    @Transactional
    public Integer batchInvite(Integer galaxyId, List<Integer> userIds) {
        int successCount = 0;

        for (Integer userId : userIds) {
            try {
                // 检查用户是否已经是成员
                if (!isMember(galaxyId, userId)) {
                    // 检查用户是否存在
                    if (userMapper.findById(userId) != null) {
                        addMember(galaxyId, userId, 0); // 默认为普通成员
                        successCount++;
                    }
                }
            } catch (Exception e) {
                // 记录错误日志，但继续处理其他用户
                // 在实际应用中，这里应该使用日志框架记录详细错误
                System.err.println("Failed to invite user " + userId + ": " + e.getMessage());
            }
        }

        return successCount;
    }

    @Override
    @Transactional
    public void removeMember(Integer galaxyId, Integer userId) {
        // 删除成员记录
        galaxyMemberMapper.deleteByGalaxyIdAndUserId(galaxyId, userId);

        // 更新星系成员计数
        knowledgeGalaxyMapper.decrementMemberCount(galaxyId);
        knowledgeGalaxyMapper.updateLastActivityTime(galaxyId, new Date());
    }

    @Override
    @Transactional
    public Integer batchRemove(Integer galaxyId, List<Integer> userIds) {
        int successCount = 0;

        for (Integer userId : userIds) {
            try {
                if (isMember(galaxyId, userId)) {
                    removeMember(galaxyId, userId);
                    successCount++;
                }
            } catch (Exception e) {
                System.err.println("Failed to remove user " + userId + ": " + e.getMessage());
            }
        }

        return successCount;
    }

    // ==================== 角色和权限管理实现 ====================

    @Override
    @Transactional
    public void updateMemberRole(Integer galaxyId, Integer userId, Integer newRoleType) {
        GalaxyMember member = getMember(galaxyId, userId);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }

        // 更新角色
        member.setRoleType(newRoleType);

        // 更新权限为新角色的默认权限
        String defaultPermissions = getDefaultPermissionsByRole(newRoleType);
        member.setOperationPermissions(defaultPermissions);

        // 保存更新
        galaxyMemberMapper.updateRole(galaxyId, userId, newRoleType, defaultPermissions);

        // 更新星系活动时间
        knowledgeGalaxyMapper.updateLastActivityTime(galaxyId, new Date());
    }

    @Override
    @Transactional
    public void updateMemberPermissions(Integer galaxyId, Integer userId, String permissions) {
        // 验证权限格式
        try {
            objectMapper.readValue(permissions, new TypeReference<List<String>>(){});
        } catch (Exception e) {
            throw new RuntimeException("权限格式错误，必须是JSON数组");
        }

        // 更新权限
        galaxyMemberMapper.updatePermissions(galaxyId, userId, permissions);

        // 更新星系活动时间
        knowledgeGalaxyMapper.updateLastActivityTime(galaxyId, new Date());
    }

    @Override
    public List<String> getMemberPermissions(Integer galaxyId, Integer userId) {
        GalaxyMember member = getMember(galaxyId, userId);
        if (member == null || member.getOperationPermissions() == null) {
            return new ArrayList<>();
        }

        try {
            // 解析JSON格式的权限数据
            return objectMapper.readValue(member.getOperationPermissions(),
                    new TypeReference<List<String>>(){});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean hasPermission(Integer galaxyId, Integer userId, String permission) {
        List<String> permissions = getMemberPermissions(galaxyId, userId);
        return permissions.contains(permission);
    }

    // ==================== 成员状态管理实现 ====================

    @Override
    @Transactional
    public void updateMemberStatus(Integer galaxyId, Integer userId, Integer status) {
        galaxyMemberMapper.updateStatus(galaxyId, userId, status);

        // 更新星系活动时间
        knowledgeGalaxyMapper.updateLastActivityTime(galaxyId, new Date());
    }

    @Override
    @Transactional
    public Integer batchUpdateStatus(Integer galaxyId, List<Integer> userIds, Integer status) {
        int successCount = 0;

        for (Integer userId : userIds) {
            try {
                if (isMember(galaxyId, userId)) {
                    updateMemberStatus(galaxyId, userId, status);
                    successCount++;
                }
            } catch (Exception e) {
                System.err.println("Failed to update status for user " + userId + ": " + e.getMessage());
            }
        }

        return successCount;
    }

    // ==================== 统计和分析实现 ====================

    @Override
    public Map<String, Object> getMemberStats(Integer userId) {
        Map<String, Object> stats = new HashMap<>();

        // 获取用户加入的所有星系
        List<GalaxyMember> memberships = galaxyMemberMapper.findByUserId(userId);

        // 统计各种角色的数量
        long joinedCount = memberships.size();
        long adminCount = memberships.stream()
                .filter(m -> m.getRoleType() == 1)
                .count();
        long creatorCount = memberships.stream()
                .filter(m -> m.getRoleType() == 2)
                .count();

        // 统计活跃星系（最近30天有活动）
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        long activeCount = memberships.stream()
                .filter(m -> m.getLastActivityTime() != null &&
                        m.getLastActivityTime().after(thirtyDaysAgo))
                .count();

        stats.put("joinedGalaxies", joinedCount);
        stats.put("managedGalaxies", adminCount);
        stats.put("createdGalaxies", creatorCount);
        stats.put("activeGalaxies", activeCount);

        return stats;
    }

    @Override
    public Map<String, Object> getGalaxyMemberStats(Integer galaxyId) {
        Map<String, Object> stats = new HashMap<>();

        // 获取总成员数
        Integer totalCount = getMemberCount(galaxyId);
        stats.put("totalMembers", totalCount);

        // 获取各角色成员数
        Integer memberCount = galaxyMemberMapper.countByGalaxyIdAndRoleType(galaxyId, 0);
        Integer adminCount = galaxyMemberMapper.countByGalaxyIdAndRoleType(galaxyId, 1);
        Integer creatorCount = galaxyMemberMapper.countByGalaxyIdAndRoleType(galaxyId, 2);

        stats.put("normalMembers", memberCount);
        stats.put("admins", adminCount);
        stats.put("creators", creatorCount);

        // 获取活跃成员数（最近7天有活动）
        Date sevenDaysAgo = new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000);
        Integer activeCount = galaxyMemberMapper.countActiveMembers(galaxyId, sevenDaysAgo);
        stats.put("activeMembers", activeCount);

        // 计算活跃率
        Double activeRate = totalCount > 0 ? (activeCount * 100.0 / totalCount) : 0.0;
        stats.put("activeRate", String.format("%.2f%%", activeRate));

        return stats;
    }

    @Override
    public List<GalaxyMemberDto> getUserMemberships(Integer userId) {
        List<GalaxyMember> memberships = galaxyMemberMapper.findByUserId(userId);
        return memberships.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ==================== 工具方法实现 ====================

    @Override
    public GalaxyMemberDto convertToDto(GalaxyMember member) {
        return ConvertUtil.convertMemberToDto(member);
    }

    @Override
    public GalaxyMember convertToEntity(GalaxyMemberDto memberDto) {
        return ConvertUtil.convertDtoToMember(memberDto);
    }

    @Override
    public boolean isMember(Integer galaxyId, Integer userId) {
        return galaxyMemberMapper.isMember(galaxyId, userId);
    }

    @Override
    public boolean isAdminOrCreator(Integer galaxyId, Integer userId) {
        GalaxyMember member = getMember(galaxyId, userId);
        return member != null && member.getRoleType() >= 1;
    }

    // ==================== 清理和维护实现 ====================

    @Override
    @Transactional
    public void removeAllMembers(Integer galaxyId) {
        // 获取所有成员数量，用于更新计数
        Integer memberCount = getMemberCount(galaxyId);

        // 删除所有成员记录
        galaxyMemberMapper.deleteByGalaxyId(galaxyId);

        // 重置星系成员计数为0
        knowledgeGalaxyMapper.resetMemberCount(galaxyId);
    }

    @Override
    @Transactional
    public void removeUserMemberships(Integer userId) {
        // 获取用户所有的成员关系
        List<GalaxyMember> memberships = galaxyMemberMapper.findByUserId(userId);

        // 逐个删除并更新对应星系的成员计数
        for (GalaxyMember membership : memberships) {
            removeMember(membership.getGalaxyId(), userId);
        }
    }

    @Override
    public void updateLastActivityTime(Integer galaxyId, Integer userId) {
        galaxyMemberMapper.updateLastActivityTime(galaxyId, userId, new Date());
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 根据角色类型获取默认权限
     * 这是一个辅助方法，用于为不同角色设置初始权限
     */
    private String getDefaultPermissionsByRole(Integer roleType) {
        List<String> permissions = new ArrayList<>();

        switch (roleType) {
            case 0: // 普通成员
                permissions.add("VIEW_CONTENT");
                permissions.add("CREATE_COMMENT");
                break;
            case 1: // 管理员
                permissions.add("VIEW_CONTENT");
                permissions.add("CREATE_COMMENT");
                permissions.add("DELETE_COMMENT");
                permissions.add("INVITE_MEMBER");
                permissions.add("REMOVE_MEMBER");
                permissions.add("MANAGE_CONTENT");
                break;
            case 2: // 创建者
                permissions.add("VIEW_CONTENT");
                permissions.add("CREATE_COMMENT");
                permissions.add("DELETE_COMMENT");
                permissions.add("INVITE_MEMBER");
                permissions.add("REMOVE_MEMBER");
                permissions.add("MANAGE_CONTENT");
                permissions.add("ASSIGN_ADMIN");
                permissions.add("MODIFY_GALAXY");
                permissions.add("DELETE_GALAXY");
                break;
        }

        try {
            // 转换为JSON格式
            return objectMapper.writeValueAsString(permissions);
        } catch (Exception e) {
            return "[]"; // 返回空数组作为默认值
        }
    }
}