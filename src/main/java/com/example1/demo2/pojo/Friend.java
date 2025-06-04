package com.example1.demo2.pojo;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tab_friend")
public class Friend {

    /**
     * 好友关系ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Integer friendId;

    /**
     * 用户ID（发起好友请求的用户）
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * 好友ID（被添加的用户）
     */
    @Column(name = "friend_user_id", nullable = false)
    private Integer friendUserId;

    /**
     * 好友关系状态：0-待确认，1-已接受，2-已拒绝，3-已删除
     */
    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 0")
    private Integer status = 0;

    /**
     * 好友来源：1-搜索添加，2-同星系成员，3-评论互动
     */
    @Column(name = "source", nullable = false)
    private Integer source;

    /**
     * 来源ID（星系ID或评论ID，根据source类型）
     */
    @Column(name = "source_id", length = 50)
    private String sourceId;

    /**
     * 申请备注
     */
    @Column(name = "request_message", length = 200)
    private String requestMessage;

    /**
     * 创建时间（申请时间）
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    /**
     * 确认时间
     */
    @Column(name = "confirm_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmTime;

    /**
     * 最后聊天时间
     */
    @Column(name = "last_chat_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChatTime;

    // JPA回调方法
    @PrePersist
    protected void onCreate() {
        this.createTime = new Date();
    }

    // Getters and Setters
    public Integer getFriendId() {
        return friendId;
    }

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(Integer friendUserId) {
        this.friendUserId = friendUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public Date getLastChatTime() {
        return lastChatTime;
    }

    public void setLastChatTime(Date lastChatTime) {
        this.lastChatTime = lastChatTime;
    }
}