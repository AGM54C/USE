package com.example1.demo2.mapper;

import com.example1.demo2.pojo.PrivateMessage;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PrivateMessageMapper {

    /**
     * 发送私信
     * 修正：添加了 send_time 字段，使用 NOW() 函数自动设置当前时间
     */
    @Insert("INSERT INTO tab_private_message(sender_id, receiver_id, content, " +
            "message_type, attachment_url, send_time) VALUES(#{sender.userId}, #{receiver.userId}, " +
            "#{content}, #{messageType}, #{attachmentUrl}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "messageId", keyColumn = "message_id")
    void insertMessage(PrivateMessage message);

    /**
     * 获取两个用户之间的聊天记录
     * 这个方法看起来没问题，保持不变
     */
    @Select("SELECT * FROM tab_private_message WHERE " +
            "((sender_id = #{userId1} AND receiver_id = #{userId2}) OR " +
            "(sender_id = #{userId2} AND receiver_id = #{userId1})) " +
            "AND status = 0 ORDER BY send_time DESC LIMIT #{offset}, #{size}")
    @Results({
            @Result(property = "messageId", column = "message_id"),
            @Result(property = "sender", column = "sender_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.findById")),
            @Result(property = "receiver", column = "receiver_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.findById")),
            @Result(property = "sendTime", column = "send_time"),
            @Result(property = "readTime", column = "read_time")
    })
    List<PrivateMessage> getChatHistory(@Param("userId1") Integer userId1,
                                        @Param("userId2") Integer userId2,
                                        @Param("offset") int offset,
                                        @Param("size") int size);

    /**
     * 标记消息为已读
     * 使用 NOW() 设置已读时间，这个已经是正确的
     */
    @Update("UPDATE tab_private_message SET is_read = 1, read_time = NOW() " +
            "WHERE receiver_id = #{receiverId} AND sender_id = #{senderId} " +
            "AND is_read = 0")
    void markMessagesAsRead(@Param("receiverId") Integer receiverId,
                            @Param("senderId") Integer senderId);

    /**
     * 获取未读消息数量
     * 这个查询没问题
     */
    @Select("SELECT COUNT(*) FROM tab_private_message " +
            "WHERE receiver_id = #{userId} AND is_read = 0 AND status = 0")
    int getUnreadCount(Integer userId);

    /**
     * 撤回消息
     * 注意：我将 status = 1 改为 status = 2，因为根据你的前端代码，
     * status = 2 表示已撤回的消息
     */
    @Update("UPDATE tab_private_message SET status = 2 " +
            "WHERE message_id = #{messageId} AND sender_id = #{senderId} " +
            "AND TIMESTAMPDIFF(MINUTE, send_time, NOW()) <= 2")
    int recallMessage(@Param("messageId") Long messageId,
                      @Param("senderId") Integer senderId);
}