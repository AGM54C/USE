package com.example1.demo2.service;

import com.example1.demo2.pojo.PlanetComment;
import com.example1.demo2.pojo.dto.PlanetCommentDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IPlanetCommentService {

    /**
     * 发布评论
     * @param commentDto 评论信息
     * @return 创建的评论信息
     */
    PlanetCommentDto publishComment(PlanetCommentDto commentDto);

    /**
     * 获取星球评论列表
     * @param planetId 星球ID
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选，用于判断点赞状态）
     * @return 评论列表
     */
    List<PlanetCommentDto> getCommentList(@NotNull String planetId, int page, int size, Integer userId);

    /**
     * 点赞/取消点赞
     * @param userId 用户ID
     * @param planetCommentId 评论ID
     * @return true-点赞，false-取消点赞
     */
    boolean toggleLike(Integer userId, Integer planetCommentId);

    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 用户ID（用于权限验证）
     */
    void deleteComment(Integer commentId, Integer userId);

    /**
     * 获取评论详情
     * @param commentId 评论ID
     * @param userId 当前用户ID（可选）
     * @return 评论详情
     */
    PlanetCommentDto getCommentDetail(Integer commentId, Integer userId);

    void handleReplyLogic(PlanetComment comment, PlanetCommentDto commentDto);

    @Transactional(readOnly = true)
    PlanetCommentDto getReplyInfo(Integer commentId, Integer userId);

    @Transactional
    PlanetCommentDto convertToDto(PlanetComment comment, Integer currentUserId);

    @Transactional
    void loadReplies(PlanetCommentDto comment, Integer currentUserId);

    @Transactional
    void deleteChildComments(Integer parentId);
}