package com.example1.demo2.service;

import com.example1.demo2.pojo.dto.NotificationDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.util.List;
import java.util.Map;

/**
 * 通知服务接口
 * 定义了通知系统需要提供的所有功能
 * 支持7种通知类型：
 * 1. 星系评论回复
 * 2. 星系评论点赞
 * 3. 星系新评论
 * 4. 星球评论回复
 * 5. 星球评论点赞
 * 6. 星球新评论
 * 7. 系统通知
 * * 8. 星系管理员任命通知（兼容旧接口）
 */
public interface INotificationService {

    // ==================== 星系相关通知 ====================

    /**
     * 发送星系评论回复通知
     * 当有人回复星系评论时调用
     *
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param commentId 相关评论ID
     * @param content 回复内容
     */
    void sendGalaxyCommentReplyNotification(Integer senderId, Integer receiverId,
                                            Integer commentId, String content);

    /**
     * 发送星系评论点赞通知
     * 当有人点赞星系评论时调用
     *
     * @param senderId 点赞者ID
     * @param receiverId 被点赞者ID
     * @param commentId 被点赞的评论ID
     * @param commentContent 评论内容（用于展示）
     */
    void sendGalaxyCommentLikeNotification(Integer senderId, Integer receiverId,
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
    void sendGalaxyNewCommentNotification(Integer senderId, Integer galaxyOwnerId,
                                          Integer galaxyId, String galaxyName);

    // ==================== 星球相关通知 ====================

    /**
     * 发送星球评论回复通知
     * 当有人回复星球评论时调用
     *
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param commentId 相关评论ID
     * @param content 回复内容
     */
    void sendPlanetCommentReplyNotification(Integer senderId, Integer receiverId,
                                            Integer commentId, String content);

    /**
     * 发送星球评论点赞通知
     * 当有人点赞星球评论时调用
     *
     * @param senderId 点赞者ID
     * @param receiverId 被点赞者ID
     * @param commentId 被点赞的评论ID
     * @param commentContent 评论内容（用于展示）
     */
    void sendPlanetCommentLikeNotification(Integer senderId, Integer receiverId,
                                           Integer commentId, String commentContent);

    /**
     * 发送星球新评论通知
     * 通知星球创建者有新评论
     *
     * @param senderId 评论者ID
     * @param planetOwnerId 星球所有者ID
     * @param planetId 星球ID
     * @param planetName 星球名称
     */
    void sendPlanetNewCommentNotification(Integer senderId, Integer planetOwnerId,
                                          String planetId, String planetName);

    // ==================== 系统通知 ====================

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
     * 发送星系管理员任命通知
     * 当用户被任命为星系管理员时调用
     *
     * @param receiverId 接收者ID
     * @param senderId 任命者ID
     * @param senderName 任命者名称
     * @param galaxyId 星系ID
     * @param galaxyName 星系名称
     */
    void sendGalaxyAdminNotification(Integer receiverId, Integer senderId,
                                     String senderName, Integer galaxyId, String galaxyName);

    // ==================== 通知查询和管理 ====================

    /**
     * 获取用户通知列表
     *
     * @param userId 用户ID
     * @param type 通知类型（可选，1-7）
     * @param isRead 是否已读（可选，0未读1已读）
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
     * 标记特定类型的所有通知为已读
     *
     * @param userId 用户ID
     * @param type 通知类型
     * @return 标记的数量
     */
    int markTypeAsRead(Integer userId, Integer type);

    /**
     * 删除通知
     *
     * @param notificationId 通知ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteNotification(Integer notificationId, Integer userId);

    /**
     * 批量删除通知
     *
     * @param notificationIds 通知ID列表
     * @param userId 用户ID
     * @return 删除的数量
     */
    int deleteNotificationBatch(List<Integer> notificationIds, Integer userId);

    /**
     * 清理过期通知
     * 定时任务调用，清理超过30天的已删除通知
     */
    void cleanExpiredNotifications();

    // ==================== 兼容旧接口（已废弃，建议使用新接口） ====================

    /**
     * @deprecated 使用 sendGalaxyCommentReplyNotification 或 sendPlanetCommentReplyNotification
     */
    @Deprecated
    default void sendCommentReplyNotification(Integer senderId, Integer receiverId,
                                              Integer commentId, String content) {
        sendGalaxyCommentReplyNotification(senderId, receiverId, commentId, content);
    }

    /**
     * @deprecated 使用 sendGalaxyCommentLikeNotification 或 sendPlanetCommentLikeNotification
     */
    @Deprecated
    default void sendLikeNotification(Integer senderId, Integer receiverId,
                                      Integer commentId, String commentContent) {
        sendGalaxyCommentLikeNotification(senderId, receiverId, commentId, commentContent);
    }

    /**
     * @deprecated 使用 sendGalaxyNewCommentNotification
     */
    @Deprecated
    default void sendGalaxyCommentNotification(Integer senderId, Integer galaxyOwnerId,
                                               Integer galaxyId, String galaxyName) {
        sendGalaxyNewCommentNotification(senderId, galaxyOwnerId, galaxyId, galaxyName);
    }

    /**
     * @deprecated 使用 sendPlanetNewCommentNotification
     */
    @Deprecated
    default void sendPlanetCommentNotification(Integer senderId, Integer planetOwnerId,
                                               String planetId, String planetName) {
        sendPlanetNewCommentNotification(senderId, planetOwnerId, planetId, planetName);
    }

    void sendNotification(@NotNull Integer userId,@NotNull Integer receiverId,@Null String content ,@NotNull Integer type);
}