package com.example1.demo2.controller;

import com.example1.demo2.pojo.dto.FriendDto;
import com.example1.demo2.pojo.dto.UserDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.IFriendService;
import com.example1.demo2.util.ThreadLocalUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 好友管理控制器
 * 处理好友请求、好友列表、好友搜索等功能
 */
@RestController
@RequestMapping("/friend")
@Validated
public class FriendController {

    @Autowired
    private IFriendService friendService;

    /**
     * 发送好友请求
     * 前端请求方式：POST
     * 请求URL：localhost:8081/friend/request
     * 请求参数（JSON格式）：
     * {
     *   "userId": 1,           // 当前用户ID（必填）
     *   "friendUserId": 2,        // 要添加的好友ID（必填）
     *   "source": 1,              // 来源：1-搜索，2-同星系，3-评论互动（必填）
     *   "sourceId": "123",        // 来源ID（选填）
     *   "requestMessage": "你好"   // 申请备注（选填）
     * }
     * 返回值：成功返回成功信息，失败返回错误信息
     */
    @PostMapping("/request")
    public ResponseMessage sendFriendRequest(
            @Validated(FriendDto.SendRequest.class) @RequestBody FriendDto friendDto) {
        try {
            // 获取当前用户ID
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");
            friendDto.setUserId(userId);

            boolean success = friendService.sendFriendRequest(friendDto);
            if (success) {
                return ResponseMessage.success("好友请求已发送");
            }
            return ResponseMessage.error("发送失败");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 接受好友请求
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/friend/accept/{friendId}
     * 路径参数：friendId - 好友关系ID
     * 返回值：成功返回成功信息
     */
    @PutMapping("/accept/{friendId}")
    public ResponseMessage acceptFriendRequest(@PathVariable @NotNull Integer friendId) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");

            boolean success = friendService.acceptFriendRequest(friendId, userId);
            if (success) {
                return ResponseMessage.success("已接受好友请求");
            }
            return ResponseMessage.error("操作失败");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 拒绝好友请求
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/friend/reject/{friendId}
     * 路径参数：friendId - 好友关系ID
     * 返回值：成功返回成功信息
     */
    @PutMapping("/reject/{friendId}")
    public ResponseMessage rejectFriendRequest(@PathVariable @NotNull Integer friendId) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");

            boolean success = friendService.rejectFriendRequest(friendId, userId);
            if (success) {
                return ResponseMessage.success("已拒绝好友请求");
            }
            return ResponseMessage.error("操作失败");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 获取好友列表
     * 前端请求方式：GET
     * 请求URL：localhost:8081/friend/list
     * 返回值：好友列表
     */
    @GetMapping("/list")
    public ResponseMessage<List<FriendDto>> getFriendList() {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");

            List<FriendDto> friends = friendService.getFriendList(userId);
            return ResponseMessage.success(friends);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 获取待处理的好友请求
     * 前端请求方式：GET
     * 请求URL：localhost:8081/friend/pending
     * 返回值：待处理的好友请求列表
     */
    @GetMapping("/pending")
    public ResponseMessage<List<FriendDto>> getPendingRequests() {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");

            List<FriendDto> requests = friendService.getPendingRequests(userId);
            return ResponseMessage.success(requests);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 删除好友
     * 前端请求方式：DELETE
     * 请求URL：localhost:8081/friend/delete/{friendUserId}
     * 路径参数：friendUserId - 好友的用户ID
     * 返回值：成功返回成功信息
     */
    @DeleteMapping("/delete/{friendUserId}")
    public ResponseMessage deleteFriend(@PathVariable @NotNull Integer friendUserId) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");

            boolean success = friendService.deleteFriend(userId, friendUserId);
            if (success) {
                return ResponseMessage.success("已删除好友");
            }
            return ResponseMessage.error("删除失败");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 搜索用户（用于添加好友）
     * 前端请求方式：GET
     * 请求URL：localhost:8081/friend/search
     * 查询参数：keyword - 搜索关键词（昵称或邮箱）
     * 返回值：匹配的用户列表
     */
    @GetMapping("/search")
    public ResponseMessage<List<UserDto>> searchUsers(
            @RequestParam(required = false) String keyword) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");

            List<UserDto> users = friendService.searchUsersForFriend(keyword, userId);
            return ResponseMessage.success(users);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 获取同星系成员（可添加为好友）
     * 前端请求方式：GET
     * 请求URL：localhost:8081/friend/galaxy/{galaxyId}/members
     * 路径参数：galaxyId - 星系ID
     * 返回值：星系成员列表
     */
    @GetMapping("/galaxy/{galaxyId}/members")
    public ResponseMessage<List<UserDto>> getGalaxyMembers(
            @PathVariable @NotNull Integer galaxyId) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");

            List<UserDto> members = friendService.getGalaxyMembers(galaxyId, userId);
            return ResponseMessage.success(members);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
}