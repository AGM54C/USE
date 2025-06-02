package com.example1.demo2.service;

import com.example1.demo2.pojo.dto.NotificationDto;
import java.util.List;
import java.util.Map;

/**
 * 通知服务接口
 * 定义了通知系统需要提供的所有功能
 */
public interface INotificationService {

    /**
     * 发送评论回复通知
     * 当有人回复你的评论时调用
     *
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param commentId 相关评论ID
     * @param content 回复内容
     */
    void sendCommentReplyNotification(Integer senderId, Integer receiverId,
                                      Integer commentId, String content);

    /**
     * 发送点赞通知
     * 当有人点赞你的评论时调用
     *
     * @param senderId 点赞者ID
     * @param receiverId 被点赞者ID
     * @param commentId 被点赞的评论ID
     * @param commentContent 评论内容（用于展示）
     */
    void sendLikeNotification(Integer senderId, Integer receiverId,
                              Integer commentId, String commentContent);

    /**
     * 发送星系新评论通知
     * 通知星系创建者有新评论
     *
     * @param senderId 评论者ID
     * @param galaxyOwnerId 星系创建者ID
     * @param galaxyId 星系ID
     * @param galaxyName 星系名称
     */
    void sendGalaxyCommentNotification(Integer senderId, Integer galaxyOwnerId,
                                       Integer galaxyId, String galaxyName);

    /**
     * 发送系统通知
     * 用于发送系统级别的通知
     *
     * @param receiverId 接收者ID（null表示全体用户）
     * @param title 通知标题
     * @param content 通知内容
     */
    void sendSystemNotification(Integer receiverId, String title, String content);

    /**
     * 获取用户通知列表
     *
     * @param userId 用户ID
     * @param type 通知类型（可选）
     * @param isRead 是否已读（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 通知列表
     */
    List<NotificationDto> getUserNotifications(Integer userId, Integer type,
                                               Integer isRead, int page, int size);

    /**
     * 获取未读通知数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    int getUnreadCount(Integer userId);

    /**
     * 获取分类未读数量
     * 返回每种类型的未读数量
     *
     * @param userId 用户ID
     * @return 类型->数量的映射
     */
    Map<Integer, Integer> getUnreadCountByType(Integer userId);

    /**
     * 标记通知为已读
     *
     * @param notificationId 通知ID
     * @param userId 用户ID（验证权限）
     * @return 是否成功
     */
    boolean markAsRead(Integer notificationId, Integer userId);

    /**
     * 批量标记为已读
     *
     * @param notificationIds 通知ID列表
     * @param userId 用户ID
     * @return 成功标记的数量
     */
    int markAsReadBatch(List<Integer> notificationIds, Integer userId);

    /**
     * 标记所有通知为已读
     *
     * @param userId 用户ID
     * @return 标记的数量
     */
    int markAllAsRead(Integer userId);

    /**
     * 删除通知
     *
     * @param notificationId 通知ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteNotification(Integer notificationId, Integer userId);

    /**
     * 清理过期通知
     * 定时任务调用，清理超过30天的已删除通知
     */
    void cleanExpiredNotifications();
}