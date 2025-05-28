package com.example1.demo2.pojo;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "friend_relations")
public class FriendRelation {

    /**
     * 关系ID，主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relation_id")
    private Integer relationId;

    /**
     * 用户ID（发起方），外键关联用户表
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * 好友ID（接收方），外键关联用户表
     */
    @Column(name = "friend_id", nullable = false)
    private Integer friendId;

    /**
     * 关系状态：PENDING-待处理 ACCEPTED-已好友 BLOCKED-已屏蔽 DELETED-已删除
     */
    @Column(name = "relation_type", nullable = false, length = 20)
    private String relationType;

    /**
     * 建立方式(0 搜索 1知识星系成员 2评论添加)
     */
    @Column(name = "source", nullable = false, columnDefinition = "tinyint default 0")
    private Integer source;

    /**
     * 分组标签（如"同学"、"同事"）
     */
    @Column(name = "group_tag", length = 20)
    private String groupTag;

    /**
     * 亲密度分数（0-100）
     */
    @Column(name = "intimacy_score")
    private Integer intimacyScore;

    /**
     * 最后互动时间
     */
    @Column(name = "last_interaction_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastInteractionTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 处理时间（接受/拒绝/屏蔽等操作时间）
     */
    @Column(name = "resolve_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date resolveTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    /**
     * 操作日志（JSON格式存储变更历史）
     */
    @Column(name = "operation_log", columnDefinition = "json")
    private String operationLog;

    // JPA回调方法
    @PrePersist
    protected void onCreate() {
        this.createTime = this.updateTime = new Date();
        if (this.relationType == null) {
            this.relationType = "PENDING"; // 默认状态
        }
        if (this.intimacyScore == null) {
            this.intimacyScore = 0; // 默认亲密度
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = new Date();
    }

    // Getters and Setters
    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFriendId() {
        return friendId;
    }

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getGroupTag() {
        return groupTag;
    }

    public void setGroupTag(String groupTag) {
        this.groupTag = groupTag;
    }

    public Integer getIntimacyScore() {
        return intimacyScore;
    }

    public void setIntimacyScore(Integer intimacyScore) {
        this.intimacyScore = intimacyScore;
    }

    public Date getLastInteractionTime() {
        return lastInteractionTime;
    }

    public void setLastInteractionTime(Date lastInteractionTime) {
        this.lastInteractionTime = lastInteractionTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getResolveTime() {
        return resolveTime;
    }

    public void setResolveTime(Date resolveTime) {
        this.resolveTime = resolveTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getOperationLog() {
        return operationLog;
    }

    public void setOperationLog(String operationLog) {
        this.operationLog = operationLog;
    }

    @Override
    public String toString() {
        return "FriendRelation{" +
                "relationId=" + relationId +
                ", userId=" + userId +
                ", friendId=" + friendId +
                ", relationType='" + relationType + '\'' +
                ", source='" + source + '\'' +
                ", groupTag='" + groupTag + '\'' +
                ", intimacyScore=" + intimacyScore +
                ", lastInteractionTime=" + lastInteractionTime +
                ", createTime=" + createTime +
                ", resolveTime=" + resolveTime +
                ", updateTime=" + updateTime +
                ", operationLog='" + operationLog + '\'' +
                '}';
    }
}