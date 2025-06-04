package com.example1.demo2.service;

import com.example1.demo2.pojo.dto.PrivateMessageDto;
import java.util.List;

public interface IPrivateMessageService {

    /**
     * 发送私信
     * @param messageDto 消息信息
     * @return 发送的消息
     */
    PrivateMessageDto sendMessage(PrivateMessageDto messageDto);

    /**
     * 获取聊天记录
     * @param userId1 用户1
     * @param userId2 用户2
     * @param page 页码
     * @param size 每页大小
     * @return 聊天记录
     */
    List<PrivateMessageDto> getChatHistory(Integer userId1, Integer userId2,
                                           int page, int size);

    /**
     * 标记消息为已读
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     */
    void markAsRead(Integer receiverId, Integer senderId);

    /**
     * 获取未读消息数
     * @param userId 用户ID
     * @return 未读数
     */
    int getUnreadCount(Integer userId);

    /**
     * 撤回消息
     * @param messageId 消息ID
     * @param senderId 发送者ID
     * @return 是否成功
     */
    boolean recallMessage(Long messageId, Integer senderId);
}
