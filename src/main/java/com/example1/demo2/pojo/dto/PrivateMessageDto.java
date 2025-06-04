package com.example1.demo2.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

public class PrivateMessageDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // 消息ID
    private Long messageId;

    // 发送者信息
    @NotNull(groups = {Send.class}, message = "发送者ID不能为空")
    private Integer senderId;
    private String senderNickname;
    private String senderAvatar;

    // 接收者信息
    @NotNull(groups = {Send.class}, message = "接收者ID不能为空")
    private Integer receiverId;
    private String receiverNickname;
    private String receiverAvatar;

    // 消息内容
    @NotBlank(groups = {Send.class}, message = "消息内容不能为空")
    @Size(max = 1000, message = "消息内容不能超过1000字符")
    private String content;

    // 消息类型：0-文本，1-图片，2-文件
    private Integer messageType = 0;

    // 附件URL
    private String attachmentUrl;

    // 是否已读
    private Integer isRead;

    // 状态：0-正常，1-已撤回，2-已删除
    private Integer status;

    // 发送时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    // 阅读时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date readTime;

    // 友好时间显示
    private String sendTimeAgo;

    // 是否可撤回（2分钟内）
    private Boolean canRecall;

    // 分组校验接口
    public interface Send {}

    // Getters and Setters
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverNickname() {
        return receiverNickname;
    }

    public void setReceiverNickname(String receiverNickname) {
        this.receiverNickname = receiverNickname;
    }

    public String getReceiverAvatar() {
        return receiverAvatar;
    }

    public void setReceiverAvatar(String receiverAvatar) {
        this.receiverAvatar = receiverAvatar;
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

    public String getSendTimeAgo() {
        return sendTimeAgo;
    }

    public void setSendTimeAgo(String sendTimeAgo) {
        this.sendTimeAgo = sendTimeAgo;
    }

    public Boolean getCanRecall() {
        return canRecall;
    }

    public void setCanRecall(Boolean canRecall) {
        this.canRecall = canRecall;
    }
}
