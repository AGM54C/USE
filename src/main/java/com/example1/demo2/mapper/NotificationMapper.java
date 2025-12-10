package com.example1.demo2.mapper;

import com.example1.demo2.pojo.Notification;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Date;
import java.util.Map;

/**
 * 通知Mapper接口 - 负责与数据库交互
 * 支持完整的7种通知类型和5种目标类型
 */
@Mapper
public interface NotificationMapper {

    /**
     * 插入一条新通知
     * 注意：target_id已改为VARCHAR类型以支持不同格式的ID
     */
    @Insert("INSERT INTO tab_notification(receiver_id, sender_id, type, title, content, " +
            "target_type, target_id, extra_data, create_time) " +
            "VALUES(#{receiver.userId}, #{sender.userId}, #{type}, #{title}, #{content}, " +
            "#{targetType}, #{targetId}, #{extraData}, now())")
    @Options(useGeneratedKeys = true, keyProperty = "notificationId", keyColumn = "notification_id")
    void insertNotification(Notification notification);

    /**
     * 批量插入通知
     * 用于一次性发送多条通知，比如群发功能
     */
    @Insert("<script>" +
            "INSERT INTO tab_notification(receiver_id, sender_id, type, title, content, " +
            "target_type, target_id, create_time) VALUES " +
            "<foreach collection='notifications' item='n' separator=','>" +
            "(#{n.receiver.userId}, " +
            "<if test='n.sender != null'>#{n.sender.userId}</if>" +
            "<if test='n.sender == null'>NULL</if>, " +
            "#{n.type}, #{n.title}, #{n.content}, " +
            "#{n.targetType}, #{n.targetId}, now())" +
            "</foreach>" +
            "</script>")
    void insertNotificationBatch(@Param("notifications") List<Notification> notifications);

    /**
     * 根据ID查询通知详情
     * 就像根据快递单号查找包裹
     */
    @Select("SELECT * FROM tab_notification WHERE notification_id = #{notificationId}")
    @Results({
            @Result(property = "notificationId", column = "notification_id"),
            @Result(property = "receiver", column = "receiver_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.findById")),
            @Result(property = "sender", column = "sender_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.findById")),
            @Result(property = "targetType", column = "target_type"),
            @Result(property = "targetId", column = "target_id"),
            @Result(property = "isRead", column = "is_read"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "readTime", column = "read_time"),
            @Result(property = "extraData", column = "extra_data")
    })
    Notification getNotificationById(Integer notificationId);

    /**
     * 查询用户的通知列表（分页）
     * 支持按通知类型和已读状态筛选
     */
    @Select("<script>" +
            "SELECT * FROM tab_notification " +
            "WHERE receiver_id = #{receiverId} AND status = 0 " +
            "<if test='type != null'>AND type = #{type}</if> " +
            "<if test='isRead != null'>AND is_read = #{isRead}</if> " +
            "ORDER BY create_time DESC " +
            "LIMIT #{offset}, #{size}" +
            "</script>")
    @Results({
            @Result(property = "notificationId", column = "notification_id"),
            @Result(property = "receiver", column = "receiver_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.findById")),
            @Result(property = "sender", column = "sender_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.findById")),
            @Result(property = "targetType", column = "target_type"),
            @Result(property = "targetId", column = "target_id"),
            @Result(property = "isRead", column = "is_read"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "readTime", column = "read_time"),
            @Result(property = "extraData", column = "extra_data")
    })
    List<Notification> getNotificationsByUser(@Param("receiverId") Integer receiverId,
                                              @Param("type") Integer type,
                                              @Param("isRead") Integer isRead,
                                              @Param("offset") int offset,
                                              @Param("size") int size);

    /**
     * 统计用户未读通知数量
     * 支持按通知类型统计
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM tab_notification " +
            "WHERE receiver_id = #{receiverId} AND is_read = 0 AND status = 0 " +
            "<if test='type != null'>AND type = #{type}</if>" +
            "</script>")
    int countUnreadNotifications(@Param("receiverId") Integer receiverId,
                                 @Param("type") Integer type);

    /**
     * 按类型统计未读通知数量
     * 返回每种类型的未读数量
     */
    @Select("SELECT type, COUNT(*) as count FROM tab_notification " +
            "WHERE receiver_id = #{receiverId} AND is_read = 0 AND status = 0 " +
            "GROUP BY type")
    @MapKey("type")
    List<Map<String, Object>> countUnreadByType(Integer receiverId);

    /**
     * 标记通知为已读
     */
    @Update("UPDATE tab_notification SET is_read = 1, read_time = now() " +
            "WHERE notification_id = #{notificationId} AND receiver_id = #{receiverId}")
    int markAsRead(@Param("notificationId") Integer notificationId,
                   @Param("receiverId") Integer receiverId);

    /**
     * 批量标记为已读
     */
    @Update("<script>" +
            "UPDATE tab_notification SET is_read = 1, read_time = now() " +
            "WHERE receiver_id = #{receiverId} AND notification_id IN " +
            "<foreach collection='notificationIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int markAsReadBatch(@Param("receiverId") Integer receiverId,
                        @Param("notificationIds") List<Integer> notificationIds);

    /**
     * 标记用户所有通知为已读
     */
    @Update("UPDATE tab_notification SET is_read = 1, read_time = now() " +
            "WHERE receiver_id = #{receiverId} AND is_read = 0")
    int markAllAsRead(Integer receiverId);

    /**
     * 标记特定类型的所有通知为已读
     */
    @Update("UPDATE tab_notification SET is_read = 1, read_time = now() " +
            "WHERE receiver_id = #{receiverId} AND type = #{type} AND is_read = 0")
    int markTypeAsRead(@Param("receiverId") Integer receiverId, @Param("type") Integer type);

    /**
     * 软删除通知
     */
    @Update("UPDATE tab_notification SET status = 1 " +
            "WHERE notification_id = #{notificationId} AND receiver_id = #{receiverId}")
    int deleteNotification(@Param("notificationId") Integer notificationId,
                           @Param("receiverId") Integer receiverId);

    /**
     * 批量删除通知
     */
    @Update("<script>" +
            "UPDATE tab_notification SET status = 1 " +
            "WHERE receiver_id = #{receiverId} AND notification_id IN " +
            "<foreach collection='notificationIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int deleteNotificationBatch(@Param("receiverId") Integer receiverId,
                                @Param("notificationIds") List<Integer> notificationIds);

    /**
     * 清理过期通知
     * 定期清理超过指定天数的已删除通知
     */
    @Delete("DELETE FROM tab_notification " +
            "WHERE create_time < DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "AND status = 1")
    int cleanExpiredNotifications(@Param("days") int days);

    /**
     * 检查是否已发送过相同的通知
     * 避免重复通知，比如短时间内多次点赞同一评论
     */
    @Select("SELECT COUNT(*) FROM tab_notification " +
            "WHERE receiver_id = #{receiverId} AND sender_id = #{senderId} " +
            "AND type = #{type} AND target_id = #{targetId} " +
            "AND create_time > DATE_SUB(NOW(), INTERVAL #{minutes} MINUTE)")
    int checkDuplicateNotification(@Param("receiverId") Integer receiverId,
                                   @Param("senderId") Integer senderId,
                                   @Param("type") Integer type,
                                   @Param("targetId") String targetId,
                                   @Param("minutes") int minutes);

    /**
     * 获取特定目标的通知
     * 比如获取某个评论的所有相关通知
     */
    @Select("SELECT * FROM tab_notification " +
            "WHERE target_type = #{targetType} AND target_id = #{targetId} " +
            "ORDER BY create_time DESC")
    @Results({
            @Result(property = "notificationId", column = "notification_id"),
            @Result(property = "receiver", column = "receiver_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.findById")),
            @Result(property = "sender", column = "sender_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.findById")),
            @Result(property = "targetType", column = "target_type"),
            @Result(property = "targetId", column = "target_id"),
            @Result(property = "isRead", column = "is_read"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "readTime", column = "read_time")
    })
    List<Notification> getNotificationsByTarget(@Param("targetType") Integer targetType,
                                                @Param("targetId") String targetId);

    /**
     * 统计用户发送的通知数量
     * 用于防止恶意刷通知
     */
    @Select("SELECT COUNT(*) FROM tab_notification " +
            "WHERE sender_id = #{senderId} " +
            "AND create_time > DATE_SUB(NOW(), INTERVAL #{hours} HOUR)")
    int countSentNotifications(@Param("senderId") Integer senderId,
                               @Param("hours") int hours);

    // ==================== 级联删除相关方法 ====================

    /**
     * 删除用户接收和发送的所有通知（硬删除）
     * 用于用户注销时清理数据
     */
    @Delete("DELETE FROM tab_notification WHERE receiver_id = #{userId} OR sender_id = #{userId}")
    void deleteNotificationsByUserId(Integer userId);

    /**
     * 软删除用户接收的所有通知
     * 将状态设置为已删除（status = 1）
     */
    @Update("UPDATE tab_notification SET status = 1 WHERE receiver_id = #{userId}")
    void softDeleteNotificationsByReceiverId(Integer userId);

    /**
     * 删除指定目标类型和目标ID的所有通知
     * 用于删除评论、星球、星系时同时删除相关通知
     *
     * 目标类型说明：
     * 1 - 星系 (Galaxy)
     * 2 - 星球 (Planet)
     * 3 - 星系评论 (GalaxyComment)
     * 4 - 星球评论 (PlanetComment)
     * 5 - 用户 (User)
     */
    @Delete("DELETE FROM tab_notification WHERE target_type = #{targetType} AND target_id = #{targetId}")
    void deleteNotificationsByTarget(@Param("targetType") Integer targetType, @Param("targetId") String targetId);
}