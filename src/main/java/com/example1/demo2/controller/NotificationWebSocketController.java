package com.example1.demo2.controller;

import com.example1.demo2.service.impl.WebSocketNotificationService;
import com.example1.demo2.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.util.Map;

/**
 * WebSocket通知控制器
 * 处理实时通知的WebSocket连接
 */
@Controller
@EnableWebSocketMessageBroker
public class NotificationWebSocketController {

    @Autowired
    private WebSocketNotificationService webSocketService;

    /**
     * 用户连接WebSocket
     */
    @MessageMapping("/connect")
    @SendTo("/topic/connected")
    public Map<String, Object> connect(SimpMessageHeaderAccessor headerAccessor) {
        // 获取当前用户ID
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer userId = (Integer) userInfo.get("userId");

        if (userId != null) {
            String sessionId = headerAccessor.getSessionId();
            webSocketService.userOnline(userId, sessionId);

            return Map.of(
                    "status", "connected",
                    "userId", userId,
                    "onlineCount", webSocketService.getOnlineUserCount()
            );
        }

        return Map.of("status", "error", "message", "未登录");
    }

    /**
     * 用户断开连接
     */
    @MessageMapping("/disconnect")
    public void disconnect() {
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer userId = (Integer) userInfo.get("userId");

        if (userId != null) {
            webSocketService.userOffline(userId);
        }
    }
}