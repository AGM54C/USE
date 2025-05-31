package com.example1.demo2.service;

import com.example1.demo2.pojo.dto.GalaxyCommentDto;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface IGalaxyCommentService {

    /**
     * 发布评论
     * @param commentDto 评论信息
     * @return 创建的评论信息
     */
    GalaxyCommentDto publishComment(GalaxyCommentDto commentDto);

    /**
     * 获取星系评论列表
     * @param galaxyId 星系ID
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选，用于判断点赞状态）
     * @return 评论列表
     */
    List<GalaxyCommentDto> getCommentList(@NotNull Integer galaxyId, int page, int size, Integer userId);

    /**
     * 点赞/取消点赞
     * @param userId 用户ID
     * @param galaxyCommentId 评论ID
     * @return true-点赞，false-取消点赞
     */
    boolean toggleLike(Integer userId, Integer galaxyCommentId);

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
    GalaxyCommentDto getCommentDetail(Integer commentId, Integer userId);
}
