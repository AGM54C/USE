package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.NotificationMapper;
import com.example1.demo2.mapper.UserMapper;
import com.example1.demo2.mapper.GalaxyCommentMapper;
import com.example1.demo2.pojo.Notification;
import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.NotificationDto;
import com.example1.demo2.service.INotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 * 这是通知系统的核心，负责处理所有通知相关的业务逻辑
 */
@Service
public class NotificationService implements INotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GalaxyCommentMapper commentMapper;

    @Autowired
    private ObjectMapper objectMapper;  // 用于处理JSON数据

    // 防重复通知的时间间隔（分钟）
    private static final int DUPLICATE_CHECK_MINUTES = 5;

    /**
     * 发送评论回复通知
     * 这个方法就像邮递员收到一封需要投递的回复信件
     */
    @Override
    @Transactional
    public void sendCommentReplyNotification(Integer senderId, Integer receiverId,
                                             Integer commentId, String content) {
        try {
            // 首先，我们需要验证发送者和接收者是否存在
            // 就像邮递员要确认寄信人和收信人的地址是否正确
            if (senderId.equals(receiverId)) {
                // 自己回复自己不需要通知
                logger.info("用户{}回复了自己的评论，不发送通知", senderId);
                return;
            }

            // 检查是否在短时间内已经发送过相同的通知
            // 避免用户在短时间内重复操作导致通知轰炸
            if (isDuplicateNotification(receiverId, senderId, 1, commentId)) {
                logger.info("{}分钟内已发送过相同通知，跳过", DUPLICATE_CHECK_MINUTES);
                return;
            }

            // 获取发送者信息，用于显示通知
            User sender = userMapper.findById(senderId);
            User receiver = userMapper.findById(receiverId);

            if (sender == null || receiver == null) {
                logger.error("发送者或接收者不存在");
                return;
            }

            // 创建通知对象
            Notification notification = new Notification();
            notification.setSender(sender);
            notification.setReceiver(receiver);
            notification.setType(1);  // 评论回复类型
            notification.setTitle(sender.getNickname() + " 回复了你的评论");

            // 处理内容，如果太长就截断
            String displayContent = content.length() > 100 ?
                    content.substring(0, 100) + "..." : content;
            notification.setContent(displayContent);

            notification.setTargetType(1);  // 目标类型：评论
            notification.setTargetId(commentId);

            // 构建额外数据，方便前端跳转
            Map<String, Object> extraData = new HashMap<>();
            extraData.put("jumpUrl", "/galaxy/comment/detail/" + commentId);
            extraData.put("senderAvatar", sender.getAvatarUrl());
            notification.setExtraData(objectMapper.writeValueAsString(extraData));

            // 保存通知到数据库
            notificationMapper.insertNotification(notification);

            // 这里可以触发实时推送
            // 就像邮局可以提供短信通知服务
            sendRealtimeNotification(receiverId, notification);

            logger.info("成功发送评论回复通知：{} -> {}", senderId, receiverId);

        } catch (Exception e) {
            logger.error("发送评论回复通知失败", e);
            // 通知发送失败不应该影响主业务流程
        }
    }

    /**
     * 发送点赞通知
     * 点赞通知就像收到一个"赞"的贴纸
     */
    @Override
    @Transactional
    public void sendLikeNotification(Integer senderId, Integer receiverId,
                                     Integer commentId, String commentContent) {
        try {
            // 自己给自己点赞不发通知
            if (senderId.equals(receiverId)) {
                return;
            }

            // 检查重复通知
            if (isDuplicateNotification(receiverId, senderId, 2, commentId)) {
                return;
            }

            User sender = userMapper.findById(senderId);
            User receiver = userMapper.findById(receiverId);

            if (sender == null || receiver == null) {
                return;
            }

            Notification notification = new Notification();
            notification.setSender(sender);
            notification.setReceiver(receiver);
            notification.setType(2);  // 点赞类型
            notification.setTitle(sender.getNickname() + " 赞了你的评论");

            // 展示被点赞的评论内容预览
            String preview = commentContent.length() > 50 ?
                    commentContent.substring(0, 50) + "..." : commentContent;
            notification.setContent("你的评论\"" + preview + "\"获得了一个赞");

            notification.setTargetType(1);  // 评论
            notification.setTargetId(commentId);

            Map<String, Object> extraData = new HashMap<>();
            extraData.put("jumpUrl", "/galaxy/comment/detail/" + commentId);
            notification.setExtraData(objectMapper.writeValueAsString(extraData));

            notificationMapper.insertNotification(notification);
            sendRealtimeNotification(receiverId, notification);

        } catch (Exception e) {
            logger.error("发送点赞通知失败", e);
        }
    }

    /**
     * 获取用户通知列表
     * 就像查看自己的收件箱
     */
    @Override
    public List<NotificationDto> getUserNotifications(Integer userId, Integer type,
                                                      Integer isRead, int page, int size) {
        // 计算分页偏移量
        int offset = (page - 1) * size;

        // 从数据库获取通知列表
        List<Notification> notifications = notificationMapper.getNotificationsByUser(
                userId, type, isRead, offset, size
        );

        // 转换为DTO并返回
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取未读通知数量
     * 就像数一下信箱里有多少未拆封的信件
     */
    @Override
    public int getUnreadCount(Integer userId) {
        return notificationMapper.countUnreadNotifications(userId, null);
    }

    /**
     * 标记通知为已读
     * 就像在信件上盖上"已阅"的印章
     */
    @Override
    @Transactional
    public boolean markAsRead(Integer notificationId, Integer userId) {
        int result = notificationMapper.markAsRead(notificationId, userId);
        return result > 0;
    }

    /**
     * 标记所有通知为已读
     * 一键清空所有未读标记
     */
    @Override
    @Transactional
    public int markAllAsRead(Integer userId) {
        return notificationMapper.markAllAsRead(userId);
    }

    /**
     * 将通知实体转换为DTO
     * 这个过程就像把内部文件整理成对外展示的格式
     */
    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();

        // 基本信息转换
        dto.setNotificationId(notification.getNotificationId());
        dto.setReceiverId(notification.getReceiver().getUserId());
        dto.setReceiverName(notification.getReceiver().getNickname());

        if (notification.getSender() != null) {
            dto.setSenderId(notification.getSender().getUserId());
            dto.setSenderName(notification.getSender().getNickname());
            dto.setSenderAvatar(notification.getSender().getAvatarUrl());
        }

        dto.setType(notification.getType());
        dto.setTypeDesc(getTypeDescription(notification.getType()));
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setTargetType(notification.getTargetType());
        dto.setTargetId(notification.getTargetId());
        dto.setIsRead(notification.getIsRead());
        dto.setStatus(notification.getStatus());
        dto.setCreateTime(notification.getCreateTime());
        dto.setReadTime(notification.getReadTime());

        // 计算友好的时间显示
        dto.setCreateTimeAgo(calculateTimeAgo(notification.getCreateTime()));

        // 解析额外数据
        if (notification.getExtraData() != null) {
            try {
                Map<String, Object> extraData = objectMapper.readValue(
                        notification.getExtraData(),
                        Map.class
                );
                dto.setExtraData(extraData);
                dto.setJumpUrl((String) extraData.get("jumpUrl"));
            } catch (Exception e) {
                logger.error("解析额外数据失败", e);
            }
        }

        return dto;
    }

    /**
     * 获取通知类型的描述
     * 让前端更容易理解每种通知的含义
     */
    private String getTypeDescription(Integer type) {
        switch (type) {
            case 1: return "评论回复";
            case 2: return "评论点赞";
            case 3: return "星系新评论";
            case 4: return "系统通知";
            default: return "其他通知";
        }
    }

    /**
     * 计算友好的时间显示
     * 将时间转换为"5分钟前"、"2小时前"这样的格式
     */
    private String calculateTimeAgo(Date createTime) {
        long diff = System.currentTimeMillis() - createTime.getTime();
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

    /**
     * 检查是否为重复通知
     * 防止用户在短时间内收到相同的通知
     */
    private boolean isDuplicateNotification(Integer receiverId, Integer senderId,
                                            Integer type, Integer targetId) {
        int count = notificationMapper.checkDuplicateNotification(
                receiverId, senderId, type, targetId, DUPLICATE_CHECK_MINUTES
        );
        return count > 0;
    }

    /**
     * 发送实时通知
     * 这是一个预留的方法，可以集成WebSocket或其他推送服务
     */
    private void sendRealtimeNotification(Integer userId, Notification notification) {
        // TODO: 实现实时推送
        // 可以使用WebSocket、Server-Sent Events或第三方推送服务
        logger.debug("准备推送实时通知给用户{}", userId);
    }

    // 其他方法的实现...

    @Override
    public void sendGalaxyCommentNotification(Integer senderId, Integer galaxyOwnerId,
                                              Integer galaxyId, String galaxyName) {
        // 实现逻辑类似于评论回复通知
    }

    @Override
    public void sendPlanetCommentNotification(Integer senderId, Integer planetOwnerId, String planetId, String planetName) {

    }

    @Override
    public void sendSystemNotification(Integer receiverId, String title, String content) {
        // 系统通知的实现
    }

    @Override
    public Map<Integer, Integer> getUnreadCountByType(Integer userId) {
        List<Map<String, Object>> results = notificationMapper.countUnreadByType(userId);
        Map<Integer, Integer> countMap = new HashMap<>();
        for (Map<String, Object> result : results) {
            Integer type = (Integer) result.get("type");
            Integer count = ((Long) result.get("count")).intValue();
            countMap.put(type, count);
        }
        return countMap;
    }

    @Override
    public int markAsReadBatch(List<Integer> notificationIds, Integer userId) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return 0;
        }
        return notificationMapper.markAsReadBatch(userId, notificationIds);
    }

    @Override
    public boolean deleteNotification(Integer notificationId, Integer userId) {
        return notificationMapper.deleteNotification(notificationId, userId) > 0;
    }

    @Override
    public void cleanExpiredNotifications() {
        int cleaned = notificationMapper.cleanExpiredNotifications(30);
        logger.info("清理了{}条过期通知", cleaned);
    }
}