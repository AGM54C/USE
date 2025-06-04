package com.example1.demo2.service.impl;

import com.example1.demo2.pojo.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket实时通知服务
 * 用于推送实时通知给在线用户
 */
@Service
public class WebSocketNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketNotificationService.class);

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // 存储用户在线状态
    private final Map<Integer, String> onlineUsers = new ConcurrentHashMap<>();

    /**
     * 发送实时通知给指定用户
     */
    public void sendRealtimeNotification(Integer userId, Notification notification) {
        if (messagingTemplate == null) {
            logger.debug("WebSocket未配置，跳过实时推送");
            return;
        }

        try {
            // 构建通知消息
            Map<String, Object> message = new HashMap<>();
            message.put("type", "notification");
            message.put("data", notification);
            message.put("timestamp", System.currentTimeMillis());

            // 推送到用户专属通道
            String destination = "/user/" + userId + "/notifications";
            messagingTemplate.convertAndSend(destination, message);

            logger.info("实时通知已推送给用户: {}", userId);
        } catch (Exception e) {
            logger.error("推送实时通知失败", e);
        }
    }

    /**
     * 用户上线
     */
    public void userOnline(Integer userId, String sessionId) {
        onlineUsers.put(userId, sessionId);
        logger.info("用户 {} 上线，会话ID: {}", userId, sessionId);
    }

    /**
     * 用户下线
     */
    public void userOffline(Integer userId) {
        onlineUsers.remove(userId);
        logger.info("用户 {} 下线", userId);
    }

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(Integer userId) {
        return onlineUsers.containsKey(userId);
    }

    /**
     * 获取在线用户数
     */
    public int getOnlineUserCount() {
        return onlineUsers.size();
    }
}