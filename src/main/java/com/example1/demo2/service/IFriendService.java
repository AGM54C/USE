package com.example1.demo2.service;

import com.example1.demo2.pojo.dto.FriendDto;
import com.example1.demo2.pojo.dto.UserDto;
import java.util.List;

public interface IFriendService {

    /**
     * 发送好友请求
     * @param friendDto 好友请求信息
     * @return 是否成功
     */
    boolean sendFriendRequest(FriendDto friendDto);

    /**
     * 接受好友请求
     * @param friendId 好友关系ID
     * @param userId 当前用户ID
     * @return 是否成功
     */
    boolean acceptFriendRequest(Integer friendId, Integer userId);

    /**
     * 拒绝好友请求
     * @param friendId 好友关系ID
     * @param userId 当前用户ID
     * @return 是否成功
     */
    boolean rejectFriendRequest(Integer friendId, Integer userId);

    /**
     * 获取好友列表
     * @param userId 用户ID
     * @return 好友列表
     */
    List<FriendDto> getFriendList(Integer userId);

    /**
     * 获取待处理的好友请求
     * @param userId 用户ID
     * @return 好友请求列表
     */
    List<FriendDto> getPendingRequests(Integer userId);

    /**
     * 删除好友
     * @param userId 用户ID
     * @param friendUserId 好友ID
     * @return 是否成功
     */
    boolean deleteFriend(Integer userId, Integer friendUserId);

    /**
     * 搜索用户（用于添加好友）
     * @param keyword 搜索关键词
     * @param userId 当前用户ID
     * @return 用户列表
     */
    List<UserDto> searchUsersForFriend(String keyword, Integer userId);

    /**
     * 获取同星系的用户（可添加为好友）
     * @param galaxyId 星系ID
     * @param userId 当前用户ID
     * @return 用户列表
     */
    List<UserDto> getGalaxyMembers(Integer galaxyId, Integer userId);

    /**
     * 检查两个用户是否为好友
     * @param userId1 用户1
     * @param userId2 用户2
     * @return 是否为好友
     */
    boolean areFriends(Integer userId1, Integer userId2);
}
