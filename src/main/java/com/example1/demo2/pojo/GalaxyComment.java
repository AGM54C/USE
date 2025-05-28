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
    @JoinColumn(name = "creator_id", referencedColumnName = "user_id",nullable = false)
    private User user;

    /**
     * 发布时间
     */
    @Column(name = "release_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    /**
     * 星系ID，关联星系表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "galaxy_id", referencedColumnName = "galaxy_id",nullable = false)
    private KnowledgeGalaxy knowledgeGalaxy;

    /**
     * 评论内容
     */
    @Column(name = "content", columnDefinition = "text", length=500,nullable = false)
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
    private Long parentId = 0L;

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

    //getters and setters
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public KnowledgeGalaxy getKnowledgeGalaxy() {
        return knowledgeGalaxy;
    }

    public void setKnowledgeGalaxy(KnowledgeGalaxy knowledgeGalaxy) {
        this.knowledgeGalaxy = knowledgeGalaxy;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    @Override
    public String toString() {
        return "GalaxyComment{" +
                "galaxyCommentId=" + galaxyCommentId +
                ", user=" + user +
                ", createTime=" + createTime +
                ", knowledgeGalaxy=" + knowledgeGalaxy +
                ", content='" + content + '\'' +
                ", level=" + level +
                ", parentId=" + parentId +
                ", creatorRole=" + creatorRole +
                ", likeCount=" + likeCount +
                ", replyCount=" + replyCount +
                ", status=" + status +
                ", reportCount=" + reportCount +
                '}';
    }
}
