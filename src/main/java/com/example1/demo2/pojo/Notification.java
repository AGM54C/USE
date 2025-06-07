package com.example1.demo2.pojo;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tab_notification")
public class Notification {

    /**
     * 通知ID - 每条通知的唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    /**
     * 接收者 - 谁会收到这条通知
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id", nullable = false)
    private User receiver;

    /**
     * 发送者 - 谁触发了这条通知
     * 可能为空（系统通知的情况）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id")
    private User sender;

    /**
     * 通知类型
     * 1-星系评论回复 2-星系评论点赞 3-星系新评论 4-星球评论回复 5-星球评论点赞 6-星球新评论 7-系统通知 8-任命通知
     */
    @Column(name = "type", nullable = false)
    private Integer type;

    /**
     * 通知标题 - 简短描述
     */
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    /**
     * 通知内容 - 详细信息
     */
    @Column(name = "content", length = 500)
    private String content;

    /**
     * 关联的目标类型
     * 1-星系评论 2-星系 3-星球评论 4-星球 5-其他
     */
    @Column(name = "target_type")
    private Integer targetType;

    /**
     * 关联的目标ID
     * 根据targetType的不同，可能是评论ID、星系ID、星球ID等
     */
    @Column(name = "target_id")
    private String targetId;  // 改为String类型，以兼容星球的String ID

    /**
     * 是否已读
     * 0-未读 1-已读
     */
    @Column(name = "is_read", nullable = false, columnDefinition = "tinyint default 0")
    private Integer isRead = 0;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    /**
     * 阅读时间
     */
    @Column(name = "read_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date readTime;

    /**
     * 通知状态
     * 0-正常 1-已删除
     */
    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 0")
    private Integer status = 0;

    /**
     * 额外数据（JSON格式）
     * 用于存储一些额外的信息，如跳转链接等
     */
    @Column(name = "extra_data", columnDefinition = "text")
    private String extraData;

    // JPA回调方法
    @PrePersist
    protected void onCreate() {
        this.createTime = new Date();
    }

    // Getters and Setters
    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}

/**
 * 通知类型枚举
 * 使用枚举来管理通知类型，让代码更清晰易懂
 */
enum NotificationType {
    GALAXY_COMMENT_REPLY(1, "星系评论回复"),
    GALAXY_COMMENT_LIKE(2, "星系评论点赞"),
    GALAXY_NEW_COMMENT(3, "星系新评论"),
    PLANET_COMMENT_REPLY(4, "星球评论回复"),
    PLANET_COMMENT_LIKE(5, "星球评论点赞"),
    PLANET_NEW_COMMENT(6, "星球新评论"),
    SYSTEM(7, "系统通知"),
    APPOINTMENT(8, "任命通知");

    private final int code;
    private final String description;

    NotificationType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static NotificationType fromCode(int code) {
        for (NotificationType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}

/**
 * 目标类型枚举
 */
enum TargetType {
    GALAXY_COMMENT(1, "星系评论"),
    GALAXY(2, "星系"),
    PLANET_COMMENT(3, "星球评论"),
    PLANET(4, "星球"),
    OTHER(5, "其他");

    private final int code;
    private final String description;

    TargetType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TargetType fromCode(int code) {
        for (TargetType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}