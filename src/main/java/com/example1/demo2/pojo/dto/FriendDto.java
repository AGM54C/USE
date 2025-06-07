package com.example1.demo2.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

public class FriendDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // 好友关系ID
    private Integer friendId;

    // 用户ID
    //@NotNull(groups = {SendRequest.class}, message = "用户ID不能为空")
    private Integer userId;

    // 好友用户ID
    @NotNull(groups = {SendRequest.class}, message = "好友ID不能为空")
    private Integer friendUserId;

    // 好友信息（展示用）
    private String friendNickname;
    private String friendAvatar;
    private String friendBio;

    // 状态：0-待确认，1-已接受，2-已拒绝，3-已删除
    private Integer status;
    private String statusDesc;

    // 来源：1-搜索添加，2-同星系成员，3-评论互动
    @NotNull(groups = {SendRequest.class}, message = "添加来源不能为空")
    @Min(value = 1, message = "来源类型无效")
    @Max(value = 3, message = "来源类型无效")
    private Integer source;
    private String sourceDesc;

    // 来源ID
    private String sourceId;

    // 申请备注
    @Size(max = 200, message = "申请备注不能超过200字符")
    private String requestMessage;

    // 时间信息
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date confirmTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastChatTime;

    // 未读消息数
    private Integer unreadCount;

    // 是否在线
    private Boolean isOnline;

    // 分组校验接口
    public interface SendRequest {}
    public interface AcceptRequest {}

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

    public String getFriendNickname() {
        return friendNickname;
    }

    public void setFriendNickname(String friendNickname) {
        this.friendNickname = friendNickname;
    }

    public String getFriendAvatar() {
        return friendAvatar;
    }

    public void setFriendAvatar(String friendAvatar) {
        this.friendAvatar = friendAvatar;
    }

    public String getFriendBio() {
        return friendBio;
    }

    public void setFriendBio(String friendBio) {
        this.friendBio = friendBio;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getSourceDesc() {
        return sourceDesc;
    }

    public void setSourceDesc(String sourceDesc) {
        this.sourceDesc = sourceDesc;
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

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }
}
