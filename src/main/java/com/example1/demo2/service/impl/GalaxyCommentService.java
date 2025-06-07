package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.GalaxyCommentMapper;
import com.example1.demo2.mapper.GalaxyMapper;
import com.example1.demo2.mapper.UserMapper;
import com.example1.demo2.pojo.GalaxyComment;
import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.GalaxyCommentDto;
import com.example1.demo2.service.IGalaxyAdminService;
import com.example1.demo2.service.IGalaxyCommentService;
import com.example1.demo2.service.INotificationService;
import com.example1.demo2.service.ISystemAdminService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GalaxyCommentService implements IGalaxyCommentService {

    @Autowired
    private GalaxyCommentMapper commentMapper;

    @Autowired
    private GalaxyMapper galaxyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private IGalaxyAdminService galaxyAdminService;

    @Autowired
    private ISystemAdminService systemAdminService;

    @Override
    @Transactional
    public GalaxyCommentDto publishComment(GalaxyCommentDto commentDto) {
        // 验证星系是否存在
        KnowledgeGalaxy galaxy = galaxyMapper.getKnowledgeGalaxyById(Integer.valueOf(commentDto.getGalaxyId()));
        if (galaxy == null) {
            throw new RuntimeException("星系不存在");
        }

        // 验证用户是否存在
        User user = userMapper.findById(commentDto.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 创建评论实体
        GalaxyComment comment = new GalaxyComment();
        comment.setUser(user);
        comment.setKnowledgeGalaxy(galaxy);
        comment.setContent(commentDto.getContent());

        // 处理回复逻辑
        if (commentDto.getParentId() != null && commentDto.getParentId() > 0) {
            handleReplyLogic(comment, commentDto);
        } else {
            // 一级评论的默认设置
            comment.setLevel(1);
            comment.setParentId(0);
        }

        // 获取用户在星系中的角色
        Integer userRole = commentMapper.getUserRoleInGalaxy(commentDto.getUserId(), commentDto.getGalaxyId());
        comment.setCreatorRole(userRole);

        // 插入评论
        commentMapper.insertComment(comment);

        // 发送通知（如果是回复）
        if (comment.getReplyToUserId() != null) {
            sendReplyNotification(comment);
        }

        // 转换为DTO并返回
        return convertToDto(comment, commentDto.getUserId());
    }

    /**
     * 处理回复评论的逻辑
     * 这个方法专门处理评论层级关系和回复用户的设置
     */
    @Override
    @Transactional
    public void handleReplyLogic(GalaxyComment comment, GalaxyCommentDto commentDto) {
        // 验证父评论是否存在
        GalaxyComment parentComment = commentMapper.getCommentById(commentDto.getParentId());
        if (parentComment == null) {
            throw new RuntimeException("父评论不存在");
        }

        // 验证父评论是否已被删除
        if (parentComment.getStatus() != 0) {
            throw new RuntimeException("无法回复已删除的评论");
        }

        // 计算并设置评论层级
        int parentLevel = parentComment.getLevel();
        int newLevel = Math.min(parentLevel + 1, 3); // 最多支持三级
        comment.setLevel(newLevel);

        // 处理不同层级的回复逻辑
        if (newLevel == 2) {
            // 二级评论：直接回复一级评论
            comment.setParentId(commentDto.getParentId());
            comment.setReplyToUserId(parentComment.getUser().getUserId());
        } else if (newLevel == 3) {
            if (parentLevel == 2) {
                // 三级评论回复二级评论
                comment.setParentId(commentDto.getParentId());
                comment.setReplyToUserId(parentComment.getUser().getUserId());
            } else {
                // 三级评论回复三级评论，需要找到它们共同的二级父评论
                comment.setParentId(parentComment.getParentId());
                // 设置被回复用户（如果前端指定了，使用前端的；否则使用父评论的用户）
                if (commentDto.getReplyToUserId() != null) {
                    // 验证被回复用户是否存在
                    User replyToUser = userMapper.findById(commentDto.getReplyToUserId());
                    if (replyToUser == null) {
                        throw new RuntimeException("被回复的用户不存在");
                    }
                    comment.setReplyToUserId(commentDto.getReplyToUserId());
                } else {
                    comment.setReplyToUserId(parentComment.getUser().getUserId());
                }
            }
        }

        // 更新父评论的回复数
        // 注意：这里更新的是实际的 parentId，而不是传入的 parentId
        commentMapper.increaseReplyCount(comment.getParentId());
    }

    /**
     * 发送回复通知
     * 使用新的通知类型系统
     */
    private void sendReplyNotification(GalaxyComment comment) {
        try {
            // 发送评论回复通知（类型1：星系评论回复）
            if (comment.getReplyToUserId() != null) {
                // 如果是回复某个用户的评论
                notificationService.sendGalaxyCommentReplyNotification(
                        comment.getUser().getUserId(),      // 发送者
                        comment.getReplyToUserId(),         // 接收者
                        comment.getGalaxyCommentId(),       // 评论ID
                        comment.getContent()                // 评论内容
                );
            } else if (comment.getLevel() == 2) {
                // 如果是二级评论（回复一级评论）
                // 通知一级评论的作者
                GalaxyComment parentComment = commentMapper.getCommentById(comment.getParentId());
                if (parentComment != null && !parentComment.getUser().getUserId().equals(comment.getUser().getUserId())) {
                    notificationService.sendGalaxyCommentReplyNotification(
                            comment.getUser().getUserId(),
                            parentComment.getUser().getUserId(),
                            comment.getGalaxyCommentId(),
                            comment.getContent()
                    );
                }
            }

            // 如果评论者不是星系创建者，通知星系创建者（类型3：星系新评论）
            Integer galaxyOwnerId = comment.getKnowledgeGalaxy().getUserId();
            if (!comment.getUser().getUserId().equals(galaxyOwnerId)) {
                notificationService.sendGalaxyNewCommentNotification(
                        comment.getUser().getUserId(),
                        galaxyOwnerId,
                        comment.getKnowledgeGalaxy().getGalaxyId(),
                        comment.getKnowledgeGalaxy().getName()
                );
            }

        } catch (Exception e) {
            // 通知发送失败不应该影响评论的发布
            System.err.println("发送通知失败: " + e.getMessage());
        }
    }


    @Override
    public List<GalaxyCommentDto> getCommentList(@NotNull Integer galaxyId, int page, int size, Integer userId) {
        // 计算偏移量
        int offset = (page - 1) * size;

        // 获取一级评论
        List<GalaxyComment> firstLevelComments = commentMapper.getFirstLevelComments(galaxyId, offset, size);

        // 转换为DTO并加载回复
        return firstLevelComments.stream()
                .map(comment -> {
                    GalaxyCommentDto dto = convertToDto(comment, userId);
                    // 加载所有层级的回复
                    loadReplies(dto, userId);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean toggleLike(Integer userId, Integer galaxyCommentId) {
        // 验证评论是否存在
        GalaxyComment comment = commentMapper.getCommentById(galaxyCommentId);
        if (comment == null || comment.getStatus() != 0) {
            throw new RuntimeException("评论不存在或已被删除");
        }

        // 检查是否已点赞
        boolean isLiked = commentMapper.isLiked(userId, galaxyCommentId);

        if (isLiked) {
            // 取消点赞
            commentMapper.deleteLike(userId, galaxyCommentId);
            commentMapper.decreaseLikeCount(galaxyCommentId);
            return false;
        } else {
            // 点赞
            commentMapper.insertLike(userId, galaxyCommentId);
            commentMapper.increaseLikeCount(galaxyCommentId);

// 发送点赞通知（类型2：星系评论点赞）
            try {
                // 不给自己的评论点赞发通知
                if (!userId.equals(comment.getUser().getUserId())) {
                    notificationService.sendGalaxyCommentLikeNotification(
                            userId,                              // 点赞者
                            comment.getUser().getUserId(),       // 被点赞者
                            galaxyCommentId,                     // 评论ID
                            comment.getContent()                 // 评论内容
                    );
                }
            }
            catch (Exception e) {
                // 通知发送失败不影响点赞功能
                System.err.println("发送点赞通知失败: " + e.getMessage());
            }

            return true;
        }
    }

    @Override
    @Transactional
    public void deleteComment(Integer commentId, Integer userId) {
        // 获取评论信息
        GalaxyComment comment = commentMapper.getCommentById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        // 权限验证：评论创建者、星系创建者、星系管理员、系统管理员可以删除
        boolean canDelete = false;

        // 1. 评论创建者
        if (comment.getUser().getUserId().equals(userId)) {
            canDelete = true;
        }

        // 2. 星系创建者
        if (comment.getKnowledgeGalaxy().getUserId().equals(userId)) {
            canDelete = true;
        }

        // 3. 星系管理员
        if (galaxyAdminService.isGalaxyAdmin(comment.getKnowledgeGalaxy().getGalaxyId(), userId)) {
            canDelete = true;
        }

        // 4. 系统管理员
        if (systemAdminService.isSystemAdmin(userId)) {
            canDelete = true;
        }

        if (!canDelete) {
            throw new RuntimeException("无权删除此评论");
        }

        // 软删除评论
        commentMapper.updateCommentStatus(commentId, 2);

        // 如果有父评论，减少父评论的回复数
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            commentMapper.decreaseReplyCount(comment.getParentId());
        }

        // 递归软删除所有子评论
        deleteChildComments(commentId);

        // 如果是管理员删除，发送通知
        if (!comment.getUser().getUserId().equals(userId)) {
            notificationService.sendSystemNotification(
                    comment.getUser().getUserId(),
                    "评论被删除",
                    "您的评论被管理员删除"
            );
        }
    }

    @Override
    public GalaxyCommentDto getCommentDetail(Integer commentId, Integer userId) {
        // 获取评论
        GalaxyComment comment = commentMapper.getCommentById(commentId);
        if (comment == null || comment.getStatus() != 0) {
            throw new RuntimeException("评论不存在或已被删除");
        }

        // 转换为DTO
        GalaxyCommentDto dto = convertToDto(comment, userId);

        // 加载所有回复
        loadReplies(dto, userId);

        return dto;
    }

    /**
     * 获取可以回复的评论信息
     * 这个方法用于前端在用户点击"回复"时获取必要的信息
     */
    public GalaxyCommentDto getReplyInfo(Integer commentId, Integer userId) {
        GalaxyComment comment = commentMapper.getCommentById(commentId);
        if (comment == null || comment.getStatus() != 0) {
            throw new RuntimeException("评论不存在或已被删除");
        }

        GalaxyCommentDto dto = new GalaxyCommentDto();
        dto.setGalaxyCommentId(comment.getGalaxyCommentId());
        dto.setUserId(comment.getUser().getUserId());
        dto.setUsername(comment.getUser().getNickname());
        dto.setContent(comment.getContent());
        dto.setLevel(comment.getLevel());

        // 如果是三级评论，返回其二级父评论的ID
        if (comment.getLevel() == 3) {
            dto.setParentId(comment.getParentId());
        } else {
            dto.setParentId(comment.getGalaxyCommentId());
        }

        return dto;
    }

    /**
     * 将评论实体转换为DTO
     * 这个方法负责将数据库实体转换为前端需要的数据传输对象
     */
    @Override
    @Transactional
    public GalaxyCommentDto convertToDto(GalaxyComment comment, Integer currentUserId) {
        GalaxyCommentDto dto = new GalaxyCommentDto();
        dto.setGalaxyCommentId(comment.getGalaxyCommentId());
        dto.setUserId(comment.getUser().getUserId());
        dto.setUsername(comment.getUser().getNickname());
        dto.setGalaxyId(comment.getKnowledgeGalaxy().getGalaxyId());
        dto.setGalaxyName(comment.getKnowledgeGalaxy().getName());
        dto.setContent(comment.getContent());
        dto.setLevel(comment.getLevel());
        dto.setParentId(comment.getParentId());
        dto.setReplyToUserId(comment.getReplyToUserId());
        dto.setCreatorRole(comment.getCreatorRole());
        dto.setLikeCount(comment.getLikeCount());
        dto.setReplyCount(comment.getReplyCount());
        dto.setStatus(comment.getStatus());
        dto.setCreateTime(comment.getCreateTime());
        dto.setUpdateTime(comment.getUpdateTime());

        // 如果有被回复用户，加载用户名
        if (comment.getReplyToUserId() != null) {
            User replyToUser = userMapper.findById(comment.getReplyToUserId());
            if (replyToUser != null) {
                dto.setReplyToUsername(replyToUser.getNickname());
            }
        }

        // 判断当前用户是否已点赞
        if (currentUserId != null) {
            dto.setIsLiked(commentMapper.isLiked(currentUserId, comment.getGalaxyCommentId()));
        }

        return dto;
    }

    /**
     * 递归加载评论的所有回复
     * 这个方法会构建完整的评论树结构
     */
    @Override
    @Transactional
    public void loadReplies(GalaxyCommentDto comment, Integer currentUserId) {
        List<GalaxyComment> replies = commentMapper.getRepliesByParentId(comment.getGalaxyCommentId());
        if (!replies.isEmpty()) {
            List<GalaxyCommentDto> replyDtos = replies.stream()
                    .map(reply -> {
                        GalaxyCommentDto dto = convertToDto(reply, currentUserId);
                        // 只有二级评论才需要递归加载子回复
                        // 三级评论不会有子回复（根据我们的设计）
                        if (dto.getLevel() == 2) {
                            loadReplies(dto, currentUserId);
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());
            comment.setReplies(replyDtos);
        } else {
            comment.setReplies(new ArrayList<>());
        }
    }

    /**
     * 递归删除子评论
     * 当删除一个评论时，其所有子评论也应该被删除
     */
    @Override
    @Transactional
    public void deleteChildComments(Integer parentId) {
        List<GalaxyComment> children = commentMapper.getRepliesByParentId(parentId);
        for (GalaxyComment child : children) {
            commentMapper.updateCommentStatus(child.getGalaxyCommentId(), 2);
            // 继续递归删除更深层的子评论
            deleteChildComments(child.getGalaxyCommentId());
        }
    }

    /**
     * 删除违规评论
     * 验证身份为星系管理员或系统管理员方可删除
     */
    @Override
    public boolean deleteViolationComment(Integer commentId, Integer userId) {
        // 验证评论是否存在
        GalaxyComment comment = commentMapper.getCommentById(commentId);
        if (comment == null || comment.getStatus() != 0) {
            throw new RuntimeException("评论不存在或已被删除");
        }

        // 检查用户是否有权限删除
        boolean isAdmin = galaxyAdminService.isGalaxyAdmin(comment.getKnowledgeGalaxy().getGalaxyId(), userId);
        boolean isSystemAdmin = systemAdminService.isSystemAdmin(userId);

        if (!isAdmin && !isSystemAdmin) {
            throw new RuntimeException("无权删除此评论");
        }

        // 软删除评论
        commentMapper.updateCommentStatus(commentId, 2);

        // 如果有父评论，减少父评论的回复数
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            commentMapper.decreaseReplyCount(comment.getParentId());
        }

        // 递归软删除所有子评论
        deleteChildComments(commentId);

        return true;
    }
}