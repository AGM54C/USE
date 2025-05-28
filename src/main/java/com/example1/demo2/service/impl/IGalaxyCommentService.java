package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.GalaxyCommentMapper;
import com.example1.demo2.mapper.GalaxyMapper;
import com.example1.demo2.mapper.UserMapper;
import com.example1.demo2.pojo.GalaxyComment;
import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.GalaxyCommentDto;
import com.example1.demo2.service.GalaxyCommentService;
import com.example1.demo2.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IGalaxyCommentService implements GalaxyCommentService {

    @Autowired
    private GalaxyCommentMapper commentMapper;

    @Autowired
    private GalaxyMapper galaxyMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public GalaxyCommentDto publishComment(GalaxyCommentDto commentDto) {
        // 验证星系是否存在
        KnowledgeGalaxy galaxy = galaxyMapper.getKnowledgeGalaxyById(String.valueOf(commentDto.getGalaxyId()));
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
            // 验证父评论是否存在
            GalaxyComment parentComment = commentMapper.getCommentById(commentDto.getParentId());
            if (parentComment == null) {
                throw new RuntimeException("父评论不存在");
            }

            // 设置评论层级（父评论层级+1，最多3级）
            int level = Math.min(parentComment.getLevel() + 1, 3);
            comment.setLevel(level);
            comment.setParentId(commentDto.getParentId());

            // 如果是三级评论，确保回复的是二级评论
            if (level == 3 && parentComment.getLevel() == 3) {
                comment.setParentId(parentComment.getParentId());
            }

            // 设置被回复用户
            if (commentDto.getReplyToUserId() != null) {
                comment.setReplyToUserId(commentDto.getReplyToUserId());
            }

            // 更新父评论的回复数
            commentMapper.increaseReplyCount(comment.getParentId());
        } else {
            comment.setLevel(1);
            comment.setParentId(0);
        }

        // 获取用户在星系中的角色
        Integer userRole = commentMapper.getUserRoleInGalaxy(commentDto.getUserId(), commentDto.getGalaxyId());
        comment.setCreatorRole(userRole);

        // 插入评论
        commentMapper.insertComment(comment);

        // 转换为DTO并返回
        return convertToDto(comment, commentDto.getUserId());
    }

    @Override
    public List<GalaxyCommentDto> getCommentList(Integer galaxyId, int page, int size, Integer userId) {
        // 计算偏移量
        int offset = (page - 1) * size;

        // 获取一级评论
        List<GalaxyComment> firstLevelComments = commentMapper.getFirstLevelComments(galaxyId, offset, size);

        // 转换为DTO并加载回复
        return firstLevelComments.stream()
                .map(comment -> {
                    GalaxyCommentDto dto = convertToDto(comment, userId);
                    // 加载回复
                    loadReplies(dto, userId);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean toggleLike(Integer userId, Integer commentId) {
        // 验证评论是否存在
        GalaxyComment comment = commentMapper.getCommentById(commentId);
        if (comment == null || comment.getStatus() != 0) {
            throw new RuntimeException("评论不存在或已被删除");
        }

        // 检查是否已点赞
        boolean isLiked = commentMapper.isLiked(userId, commentId);

        if (isLiked) {
            // 取消点赞
            commentMapper.deleteLike(userId, commentId);
            commentMapper.decreaseLikeCount(commentId);
            return false;
        } else {
            // 点赞
            commentMapper.insertLike(userId, commentId);
            commentMapper.increaseLikeCount(commentId);
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

        // 权限验证：评论创建者、星系创建者、管理员可以删除
        boolean canDelete = comment.getUser().getUserId().equals(userId) ||
                comment.getKnowledgeGalaxy().getUserId().equals(userId) ||
                commentMapper.getUserRoleInGalaxy(userId, comment.getKnowledgeGalaxy().getGalaxyId()) == 1;

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
     * 将评论实体转换为DTO
     */
    private GalaxyCommentDto convertToDto(GalaxyComment comment, Integer currentUserId) {
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
     * 加载评论的所有回复
     */
    private void loadReplies(GalaxyCommentDto comment, Integer currentUserId) {
        List<GalaxyComment> replies = commentMapper.getRepliesByParentId(comment.getGalaxyCommentId());
        if (!replies.isEmpty()) {
            List<GalaxyCommentDto> replyDtos = replies.stream()
                    .map(reply -> {
                        GalaxyCommentDto dto = convertToDto(reply, currentUserId);
                        // 递归加载子回复（如果是二级评论）
                        if (dto.getLevel() < 3) {
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
     */
    private void deleteChildComments(Integer parentId) {
        List<GalaxyComment> children = commentMapper.getRepliesByParentId(parentId);
        for (GalaxyComment child : children) {
            commentMapper.updateCommentStatus(child.getGalaxyCommentId(), 2);
            deleteChildComments(child.getGalaxyCommentId());
        }
    }
}
