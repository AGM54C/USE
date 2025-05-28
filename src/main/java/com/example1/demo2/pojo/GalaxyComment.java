// 1. 首先修改GalaxyComment实体，添加一些必要的字段和方法
package com.example1.demo2.pojo;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tab_galaxy_comment")
public class GalaxyComment {
    /**
     * 星系评论ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "galaxy_comment_id")
    private Integer galaxyCommentId;

    /**
     * 创建者ID,关联用户表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    /**
     * 星系ID，关联星系表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "galaxy_id", referencedColumnName = "galaxy_id", nullable = false)
    private KnowledgeGalaxy knowledgeGalaxy;

    /**
     * 发布时间
     */
    @Column(name = "release_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime = new Date();

    /**
     * 评论内容
     */
    @Column(name = "content", columnDefinition = "text", length = 500, nullable = false)
    private String content;

    /**
     * 评论层级（1-3级，默认1）
     */
    @Column(name = "level", nullable = false, columnDefinition = "tinyint default 1")
    private Integer level = 1;

    /**
     * 父评论ID（0表示一级评论）
     */
    @Column(name = "parent_comment_id", columnDefinition = "int default 0")
    private Integer parentId = 0;

    /**
     * 被回复的用户ID（用于@功能，可为空）
     */
    @Column(name = "reply_to_user_id")
    private Integer replyToUserId;

    /**
     * 创建者角色（0 星系创建者 1 星系管理员 2普通成员）
     */
    @Column(name = "creator_role", nullable = false, columnDefinition = "tinyint default 2")
    private Integer creatorRole = 2;

    /**
     * 点赞数（默认0）
     */
    @Column(name = "like_count", columnDefinition = "int default 0")
    private Integer likeCount = 0;

    /**
     * 回复数（默认0）
     */
    @Column(name = "reply_count", columnDefinition = "int default 0")
    private Integer replyCount = 0;

    /**
     * 状态（0-正常，1-待审核，2-已删除，默认0）
     */
    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 0")
    private Integer status = 0;

    /**
     * 举报数
     */
    @Column(name = "report_count", columnDefinition = "int default 0")
    private Integer reportCount = 0;

    /**
     * 子评论列表（一对多自关联）
     */
    @OneToMany(mappedBy = "parentId", fetch = FetchType.LAZY)
    private List<GalaxyComment> replies;

    // JPA回调方法
    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createTime = now;
        this.updateTime = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = new Date();
    }

    // Getters and Setters
    public Integer getGalaxyCommentId() {
        return galaxyCommentId;
    }

    public void setGalaxyCommentId(Integer galaxyCommentId) {
        this.galaxyCommentId = galaxyCommentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public KnowledgeGalaxy getKnowledgeGalaxy() {
        return knowledgeGalaxy;
    }

    public void setKnowledgeGalaxy(KnowledgeGalaxy knowledgeGalaxy) {
        this.knowledgeGalaxy = knowledgeGalaxy;
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

    public Integer getReportCount() {
        return reportCount;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    public List<GalaxyComment> getReplies() {
        return replies;
    }

    public void setReplies(List<GalaxyComment> replies) {
        this.replies = replies;
    }

    @Override
    public String toString() {
        return "GalaxyComment{" +
                "galaxyCommentId=" + galaxyCommentId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", galaxyId=" + (knowledgeGalaxy != null ? knowledgeGalaxy.getGalaxyId() : null) +
                ", createTime=" + createTime +
                ", content='" + content + '\'' +
                ", level=" + level +
                ", parentId=" + parentId +
                ", creatorRole=" + creatorRole +
                ", likeCount=" + likeCount +
                ", replyCount=" + replyCount +
                ", status=" + status +
                '}';
    }
}