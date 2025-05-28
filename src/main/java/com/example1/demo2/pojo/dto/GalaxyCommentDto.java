package com.example1.demo2.pojo.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class GalaxyCommentDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // 评论ID（创建时不需要，更新/删除时需要）
    private Integer galaxyCommentId;

    // 创建者用户ID（从认证信息获取）
    @NotNull(groups = {Create.class}, message = "用户ID不能为空")
    private Integer userId;

    // 创建者用户名（用于展示）
    private String username;

    // 星系ID
    @NotNull(groups = {Create.class}, message = "星系ID不能为空")
    private Integer galaxyId;

    // 星系名称（用于展示）
    private String galaxyName;

    // 评论内容
    @NotBlank(groups = {Create.class}, message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500字")
    private String content;

    // 评论层级（1-3级）
    @Min(value = 1, message = "评论层级最小为1")
    @Max(value = 3, message = "评论层级最大为3")
    private Integer level = 1;

    // 父评论ID（0表示一级评论）
    private Integer parentId = 0;

    // 被回复的用户ID
    private Integer replyToUserId;

    // 被回复的用户名（用于展示）
    private String replyToUsername;

    // 创建者角色（0-星系创建者，1-管理员，2-普通成员）
    private Integer creatorRole = 2;

    // 点赞数
    private Integer likeCount = 0;

    // 回复数
    private Integer replyCount = 0;

    // 状态（0-正常，1-待审核，2-已删除）
    private Integer status = 0;

    // 是否已点赞（当前用户）
    private Boolean isLiked = false;

    // 创建时间
    private Date createTime;

    // 更新时间
    private Date updateTime;

    // 子评论列表
    private List<GalaxyCommentDto> replies;

    // 分组校验接口
    public interface Create {}
    public interface Update {}

    // Getters and Setters
    public Integer getGalaxyCommentId() {
        return galaxyCommentId;
    }

    public void setGalaxyCommentId(Integer galaxyCommentId) {
        this.galaxyCommentId = galaxyCommentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getGalaxyId() {
        return galaxyId;
    }

    public void setGalaxyId(Integer galaxyId) {
        this.galaxyId = galaxyId;
    }

    public String getGalaxyName() {
        return galaxyName;
    }

    public void setGalaxyName(String galaxyName) {
        this.galaxyName = galaxyName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(Integer replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public String getReplyToUsername() {
        return replyToUsername;
    }

    public void setReplyToUsername(String replyToUsername) {
        this.replyToUsername = replyToUsername;
    }

    public Integer getCreatorRole() {
        return creatorRole;
    }

    public void setCreatorRole(Integer creatorRole) {
        this.creatorRole = creatorRole;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<GalaxyCommentDto> getReplies() {
        return replies;
    }

    public void setReplies(List<GalaxyCommentDto> replies) {
        this.replies = replies;
    }
}
