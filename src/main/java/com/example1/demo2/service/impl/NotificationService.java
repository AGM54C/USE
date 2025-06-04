package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.NotificationMapper;
import com.example1.demo2.mapper.UserMapper;
import com.example1.demo2.mapper.GalaxyCommentMapper;
import com.example1.demo2.mapper.PlanetCommentMapper;
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
 * 支持完整的7种通知类型
 */
@Service
public class NotificationService implements INotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GalaxyCommentMapper galaxyCommentMapper;

    @Autowired
    private PlanetCommentMapper planetCommentMapper;

    @Autowired
    private ObjectMapper objectMapper;  // 用于处理JSON数据

    @Autowired(required = false)
    private WebSocketNotificationService webSocketService;  // 实时推送服务（可选）

    // 防重复通知的时间间隔（分钟）
    private static final int DUPLICATE_CHECK_MINUTES = 5;

    // ==================== 星系相关通知实现 ====================

    /**
     * 发送星系评论回复通知
     * 通知类型：1
     */
    @Override
    @Transactional
    public void sendGalaxyCommentReplyNotification(Integer senderId, Integer receiverId,
                                                   Integer commentId, String content) {
        try {
            // 验证发送者和接收者
            if (senderId.equals(receiverId)) {
                logger.info("用户{}回复了自己的评论，不发送通知", senderId);
                return;
            }

            // 检查重复通知
            if (isDuplicateNotification(receiverId, senderId, 1, String.valueOf(commentId))) {
                logger.info("{}分钟内已发送过相同通知，跳过", DUPLICATE_CHECK_MINUTES);
                return;
            }

            // 获取用户信息
            User sender = userMapper.findById(senderId);
            User receiver = userMapper.findById(receiverId);

            if (sender == null || receiver == null) {
                logger.error("发送者或接收者不存在");
                return;
            }

            // 创建通知
            Notification notification = new Notification();
            notification.setSender(sender);
            notification.setReceiver(receiver);
            notification.setType(1);  // 星系评论回复
            notification.setTitle(sender.getNickname() + " 回复了你的评论");

            // 处理内容
            String displayContent = content.length() > 100 ?
                    content.substring(0, 100) + "..." : content;
            notification.setContent(displayContent);

            notification.setTargetType(1);  // 目标类型：星系评论
            notification.setTargetId(String.valueOf(commentId));

            // 构建额外数据
            Map<String, Object> extraData = new HashMap<>();
            extraData.put("jumpUrl", "/galaxy/comment/detail/" + commentId);
            extraData.put("senderAvatar", sender.getAvatarUrl());
            notification.setExtraData(objectMapper.writeValueAsString(extraData));

            // 保存通知
            notificationMapper.insertNotification(notification);

            // 触发实时推送
            sendRealtimeNotification(receiverId, notification);

            logger.info("成功发送星系评论回复通知：{} -> {}", senderId, receiverId);

        } catch (Exception e) {
            logger.error("发送星系评论回复通知失败", e);
        }
    }

    /**
     * 发送星系评论点赞通知
     * 通知类型：2
     */
    @Override
    @Transactional
    public void sendGalaxyCommentLikeNotification(Integer senderId, Integer receiverId,
                                                  Integer commentId, String commentContent) {
        try {
            if (senderId.equals(receiverId)) {
                return;
            }

            if (isDuplicateNotification(receiverId, senderId, 2, String.valueOf(commentId))) {
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
            notification.setType(2);  // 星系评论点赞
            notification.setTitle(sender.getNickname() + " 赞了你的评论");

            String preview = commentContent.length() > 50 ?
                    commentContent.substring(0, 50) + "..." : commentContent;
            notification.setContent("你的评论\"" + preview + "\"获得了一个赞");

            notification.setTargetType(1);  // 星系评论
            notification.setTargetId(String.valueOf(commentId));

            Map<String, Object> extraData = new HashMap<>();
            extraData.put("jumpUrl", "/galaxy/comment/detail/" + commentId);
            extraData.put("senderAvatar", sender.getAvatarUrl());
            notification.setExtraData(objectMapper.writeValueAsString(extraData));

            notificationMapper.insertNotification(notification);
            sendRealtimeNotification(receiverId, notification);

        } catch (Exception e) {
            logger.error("发送星系评论点赞通知失败", e);
        }
    }

    /**
     * 发送星系新评论通知
     * 通知类型：3
     */
    @Override
    @Transactional
    public void sendGalaxyNewCommentNotification(Integer senderId, Integer galaxyOwnerId,
                                                 Integer galaxyId, String galaxyName) {
        try {
            if (senderId.equals(galaxyOwnerId)) {
                return;
            }

            if (isDuplicateNotification(galaxyOwnerId, senderId, 3, String.valueOf(galaxyId))) {
                return;
            }

            User sender = userMapper.findById(senderId);
            User receiver = userMapper.findById(galaxyOwnerId);

            if (sender == null || receiver == null) {
                return;
            }

            Notification notification = new Notification();
            notification.setSender(sender);
            notification.setReceiver(receiver);
            notification.setType(3);  // 星系新评论
            notification.setTitle(sender.getNickname() + " 在你的星系中发表了评论");
            notification.setContent("在星系\"" + galaxyName + "\"中有新的评论");

            notification.setTargetType(2);  // 目标类型：星系
            notification.setTargetId(String.valueOf(galaxyId));

            Map<String, Object> extraData = new HashMap<>();
            extraData.put("jumpUrl", "/galaxy/" + galaxyId);
            extraData.put("senderAvatar", sender.getAvatarUrl());
            notification.setExtraData(objectMapper.writeValueAsString(extraData));

            notificationMapper.insertNotification(notification);
            sendRealtimeNotification(galaxyOwnerId, notification);

        } catch (Exception e) {
            logger.error("发送星系新评论通知失败", e);
        }
    }

    // ==================== 星球相关通知实现 ====================

    /**
     * 发送星球评论回复通知
     * 通知类型：4
     */
    @Override
    @Transactional
    public void sendPlanetCommentReplyNotification(Integer senderId, Integer receiverId,
                                                   Integer commentId, String content) {
        try {
            if (senderId.equals(receiverId)) {
                logger.info("用户{}回复了自己的评论，不发送通知", senderId);
                return;
            }

            if (isDuplicateNotification(receiverId, senderId, 4, String.valueOf(commentId))) {
                logger.info("{}分钟内已发送过相同通知，跳过", DUPLICATE_CHECK_MINUTES);
                return;
            }

            User sender = userMapper.findById(senderId);
            User receiver = userMapper.findById(receiverId);

            if (sender == null || receiver == null) {
                logger.error("发送者或接收者不存在");
                return;
            }

            Notification notification = new Notification();
            notification.setSender(sender);
            notification.setReceiver(receiver);
            notification.setType(4);  // 星球评论回复
            notification.setTitle(sender.getNickname() + " 回复了你的评论");

            String displayContent = content.length() > 100 ?
                    content.substring(0, 100) + "..." : content;
            notification.setContent(displayContent);

            notification.setTargetType(3);  // 目标类型：星球评论
            notification.setTargetId(String.valueOf(commentId));

            Map<String, Object> extraData = new HashMap<>();
            extraData.put("jumpUrl", "/planet/comment/detail/" + commentId);
            extraData.put("senderAvatar", sender.getAvatarUrl());
            notification.setExtraData(objectMapper.writeValueAsString(extraData));

            notificationMapper.insertNotification(notification);
            sendRealtimeNotification(receiverId, notification);

            logger.info("成功发送星球评论回复通知：{} -> {}", senderId, receiverId);

        } catch (Exception e) {
            logger.error("发送星球评论回复通知失败", e);
        }
    }

    /**
     * 发送星球评论点赞通知
     * 通知类型：5
     */
    @Override
    @Transactional
    public void sendPlanetCommentLikeNotification(Integer senderId, Integer receiverId,
                                                  Integer commentId, String commentContent) {
        try {
            if (senderId.equals(receiverId)) {
                return;
            }

            if (isDuplicateNotification(receiverId, senderId, 5, String.valueOf(commentId))) {
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
            notification.setType(5);  // 星球评论点赞
            notification.setTitle(sender.getNickname() + " 赞了你的评论");

            String preview = commentContent.length() > 50 ?
                    commentContent.substring(0, 50) + "..." : commentContent;
            notification.setContent("你的评论\"" + preview + "\"获得了一个赞");

            notification.setTargetType(3);  // 星球评论
            notification.setTargetId(String.valueOf(commentId));

            Map<String, Object> extraData = new HashMap<>();
            extraData.put("jumpUrl", "/planet/comment/detail/" + commentId);
            extraData.put("senderAvatar", sender.getAvatarUrl());
            notification.setExtraData(objectMapper.writeValueAsString(extraData));

            notificationMapper.insertNotification(notification);
            sendRealtimeNotification(receiverId, notification);

        } catch (Exception e) {
            logger.error("发送星球评论点赞通知失败", e);
        }
    }

    /**
     * 发送星球新评论通知
     * 通知类型：6
     */
    @Override
    @Transactional
    public void sendPlanetNewCommentNotification(Integer senderId, Integer planetOwnerId,
                                                 String planetId, String planetName) {
        try {
            if (senderId.equals(planetOwnerId)) {
                return;
            }

            if (isDuplicateNotification(planetOwnerId, senderId, 6, planetId)) {
                return;
            }

            User sender = userMapper.findById(senderId);
            User receiver = userMapper.findById(planetOwnerId);

            if (sender == null || receiver == null) {
                return;
            }

            Notification notification = new Notification();
            notification.setSender(sender);
            notification.setReceiver(receiver);
            notification.setType(6);  // 星球新评论
            notification.setTitle(sender.getNickname() + " 在你的星球中发表了评论");
            notification.setContent("在星球\"" + planetName + "\"中有新的评论");

            notification.setTargetType(4);  // 目标类型：星球
            notification.setTargetId(planetId);

            Map<String, Object> extraData = new HashMap<>();
            extraData.put("jumpUrl", "/planet/" + planetId);
            extraData.put("senderAvatar", sender.getAvatarUrl());
            notification.setExtraData(objectMapper.writeValueAsString(extraData));

            notificationMapper.insertNotification(notification);
            sendRealtimeNotification(planetOwnerId, notification);

        } catch (Exception e) {
            logger.error("发送星球新评论通知失败", e);
        }
    }

    /**
     * 发送系统通知
     * 通知类型：7
     */
    @Override
    @Transactional
    public void sendSystemNotification(Integer receiverId, String title, String content) {
        try {
            if (receiverId == null) {
                // 群发给所有用户
                sendSystemNotificationToAll(title, content);
                return;
            }

            User receiver = userMapper.findById(receiverId);
            if (receiver == null) {
                logger.error("接收者不存在：{}", receiverId);
                return;
            }

            Notification notification = new Notification();
            notification.setReceiver(receiver);
            notification.setSender(null);  // 系统通知没有发送者
            notification.setType(7);  // 系统通知
            notification.setTitle(title);
            notification.setContent(content);
            notification.setTargetType(5);  // 其他
            notification.setTargetId(null);

            Map<String, Object> extraData = new HashMap<>();
            extraData.put("systemNotice", true);
            notification.setExtraData(objectMapper.writeValueAsString(extraData));

            notificationMapper.insertNotification(notification);
            sendRealtimeNotification(receiverId, notification);

        } catch (Exception e) {
            logger.error("发送系统通知失败", e);
        }
    }

    /**
     * 群发系统通知给所有用户
     * 实现批量发送逻辑
     */
    @Transactional
    public void sendSystemNotificationToAll(String title, String content) {
        logger.info("开始群发系统通知：{}", title);

        // 分批处理，避免一次性创建太多记录
        final int BATCH_SIZE = 100;
        int page = 0;
        int processedCount = 0;

        while (true) {
            // 获取一批用户（这里假设UserMapper有分页查询所有用户的方法）
            // 需要在UserMapper中添加：
            // @Select("SELECT * FROM tab_user WHERE status = 0 LIMIT #{offset}, #{size}")
            // List<User> getUsersWithPagination(@Param("offset") int offset, @Param("size") int size);

            List<User> users = getUserBatch(page * BATCH_SIZE, BATCH_SIZE);
            if (users.isEmpty()) {
                break;
            }

            // 构建批量通知
            List<Notification> notifications = new ArrayList<>();
            for (User user : users) {
                Notification notification = new Notification();
                notification.setReceiver(user);
                notification.setSender(null); // 系统通知没有发送者
                notification.setType(7); // 系统通知
                notification.setTitle(title);
                notification.setContent(content);
                notification.setTargetType(5); // 其他
                notification.setTargetId(null);

                Map<String, Object> extraData = new HashMap<>();
                extraData.put("systemNotice", true);
                extraData.put("broadcast", true);
                try {
                    notification.setExtraData(objectMapper.writeValueAsString(extraData));
                } catch (Exception e) {
                    logger.error("序列化额外数据失败", e);
                }

                notifications.add(notification);
            }

            // 批量插入
            notificationMapper.insertNotificationBatch(notifications);

            processedCount += users.size();
            page++;

            // 避免过度占用资源，每批次后短暂休眠
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.info("群发系统通知完成，共发送给 {} 个用户", processedCount);
    }

    /**
     * 获取用户批次（辅助方法）
     */
    private List<User> getUserBatch(int offset, int size) {
        // 这里需要实现或调用UserMapper的分页查询方法
        // 临时返回空列表，实际使用时需要实现
        return new ArrayList<>();
    }
    // ==================== 通知查询和管理实现 ====================

    /**
     * 获取用户通知列表
     */
    @Override
    public List<NotificationDto> getUserNotifications(Integer userId, Integer type,
                                                      Integer isRead, int page, int size) {
        int offset = (page - 1) * size;

        List<Notification> notifications = notificationMapper.getNotificationsByUser(
                userId, type, isRead, offset, size
        );

        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取未读通知数量
     */
    @Override
    public int getUnreadCount(Integer userId) {
        return notificationMapper.countUnreadNotifications(userId, null);
    }

    /**
     * 获取分类未读数量
     */
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

    /**
     * 标记通知为已读
     */
    @Override
    @Transactional
    public boolean markAsRead(Integer notificationId, Integer userId) {
        int result = notificationMapper.markAsRead(notificationId, userId);
        return result > 0;
    }

    /**
     * 批量标记为已读
     */
    @Override
    @Transactional
    public int markAsReadBatch(List<Integer> notificationIds, Integer userId) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return 0;
        }
        return notificationMapper.markAsReadBatch(userId, notificationIds);
    }

    /**
     * 标记所有通知为已读
     */
    @Override
    @Transactional
    public int markAllAsRead(Integer userId) {
        return notificationMapper.markAllAsRead(userId);
    }

    /**
     * 标记特定类型的所有通知为已读
     */
    @Override
    @Transactional
    public int markTypeAsRead(Integer userId, Integer type) {
        return notificationMapper.markTypeAsRead(userId, type);
    }

    /**
     * 删除通知
     */
    @Override
    @Transactional
    public boolean deleteNotification(Integer notificationId, Integer userId) {
        return notificationMapper.deleteNotification(notificationId, userId) > 0;
    }

    /**
     * 批量删除通知
     */
    @Override
    @Transactional
    public int deleteNotificationBatch(List<Integer> notificationIds, Integer userId) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return 0;
        }
        return notificationMapper.deleteNotificationBatch(userId, notificationIds);
    }

    /**
     * 清理过期通知
     */
    @Override
    public void cleanExpiredNotifications() {
        int cleaned = notificationMapper.cleanExpiredNotifications(30);
        logger.info("清理了{}条过期通知", cleaned);
    }

    // ==================== 辅助方法 ====================

    /**
     * 将通知实体转换为DTO
     */
    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();

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

        dto.setCreateTimeAgo(calculateTimeAgo(notification.getCreateTime()));

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
     */
    private String getTypeDescription(Integer type) {
        switch (type) {
            case 1:
                return "星系评论回复";
            case 2:
                return "星系评论点赞";
            case 3:
                return "星系新评论";
            case 4:
                return "星球评论回复";
            case 5:
                return "星球评论点赞";
            case 6:
                return "星球新评论";
            case 7:
                return "系统通知";
            default:
                return "其他通知";
        }
    }

    /**
     * 计算友好的时间显示
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
     */
    private boolean isDuplicateNotification(Integer receiverId, Integer senderId,
                                            Integer type, String targetId) {
        int count = notificationMapper.checkDuplicateNotification(
                receiverId, senderId, type, targetId, DUPLICATE_CHECK_MINUTES
        );
        return count > 0;
    }

    /**
     * 发送实时通知
     * 集成WebSocket推送服务
     */
    private void sendRealtimeNotification(Integer userId, Notification notification) {
        if (webSocketService != null) {
            webSocketService.sendRealtimeNotification(userId, notification);
        } else {
            logger.debug("WebSocket服务未配置，通知将在用户下次登录时显示");
        }
    }
}