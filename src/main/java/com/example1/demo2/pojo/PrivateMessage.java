package com.example1.demo2.pojo;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tab_private_message")
public class PrivateMessage {

    /**
     * 消息ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    /**
     * 发送者ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id", nullable = false)
    private User sender;

    /**
     * 接收者ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id", nullable = false)
    private User receiver;

    /**
     * 消息内容
     */
    @Column(name = "content", columnDefinition = "text", nullable = false)
    private String content;

    /**
     * 消息类型：0-文本，1-图片，2-文件
     */
    @Column(name = "message_type", nullable = false, columnDefinition = "tinyint default 0")
    private Integer messageType = 0;

    /**
     * 附件URL（图片或文件）
     */
    @Column(name = "attachment_url", length = 255)
    private String attachmentUrl;

    /**
     * 是否已读：0-未读，1-已读
     */
    @Column(name = "is_read", nullable = false, columnDefinition = "tinyint default 0")
    private Integer isRead = 0;

    /**
     * 状态：0-正常，1-已撤回，2-已删除
     */
    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 0")
    private Integer status = 0;

    /**
     * 发送时间
     */
    @Column(name = "send_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendTime = new Date();

    /**
     * 阅读时间
     */
    @Column(name = "read_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date readTime;

    // JPA回调方法
    @PrePersist
    protected void onCreate() {
        this.sendTime = new Date();
    }

    // Getters and Setters
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }
}
