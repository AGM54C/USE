package com.example1.demo2.mapper;

import com.example1.demo2.pojo.Friend;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface FriendMapper {

    /**
     * 添加好友请求
     */
    @Insert("INSERT INTO tab_friend(user_id, friend_user_id, status, source, source_id, request_message, create_time) " +
            "VALUES(#{userId}, #{friendUserId}, #{status}, #{source}, #{sourceId}, #{requestMessage}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "friendId", keyColumn = "friend_id")
    void insertFriendRequest(Friend friend);

    /**
     * 检查是否已存在好友关系（双向检查）
     */
    @Select("SELECT COUNT(*) FROM tab_friend WHERE " +
            "((user_id = #{userId} AND friend_user_id = #{friendUserId}) OR " +
            "(user_id = #{friendUserId} AND friend_user_id = #{userId})) " +
            "AND status IN (0, 1)")
    int checkFriendshipExists(@Param("userId") Integer userId,
                              @Param("friendUserId") Integer friendUserId);

    /**
     * 获取好友请求
     */
    @Select("SELECT * FROM tab_friend WHERE friend_id = #{friendId}")
    Friend getFriendRequestById(Integer friendId);

    /**
     * 更新好友请求状态
     */
    @Update("UPDATE tab_friend SET status = #{status}, confirm_time = now() " +
            "WHERE friend_id = #{friendId}")
    void updateFriendStatus(@Param("friendId") Integer friendId,
                            @Param("status") Integer status);

    /**
     * 获取用户的好友列表
     */
    @Select("SELECT * FROM tab_friend WHERE " +
            "(user_id = #{userId} OR friend_user_id = #{userId}) " +
            "AND status = 1 ORDER BY confirm_time DESC")
    List<Friend> getFriendList(Integer userId);

    /**
     * 获取待处理的好友请求
     */
    @Select("SELECT * FROM tab_friend WHERE friend_user_id = #{userId} " +
            "AND status = 0 ORDER BY create_time DESC")
    List<Friend> getPendingRequests(Integer userId);

    /**
     * 删除好友关系
     */
    @Update("UPDATE tab_friend SET status = 3 WHERE " +
            "((user_id = #{userId} AND friend_user_id = #{friendUserId}) OR " +
            "(user_id = #{friendUserId} AND friend_user_id = #{userId})) " +
            "AND status = 1")
    void deleteFriend(@Param("userId") Integer userId,
                      @Param("friendUserId") Integer friendUserId);

    /**
     * 检查两个用户是否为好友
     */
    @Select("SELECT COUNT(*) > 0 FROM tab_friend WHERE " +
            "((user_id = #{userId} AND friend_user_id = #{friendUserId}) OR " +
            "(user_id = #{friendUserId} AND friend_user_id = #{userId})) " +
            "AND status = 1")
    boolean areFriends(@Param("userId") Integer userId,
                       @Param("friendUserId") Integer friendUserId);

    /**
     * 更新最后聊天时间
     */
    @Update("UPDATE tab_friend SET last_chat_time = now() WHERE " +
            "((user_id = #{userId} AND friend_user_id = #{friendUserId}) OR " +
            "(user_id = #{friendUserId} AND friend_user_id = #{userId})) " +
            "AND status = 1")
    void updateLastChatTime(@Param("userId") Integer userId,
                            @Param("friendUserId") Integer friendUserId);

    // ==================== 级联删除相关方法 ====================

    /**
     * 删除用户的所有好友关系记录（硬删除）
     * 包括用户作为请求方和被请求方的所有记录
     */
    @Delete("DELETE FROM tab_friend WHERE user_id = #{userId} OR friend_user_id = #{userId}")
    void deleteAllFriendshipsByUserId(Integer userId);

    /**
     * 软删除用户的所有好友关系
     * 将状态设置为已删除（status = 3）
     */
    @Update("UPDATE tab_friend SET status = 3 WHERE user_id = #{userId} OR friend_user_id = #{userId}")
    void softDeleteAllFriendshipsByUserId(Integer userId);
}