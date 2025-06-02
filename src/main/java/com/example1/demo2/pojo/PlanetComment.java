// 1. 首先修改PlanetComment实体，添加一些必要的字段和方法
package com.example1.demo2.pojo;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tab_planet_comment")
public class PlanetComment {
    /**
     * 星球评论ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planet_comment_id")
    private Integer planetCommentId;

    /**
     * 创建者ID,关联用户表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    /**
     * 星球ID，关联星球表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet_id", referencedColumnName = "planet_id", nullable = false)
    private KnowledgePlanet knowledgePlanet;

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
    private List<PlanetComment> replies;

    /**
     * 是否已删除（逻辑删除标志0 未删除 1 已删除）
     */
    @Column(name = "is_deleted", nullable = false, columnDefinition = "tinyint default 0")
    private Integer isDeleted;

    /**
     * 删除原因
     */
    @Column(name = "delete_reason", length = 255)
    private String deleteReason;

    /**
     * 删除时间
     */
    @Column(name = "delete_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deleteTime;

    /**
     * 删除人ID
     */
    @Column(name = "deleted_by_user_id")
    private Integer deletedByUserId;

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
    public Integer getPlanetCommentId() {
        return planetCommentId;
    }

    public void setPlanetCommentId(Integer planetCommentId) {
        this.planetCommentId = planetCommentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public KnowledgePlanet getPlanet() {
        return knowledgePlanet;
    }

    public void setPlanet(KnowledgePlanet knowledgePlanet) {
        this.knowledgePlanet = knowledgePlanet;
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

    public List<PlanetComment> getReplies() {
        return replies;
    }

    public void setReplies(List<PlanetComment> replies) {
        this.replies = replies;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

    public void setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
    }

    public Date getDeletedTime() {
        return deleteTime;
    }

    public void setDeletedTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public Integer getDeletedBy() {
        return deletedByUserId;
    }

    public void setDeletedBy(Integer deletedByUserId) {
        this.deletedByUserId = deletedByUserId;
    }

    @Override
    public String toString() {
        return "PlanetComment{" +
                "planetCommentId=" + planetCommentId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", planetId=" + (knowledgePlanet != null ? knowledgePlanet.getPlanetId() : null) +
                ", createTime=" + createTime +
                ", content='" + content + '\'' +
                ", level=" + level +
                ", parentId=" + parentId +
                ", likeCount=" + likeCount +
                ", replyCount=" + replyCount +
                ", status=" + status +
                '}';
    }
}