package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.PrivateMessageMapper;
import com.example1.demo2.mapper.FriendMapper;
import com.example1.demo2.mapper.UserMapper;
import com.example1.demo2.pojo.PrivateMessage;
import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.PrivateMessageDto;
import com.example1.demo2.service.IPrivateMessageService;
import com.example1.demo2.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrivateMessageService implements IPrivateMessageService {

    @Autowired
    private PrivateMessageMapper messageMapper;

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private INotificationService notificationService;

    @Override
    @Transactional
    public PrivateMessageDto sendMessage(PrivateMessageDto messageDto) {
        // 验证是否为好友
        if (!friendMapper.areFriends(messageDto.getSenderId(), messageDto.getReceiverId())) {
            throw new RuntimeException("只能给好友发送私信");
        }

        // 创建消息实体
        PrivateMessage message = new PrivateMessage();

        User sender = userMapper.findById(messageDto.getSenderId());
        User receiver = userMapper.findById(messageDto.getReceiverId());

        if (sender == null || receiver == null) {
            throw new RuntimeException("用户不存在");
        }

        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageDto.getContent());
        message.setMessageType(messageDto.getMessageType());
        message.setAttachmentUrl(messageDto.getAttachmentUrl());

        // 保存消息
        messageMapper.insertMessage(message);

        // 更新最后聊天时间
        friendMapper.updateLastChatTime(messageDto.getSenderId(), messageDto.getReceiverId());

        // 发送消息通知
        sendMessageNotification(sender, receiver);

        // 转换为DTO返回
        return convertToDto(message);
    }

    @Override
    public List<PrivateMessageDto> getChatHistory(Integer userId1, Integer userId2,
                                                  int page, int size) {
        // 验证是否为好友
        if (!friendMapper.areFriends(userId1, userId2)) {
            throw new RuntimeException("只能查看好友的聊天记录");
        }

        int offset = (page - 1) * size;
        List<PrivateMessage> messages = messageMapper.getChatHistory(userId1, userId2, offset, size);

        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Integer receiverId, Integer senderId) {
        messageMapper.markMessagesAsRead(receiverId, senderId);
    }

    @Override
    public int getUnreadCount(Integer userId) {
        return messageMapper.getUnreadCount(userId);
    }

    @Override
    @Transactional
    public boolean recallMessage(Long messageId, Integer senderId) {
        int result = messageMapper.recallMessage(messageId, senderId);
        return result > 0;
    }

    private void sendMessageNotification(User sender, User receiver) {
        try {
            notificationService.sendSystemNotification(
                    receiver.getUserId(),
                    "新私信",
                    sender.getNickname() + " 给您发送了一条私信"
            );
        } catch (Exception e) {
            // 通知失败不影响主流程
        }
    }

    private PrivateMessageDto convertToDto(PrivateMessage message) {
        PrivateMessageDto dto = new PrivateMessageDto();
        dto.setMessageId(message.getMessageId());
        dto.setSenderId(message.getSender().getUserId());
        dto.setSenderNickname(message.getSender().getNickname());
        dto.setSenderAvatar(message.getSender().getAvatarUrl());
        dto.setReceiverId(message.getReceiver().getUserId());
        dto.setReceiverNickname(message.getReceiver().getNickname());
        dto.setReceiverAvatar(message.getReceiver().getAvatarUrl());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setAttachmentUrl(message.getAttachmentUrl());
        dto.setIsRead(message.getIsRead());
        dto.setStatus(message.getStatus());
        dto.setSendTime(message.getSendTime());
        dto.setReadTime(message.getReadTime());
        dto.setSendTimeAgo(calculateTimeAgo(message.getSendTime()));

        // 判断是否可撤回（2分钟内）
        long diff = System.currentTimeMillis() - message.getSendTime().getTime();
        dto.setCanRecall(diff <= 2 * 60 * 1000);

        return dto;
    }

    private String calculateTimeAgo(Date time) {
        long diff = System.currentTimeMillis() - time.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 60) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (days < 30) {
            return days + "天前";
        } else {
            return "很久以前";
        }
    }
}
