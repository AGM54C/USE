package com.example1.demo2.pojo;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tab_planet_comment")
public class PlanetComment {

    /** 评论ID（自增主键） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    /**
     * 所属星球（多对一关联）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet_id", nullable = false)
    private KnowledgePlanet planet;


    /** 评论用户ID（必填） */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** 父评论ID（0表示一级评论） */
    @Column(name = "parent_id", columnDefinition = "bigint default 0")
    private Long parentId = 0L;

    /** 评论层级（1-3级，默认1） */
    @Column(name = "level", nullable = false, columnDefinition = "tinyint default 1")
    private Integer level = 1;

    /** 评论内容（必填） */
    @Column(name = "content", columnDefinition = "text", nullable = false)
    private String content;

    /** 点赞数（默认0） */
    @Column(name = "like_count", columnDefinition = "int default 0")
    private Integer likeCount = 0;

    /** 回复数（默认0） */
    @Column(name = "reply_count", columnDefinition = "int default 0")
    private Integer replyCount = 0;

    /** 状态（0-正常，1-待审核，2-已删除，默认0） */
    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 0")
    private Integer status = 0;

    /** 创建时间（自动生成） */
    @Column(name = "create_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /** 更新时间（自动更新） */
    @Column(name = "update_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    // JPA 回调方法：自动设置时间
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

    // Getter 和 Setter
    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public Date getUpdateTime() { return updateTime; }

    @Override
    public String toString() {
        return "PlanetComment{" +
                "commentId=" + commentId +
                ", planet=" + planet +
                ", userId=" + userId +
                ", parentId=" + parentId +
                ", level=" + level +
                ", content='" + content + '\'' +
                ", likeCount=" + likeCount +
                ", replyCount=" + replyCount +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public String getContent() {
        return content;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId() {
        return userId;
    }

    public KnowledgePlanet getPlanet() {
        return planet;
    }

    public void setPlanet(KnowledgePlanet planet) {
        this.planet = planet;
    }
}