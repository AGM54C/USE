package com.example1.demo2.controller;

import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.PrivateMessageDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.IPrivateMessageService;
import com.example1.demo2.util.ThreadLocalUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 私信控制器
 * 处理私信发送、聊天记录、消息撤回等功能
 */
@RestController
@RequestMapping("/message")
@Validated
public class PrivateMessageController {

    @Autowired
    private IPrivateMessageService messageService;

    @Autowired
    private com.example1.demo2.service.IUserService userService;

    /**
     * 发送私信
     * 前端请求方式：POST
     * 请求URL：localhost:8081/message/send
     * 请求参数（JSON格式）：
     * {
     *   "receiverId": 2,          // 接收者ID（必填）
     *   "content": "你好",        // 消息内容（必填）
     *   "messageType": 0,         // 消息类型：0-文本，1-图片，2-文件（选填）
     *   "attachmentUrl": ""       // 附件URL（选填）
     * }
     * 返回值：发送的消息信息
     */
    @PostMapping("/send")
    public ResponseMessage<PrivateMessageDto> sendMessage(
            @Validated(PrivateMessageDto.Send.class) @RequestBody PrivateMessageDto messageDto) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");
            messageDto.setSenderId(userId);

            PrivateMessageDto sentMessage = messageService.sendMessage(messageDto);
            // 检查用户是否被封禁
            User user = userService.findById(userId);
            if (user == null) {
                return ResponseMessage.error("用户不存在");
            }
            if (user.getStatus() != null && user.getStatus() == 1) {
                return ResponseMessage.error("您的账户已被封禁，无法发表评论");
            }
            return ResponseMessage.success(sentMessage);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 获取聊天记录
     * 前端请求方式：GET
     * 请求URL：localhost:8081/message/history/{friendId}
     * 路径参数：friendId - 好友ID
     * 查询参数：
     *   page - 页码（默认1）
     *   size - 每页大小（默认20）
     * 返回值：聊天记录列表
     */
    @GetMapping("/history/{friendId}")
    public ResponseMessage<List<PrivateMessageDto>> getChatHistory(
            @PathVariable @NotNull Integer friendId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");

            List<PrivateMessageDto> messages = messageService.getChatHistory(
                    userId, friendId, page, size
            );
            return ResponseMessage.success(messages);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 标记消息为已读
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/message/read/{senderId}
     * 路径参数：senderId - 发送者ID
     * 返回值：成功信息
     */
    @PutMapping("/read/{senderId}")
    public ResponseMessage markAsRead(@PathVariable @NotNull Integer senderId) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");

            messageService.markAsRead(userId, senderId);
            return ResponseMessage.success("消息已标记为已读");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 获取未读消息数
     * 前端请求方式：GET
     * 请求URL：localhost:8081/message/unread/count
     * 返回值：未读消息数量
     */
    @GetMapping("/unread/count")
    public ResponseMessage<Integer> getUnreadCount() {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");

            int count = messageService.getUnreadCount(userId);
            return ResponseMessage.success(count);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 撤回消息（2分钟内）
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/message/recall/{messageId}
     * 路径参数：messageId - 消息ID
     * 返回值：成功信息
     */
    @PutMapping("/recall/{messageId}")
    public ResponseMessage recallMessage(@PathVariable @NotNull Long messageId) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");

            boolean success = messageService.recallMessage(messageId, userId);
            if (success) {
                return ResponseMessage.success("消息已撤回");
            }
            return ResponseMessage.error("撤回失败，可能已超过2分钟");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
}
