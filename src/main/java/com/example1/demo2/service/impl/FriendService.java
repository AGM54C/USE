package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.FriendMapper;
import com.example1.demo2.mapper.UserMapper;
import com.example1.demo2.mapper.PrivateMessageMapper;
import com.example1.demo2.pojo.Friend;
import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.FriendDto;
import com.example1.demo2.pojo.dto.UserDto;
import com.example1.demo2.service.IFriendService;
import com.example1.demo2.service.INotificationService;
import com.example1.demo2.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendService implements IFriendService {

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PrivateMessageMapper messageMapper;

    @Autowired
    private INotificationService notificationService;

    @Override
    @Transactional
    public boolean sendFriendRequest(FriendDto friendDto) {
        // 检查是否已存在好友关系
        int exists = friendMapper.checkFriendshipExists(
                friendDto.getUserId(),
                friendDto.getFriendUserId()
        );

        if (exists > 0) {
            throw new RuntimeException("好友关系已存在或待处理");
        }

        // 不能添加自己为好友
        if (friendDto.getUserId().equals(friendDto.getFriendUserId())) {
            throw new RuntimeException("不能添加自己为好友");
        }

        // 创建好友请求
        Friend friend = new Friend();
        friend.setUserId(friendDto.getUserId());
        friend.setFriendUserId(friendDto.getFriendUserId());
        friend.setStatus(0); // 待确认
        friend.setSource(friendDto.getSource());
        friend.setSourceId(friendDto.getSourceId());
        friend.setRequestMessage(friendDto.getRequestMessage());

        friendMapper.insertFriendRequest(friend);

        // 发送好友请求通知
        sendFriendRequestNotification(friendDto.getUserId(), friendDto.getFriendUserId());

        return true;
    }

    @Override
    @Transactional
    public boolean acceptFriendRequest(Integer friendId, Integer userId) {
        Friend request = friendMapper.getFriendRequestById(friendId);

        if (request == null) {
            throw new RuntimeException("好友请求不存在");
        }

        // 验证是否是接收者
        if (!request.getFriendUserId().equals(userId)) {
            throw new RuntimeException("无权操作此请求");
        }

        // 验证状态
        if (request.getStatus() != 0) {
            throw new RuntimeException("请求已处理");
        }

        // 更新状态为已接受
        friendMapper.updateFriendStatus(friendId, 1);

        // 发送接受通知
        sendFriendAcceptNotification(request.getFriendUserId(), request.getUserId());

        return true;
    }

    @Override
    @Transactional
    public boolean rejectFriendRequest(Integer friendId, Integer userId) {
        Friend request = friendMapper.getFriendRequestById(friendId);

        if (request == null || !request.getFriendUserId().equals(userId)) {
            throw new RuntimeException("无权操作此请求");
        }

        if (request.getStatus() != 0) {
            throw new RuntimeException("请求已处理");
        }

        // 更新状态为已拒绝
        friendMapper.updateFriendStatus(friendId, 2);

        return true;
    }

    @Override
    public List<FriendDto> getFriendList(Integer userId) {
        List<Friend> friends = friendMapper.getFriendList(userId);

        return friends.stream().map(friend -> {
            FriendDto dto = convertToDto(friend);

            // 确定好友ID
            Integer friendUserId = friend.getUserId().equals(userId) ?
                    friend.getFriendUserId() : friend.getUserId();

            // 加载好友信息
            User friendUser = userMapper.findById(friendUserId);
            if (friendUser != null) {
                dto.setFriendUserId(friendUserId);
                dto.setFriendNickname(friendUser.getNickname());
                dto.setFriendAvatar(friendUser.getAvatarUrl());
                dto.setFriendBio(friendUser.getBio());

                // 获取未读消息数
                int unreadCount = messageMapper.getUnreadCount(userId);
                dto.setUnreadCount(unreadCount);

                // TODO: 实现在线状态检查
                dto.setIsOnline(false);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<FriendDto> getPendingRequests(Integer userId) {
        List<Friend> requests = friendMapper.getPendingRequests(userId);

        return requests.stream().map(friend -> {
            FriendDto dto = convertToDto(friend);

            // 加载发送者信息
            User sender = userMapper.findById(friend.getUserId());
            if (sender != null) {
                dto.setFriendNickname(sender.getNickname());
                dto.setFriendAvatar(sender.getAvatarUrl());
                dto.setFriendBio(sender.getBio());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteFriend(Integer userId, Integer friendUserId) {
        // 检查是否为好友
        if (!friendMapper.areFriends(userId, friendUserId)) {
            throw new RuntimeException("不是好友关系");
        }

        // 软删除好友关系
        friendMapper.deleteFriend(userId, friendUserId);

        return true;
    }

    @Override
    public List<UserDto> searchUsersForFriend(String keyword, Integer userId) {
        List<User> users = userMapper.searchUsers(keyword, userId, 20);

        return users.stream()
                .map(ConvertUtil::convertUserToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getGalaxyMembers(Integer galaxyId, Integer userId) {
        // TODO: 实现获取星系成员的逻辑
        return new ArrayList<>();
    }

    @Override
    public boolean areFriends(Integer userId1, Integer userId2) {
        return friendMapper.areFriends(userId1, userId2);
    }

    private void sendFriendRequestNotification(Integer senderId, Integer receiverId) {
        try {
            notificationService.sendSystemNotification(
                    receiverId,
                    "新的好友请求",
                    userMapper.findById(senderId).getNickname() + " 请求添加您为好友"
            );
        } catch (Exception e) {
            // 通知失败不影响主流程
        }
    }

    private void sendFriendAcceptNotification(Integer senderId, Integer receiverId) {
        try {
            notificationService.sendSystemNotification(
                    receiverId,
                    "好友请求已通过",
                    userMapper.findById(senderId).getNickname() + " 已接受您的好友请求"
            );
        } catch (Exception e) {
            // 通知失败不影响主流程
        }
    }

    private FriendDto convertToDto(Friend friend) {
        FriendDto dto = new FriendDto();
        dto.setFriendId(friend.getFriendId());
        dto.setUserId(friend.getUserId());
        dto.setFriendUserId(friend.getFriendUserId());
        dto.setStatus(friend.getStatus());
        dto.setStatusDesc(getStatusDesc(friend.getStatus()));
        dto.setSource(friend.getSource());
        dto.setSourceDesc(getSourceDesc(friend.getSource()));
        dto.setSourceId(friend.getSourceId());
        dto.setRequestMessage(friend.getRequestMessage());
        dto.setCreateTime(friend.getCreateTime());
        dto.setConfirmTime(friend.getConfirmTime());
        dto.setLastChatTime(friend.getLastChatTime());
        return dto;
    }

    private String getStatusDesc(Integer status) {
        switch (status) {
            case 0: return "待确认";
            case 1: return "已接受";
            case 2: return "已拒绝";
            case 3: return "已删除";
            default: return "未知";
        }
    }

    private String getSourceDesc(Integer source) {
        switch (source) {
            case 1: return "搜索添加";
            case 2: return "同星系成员";
            case 3: return "评论互动";
            default: return "其他";
        }
    }
}
