package com.example1.demo2.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 通知DTO
 * 用于前后端数据传输，包含了完整的通知信息
 */
public class NotificationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // 通知ID
    private Integer notificationId;

    // 接收者信息
    private Integer receiverId;
    private String receiverName;

    // 发送者信息
    @NotNull(groups = {Create.class}, message = "发送者ID不能为空")
    private Integer senderId;
    private String senderName;
    private String senderAvatar;  // 发送者头像，用于前端展示

    // 通知类型（1-星系评论回复 2-星系评论点赞 3-星系新评论 4-星球评论回复 5-星球评论点赞 6-星球新评论 7-系统通知 8-星系管理员任命通知）
    @NotNull(groups = {Create.class}, message = "通知类型不能为空")
    private Integer type;
    private String typeDesc;  // 类型描述，便于前端理解

    // 通知内容
    @NotNull(groups = {Create.class}, message = "通知标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100字符")
    private String title;

    @Size(max = 500, message = "内容长度不能超过500字符")
    private String content;

    // 关联目标（1-星系评论 2-星系 3-星球评论 4-星球 5-其他）
    private Integer targetType;
    private String targetId;  // 改为String类型，兼容不同ID格式
    private String targetTitle;  // 目标标题，如评论内容的前20字

    // 状态信息
    private Integer isRead;  // 0-未读 1-已读
    private Integer status;  // 0-正常 1-已删除

    // 时间信息
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date readTime;

    // 友好的时间显示（如"5分钟前"）
    private String createTimeAgo;

    // 额外数据
    private Map<String, Object> extraData;

    // 前端跳转链接
    private String jumpUrl;

    // 分组校验接口
    public interface Create {}
    public interface Update {}

    // 构造函数
    public NotificationDto() {}

    /**
     * 快速创建星系评论回复通知的构造函数
     */
    public static NotificationDto createGalaxyCommentReplyNotification(
            Integer receiverId, Integer senderId, String senderName,
            Integer commentId, String commentContent) {
        NotificationDto dto = new NotificationDto();
        dto.setReceiverId(receiverId);
        dto.setSenderId(senderId);
        dto.setSenderName(senderName);
        dto.setType(1);  // 星系评论回复
        dto.setTitle(senderName + " 回复了你的评论");
        dto.setContent(commentContent.length() > 50 ?
                commentContent.substring(0, 50) + "..." : commentContent);
        dto.setTargetType(1);  // 星系评论
        dto.setTargetId(String.valueOf(commentId));
        dto.setIsRead(0);
        dto.setStatus(0);
        return dto;
    }

    /**
     * 快速创建星系评论点赞通知的构造函数
     */
    public static NotificationDto createGalaxyLikeNotification(
            Integer receiverId, Integer senderId, String senderName,
            Integer commentId, String commentContent) {
        NotificationDto dto = new NotificationDto();
        dto.setReceiverId(receiverId);
        dto.setSenderId(senderId);
        dto.setSenderName(senderName);
        dto.setType(2);  // 星系评论点赞
        dto.setTitle(senderName + " 赞了你的评论");
        dto.setContent("你的评论收到了一个赞");
        dto.setTargetType(1);  // 星系评论
        dto.setTargetId(String.valueOf(commentId));
        dto.setTargetTitle(commentContent.length() > 30 ?
                commentContent.substring(0, 30) + "..." : commentContent);
        dto.setIsRead(0);
        dto.setStatus(0);
        return dto;
    }

    /**
     * 快速创建星球评论回复通知的构造函数
     */
    public static NotificationDto createPlanetCommentReplyNotification(
            Integer receiverId, Integer senderId, String senderName,
            Integer commentId, String commentContent) {
        NotificationDto dto = new NotificationDto();
        dto.setReceiverId(receiverId);
        dto.setSenderId(senderId);
        dto.setSenderName(senderName);
        dto.setType(4);  // 星球评论回复
        dto.setTitle(senderName + " 回复了你的评论");
        dto.setContent(commentContent.length() > 50 ?
                commentContent.substring(0, 50) + "..." : commentContent);
        dto.setTargetType(3);  // 星球评论
        dto.setTargetId(String.valueOf(commentId));
        dto.setIsRead(0);
        dto.setStatus(0);
        return dto;
    }

    /**
     * 快速创建星球评论点赞通知的构造函数
     */
    public static NotificationDto createPlanetLikeNotification(
            Integer receiverId, Integer senderId, String senderName,
            Integer commentId, String commentContent) {
        NotificationDto dto = new NotificationDto();
        dto.setReceiverId(receiverId);
        dto.setSenderId(senderId);
        dto.setSenderName(senderName);
        dto.setType(5);  // 星球评论点赞
        dto.setTitle(senderName + " 赞了你的评论");
        dto.setContent("你的评论收到了一个赞");
        dto.setTargetType(3);  // 星球评论
        dto.setTargetId(String.valueOf(commentId));
        dto.setTargetTitle(commentContent.length() > 30 ?
                commentContent.substring(0, 30) + "..." : commentContent);
        dto.setIsRead(0);
        dto.setStatus(0);
        return dto;
    }

    /**
     * 快速创建系统通知的构造函数
     */
    public static NotificationDto createSystemNotification(
            Integer receiverId, String title, String content) {
        NotificationDto dto = new NotificationDto();
        dto.setReceiverId(receiverId);
        dto.setType(7);  // 系统通知
        dto.setTitle(title);
        dto.setContent(content);
        dto.setTargetType(5);  // 其他
        dto.setIsRead(0);
        dto.setStatus(0);
        return dto;
    }

    /**
     * 快速创建星系管理员任命通知的构造函数
     */
    public static NotificationDto createGalaxyAdminNotification(
            Integer receiverId, Integer senderId, String senderName,
            Integer galaxyId, String galaxyName) {
        NotificationDto dto = new NotificationDto();
        dto.setReceiverId(receiverId);
        dto.setSenderId(senderId);
        dto.setSenderName(senderName);
        dto.setType(8);  // 星系管理员任命
        dto.setTitle(senderName + " 任命你为星系管理员");
        dto.setContent("你被任命为星系 " + galaxyName + " 的管理员");
        dto.setTargetType(2);  // 星系
        dto.setTargetId(String.valueOf(galaxyId));
        dto.setIsRead(0);
        dto.setStatus(0);
        return dto;
    }

    // Getters and Setters
    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
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

    public String getTargetTitle() {
        return targetTitle;
    }

    public void setTargetTitle(String targetTitle) {
        this.targetTitle = targetTitle;
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

    public String getCreateTimeAgo() {
        return createTimeAgo;
    }

    public void setCreateTimeAgo(String createTimeAgo) {
        this.createTimeAgo = createTimeAgo;
    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }
}