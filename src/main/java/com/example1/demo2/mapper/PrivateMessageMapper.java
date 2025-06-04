package com.example1.demo2.mapper;

import com.example1.demo2.pojo.PrivateMessage;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PrivateMessageMapper {

    /**
     * 发送私信
     */
    @Insert("INSERT INTO tab_private_message(sender_id, receiver_id, content, " +
            "message_type, attachment_url) VALUES(#{sender.userId}, #{receiver.userId}, " +
            "#{content}, #{messageType}, #{attachmentUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "messageId", keyColumn = "message_id")
    void insertMessage(PrivateMessage message);

    /**
     * 获取两个用户之间的聊天记录
     */
    @Select("SELECT * FROM tab_private_message WHERE " +
            "((sender_id = #{userId1} AND receiver_id = #{userId2}) OR " +
            "(sender_id = #{userId2} AND receiver_id = #{userId1})) " +
            "AND status = 0 ORDER BY send_time ASC LIMIT #{offset}, #{size}")
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
     */
    @Update("UPDATE tab_private_message SET is_read = 1, read_time = now() " +
            "WHERE receiver_id = #{receiverId} AND sender_id = #{senderId} " +
            "AND is_read = 0")
    void markMessagesAsRead(@Param("receiverId") Integer receiverId,
                            @Param("senderId") Integer senderId);

    /**
     * 获取未读消息数量
     */
    @Select("SELECT COUNT(*) FROM tab_private_message " +
            "WHERE receiver_id = #{userId} AND is_read = 0 AND status = 0")
    int getUnreadCount(Integer userId);

    /**
     * 撤回消息
     */
    @Update("UPDATE tab_private_message SET status = 1 " +
            "WHERE message_id = #{messageId} AND sender_id = #{senderId} " +
            "AND TIMESTAMPDIFF(MINUTE, send_time, NOW()) <= 2")
    int recallMessage(@Param("messageId") Long messageId,
                      @Param("senderId") Integer senderId);
}