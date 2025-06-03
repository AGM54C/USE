package com.example1.demo2.controller;

import com.example1.demo2.pojo.dto.NotificationDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.INotificationService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 * 这是通知系统的对外接口，就像邮局的服务窗口
 * 用户可以通过这些接口查看通知、标记已读、管理通知等
 * 支持7种通知类型的完整管理
 */
@RestController
@RequestMapping("/notification")  // localhost:8081/notification/**
@Validated
public class NotificationController {

    @Autowired
    private INotificationService notificationService;

    /**
     * 获取通知列表
     *
     * 这个接口就像查看自己的邮箱，可以按不同条件筛选通知
     *
     * 前端请求方式：GET
     * 请求URL：localhost:8081/notification/list
     * 请求参数：
     *   - userId: 用户ID（必填）
     *   - type: 通知类型（可选）
     *       1-星系评论回复 2-星系评论点赞 3-星系新评论
     *       4-星球评论回复 5-星球评论点赞 6-星球新评论 7-系统通知
     *   - isRead: 是否已读（可选，0-未读 1-已读）
     *   - page: 页码（默认1）
     *   - size: 每页大小（默认20）
     *
     * 返回示例：
     * {
     *   "code": 0,
     *   "message": "success",
     *   "data": [
     *     {
     *       "notificationId": 1,
     *       "senderName": "张三",
     *       "senderAvatar": "avatar_url",
     *       "type": 1,
     *       "typeDesc": "星系评论回复",
     *       "title": "张三回复了你的评论",
     *       "content": "说得很有道理...",
     *       "createTimeAgo": "5分钟前",
     *       "isRead": 0,
     *       "jumpUrl": "/galaxy/comment/detail/123"
     *     }
     *   ]
     * }
     */
    @GetMapping("/list")
    public ResponseMessage getNotificationList(
            @RequestParam @NotNull(message = "用户ID不能为空") Integer userId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer isRead,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            // 参数验证
            if (type != null && (type < 1 || type > 7)) {
                return ResponseMessage.error("无效的通知类型");
            }

            List<NotificationDto> notifications = notificationService.getUserNotifications(
                    userId, type, isRead, page, size
            );
            return ResponseMessage.success(notifications);
        } catch (Exception e) {
            return ResponseMessage.error("获取通知列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取未读通知数量
     *
     * 这个接口通常用于在界面上显示小红点
     * 就像信箱上的"您有X封未读邮件"提示
     *
     * 前端请求方式：GET
     * 请求URL：localhost:8081/notification/unread/count
     * 请求参数：userId - 用户ID
     *
     * 返回示例：
     * {
     *   "code": 0,
     *   "message": "success",
     *   "data": {
     *     "total": 5,           // 总未读数
     *     "byType": {           // 分类未读数
     *       "1": 2,             // 星系评论回复: 2条
     *       "2": 1,             // 星系评论点赞: 1条
     *       "4": 2              // 星球评论回复: 2条
     *     }
     *   }
     * }
     */
    @GetMapping("/unread/count")
    public ResponseMessage getUnreadCount(
            @RequestParam @NotNull(message = "用户ID不能为空") Integer userId) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 获取总未读数
            int totalUnread = notificationService.getUnreadCount(userId);
            result.put("total", totalUnread);

            // 获取分类未读数
            Map<Integer, Integer> byType = notificationService.getUnreadCountByType(userId);
            result.put("byType", byType);

            return ResponseMessage.success(result);
        } catch (Exception e) {
            return ResponseMessage.error("获取未读数量失败: " + e.getMessage());
        }
    }

    /**
     * 标记单个通知为已读
     *
     * 当用户点击通知查看详情时调用
     * 就像在信件上盖上"已阅"的印章
     *
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/notification/read/{notificationId}
     * 路径参数：notificationId - 通知ID
     * 请求参数：userId - 用户ID（用于权限验证）
     *
     * 返回示例：
     * {
     *   "code": 0,
     *   "message": "标记成功"
     * }
     */
    @PutMapping("/read/{notificationId}")
    public ResponseMessage markAsRead(
            @PathVariable @NotNull Integer notificationId,
            @RequestParam @NotNull Integer userId) {
        try {
            boolean success = notificationService.markAsRead(notificationId, userId);
            if (success) {
                return ResponseMessage.success("标记成功");
            } else {
                return ResponseMessage.error("标记失败，请检查通知是否存在");
            }
        } catch (Exception e) {
            return ResponseMessage.error("标记失败: " + e.getMessage());
        }
    }

    /**
     * 批量标记为已读
     *
     * 用户可以选择多个通知一次性标记为已读
     * 就像一次性处理多封信件
     *
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/notification/read/batch
     * 请求体（JSON）：
     * {
     *   "userId": 1,
     *   "notificationIds": [1, 2, 3]
     * }
     */
    @PutMapping("/read/batch")
    public ResponseMessage markAsReadBatch(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            List<Integer> notificationIds = (List<Integer>) request.get("notificationIds");

            if (userId == null || notificationIds == null || notificationIds.isEmpty()) {
                return ResponseMessage.error("参数错误");
            }

            int count = notificationService.markAsReadBatch(notificationIds, userId);
            return ResponseMessage.success("成功标记" + count + "条通知为已读");
        } catch (Exception e) {
            return ResponseMessage.error("批量标记失败: " + e.getMessage());
        }
    }

    /**
     * 标记所有通知为已读
     *
     * 一键清空所有未读通知
     * 就像邮局提供的"全部签收"服务
     *
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/notification/read/all
     * 请求参数：userId - 用户ID
     */
    @PutMapping("/read/all")
    public ResponseMessage markAllAsRead(
            @RequestParam @NotNull Integer userId) {
        try {
            int count = notificationService.markAllAsRead(userId);
            return ResponseMessage.success("成功标记" + count + "条通知为已读");
        } catch (Exception e) {
            return ResponseMessage.error("全部标记失败: " + e.getMessage());
        }
    }

    /**
     * 按类型标记为已读
     *
     * 标记特定类型的所有通知为已读
     * 比如标记所有"点赞通知"为已读
     *
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/notification/read/type
     * 请求参数：
     *   - userId: 用户ID
     *   - type: 通知类型（1-7）
     */
    @PutMapping("/read/type")
    public ResponseMessage markTypeAsRead(
            @RequestParam @NotNull Integer userId,
            @RequestParam @NotNull Integer type) {
        try {
            if (type < 1 || type > 7) {
                return ResponseMessage.error("无效的通知类型");
            }

            int count = notificationService.markTypeAsRead(userId, type);
            return ResponseMessage.success("成功标记" + count + "条通知为已读");
        } catch (Exception e) {
            return ResponseMessage.error("按类型标记失败: " + e.getMessage());
        }
    }

    /**
     * 删除通知
     *
     * 软删除，不会真正从数据库删除
     * 就像把信件放入垃圾箱，还可以恢复
     *
     * 前端请求方式：DELETE
     * 请求URL：localhost:8081/notification/{notificationId}
     * 路径参数：notificationId - 通知ID
     * 请求参数：userId - 用户ID
     */
    @DeleteMapping("/{notificationId}")
    public ResponseMessage deleteNotification(
            @PathVariable @NotNull Integer notificationId,
            @RequestParam @NotNull Integer userId) {
        try {
            boolean success = notificationService.deleteNotification(notificationId, userId);
            if (success) {
                return ResponseMessage.success("删除成功");
            } else {
                return ResponseMessage.error("删除失败");
            }
        } catch (Exception e) {
            return ResponseMessage.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除通知
     *
     * 批量软删除多条通知
     *
     * 前端请求方式：DELETE
     * 请求URL：localhost:8081/notification/batch
     * 请求体（JSON）：
     * {
     *   "userId": 1,
     *   "notificationIds": [1, 2, 3]
     * }
     */
    @DeleteMapping("/batch")
    public ResponseMessage deleteNotificationBatch(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            List<Integer> notificationIds = (List<Integer>) request.get("notificationIds");

            if (userId == null || notificationIds == null || notificationIds.isEmpty()) {
                return ResponseMessage.error("参数错误");
            }

            int count = notificationService.deleteNotificationBatch(notificationIds, userId);
            return ResponseMessage.success("成功删除" + count + "条通知");
        } catch (Exception e) {
            return ResponseMessage.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 发送系统通知（管理员接口）
     *
     * 这个接口通常只有管理员能够调用
     * 用于发送系统级别的通知，比如维护通知、活动通知等
     *
     * 前端请求方式：POST
     * 请求URL：localhost:8081/notification/system
     * 请求体（JSON）：
     * {
     *   "receiverId": null,     // null表示全体用户，或指定用户ID
     *   "title": "系统维护通知",
     *   "content": "系统将于今晚12点进行维护..."
     * }
     */
    @PostMapping("/system")
    public ResponseMessage sendSystemNotification(@RequestBody Map<String, Object> request) {
        try {
            // 这里应该添加管理员权限验证
            // TODO: 检查当前用户是否为管理员

            Integer receiverId = (Integer) request.get("receiverId");
            String title = (String) request.get("title");
            String content = (String) request.get("content");

            if (title == null || content == null) {
                return ResponseMessage.error("标题和内容不能为空");
            }

            notificationService.sendSystemNotification(receiverId, title, content);
            return ResponseMessage.success("系统通知发送成功");
        } catch (Exception e) {
            return ResponseMessage.error("发送失败: " + e.getMessage());
        }
    }

    /**
     * 清理过期通知（系统接口）
     *
     * 这个接口应该由定时任务调用
     * 用于定期清理已删除超过30天的通知
     *
     * 前端请求方式：POST
     * 请求URL：localhost:8081/notification/clean
     */
    @PostMapping("/clean")
    public ResponseMessage cleanExpiredNotifications() {
        try {
            // TODO: 添加系统权限验证

            notificationService.cleanExpiredNotifications();
            return ResponseMessage.success("过期通知清理完成");
        } catch (Exception e) {
            return ResponseMessage.error("清理失败: " + e.getMessage());
        }
    }
}