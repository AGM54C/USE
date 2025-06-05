package com.example1.demo2.controller;

import com.example1.demo2.pojo.dto.GalaxyCommentDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.IGalaxyCommentService;
import com.example1.demo2.service.IRewardService;
import com.example1.demo2.service.impl.RewardService;
import com.example1.demo2.util.ThreadLocalUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/galaxy/comment")  // localhost:8081/galaxy/comment/**
@Validated
public class GalaxyCommentController {

    @Autowired
    private IGalaxyCommentService commentService;

    @Autowired
    private IRewardService rewardService;

    private static final Logger logger = LoggerFactory.getLogger(RewardService.class);

    /**
     * 发布评论接口
     * 前端请求方式：POST
     * 请求URL：localhost:8081/galaxy/comment/publish
     * 请求参数（JSON格式）：
     * {
     *   "userId": 1,                  // 用户ID（必填）
     *   "galaxyId": 1,                // 星系ID（必填）
     *   "content": "这是一条评论",      // 评论内容（必填）
     *   "parentId": 0,                // 父评论ID（选填，默认0表示一级评论）
     *   "replyToUserId": null         // 被回复用户ID（选填）
     * }
     * 返回值：成功返回评论信息，失败返回错误信息
     */
    @PostMapping("/publish")
    @Transactional
    public ResponseMessage publishComment(@Validated(GalaxyCommentDto.Create.class)
                                          @RequestBody GalaxyCommentDto commentDto) {
        try {
            // 使用ThreadLocal获取当前用户ID
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");
            commentDto.setUserId(userId);

            // 发布评论
            GalaxyCommentDto result = commentService.publishComment(commentDto);

            // 奖励知识星云值
            try {
                Integer newDust = rewardService.rewardForComment(userId, true);
                // 更新ThreadLocal中的用户信息
                logger.info("用户 {} 发表星系评论，获得知识星云值，当前值: {}", userId, newDust);
            } catch (Exception e) {
                logger.error("奖励知识星云值失败: {}", e.getMessage());
            }

            return ResponseMessage.success(result);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 获取星系评论列表接口
     * 前端请求方式：POST
     * 请求URL：localhost:8081/galaxy/comment/list/{galaxyId}
     * 路径参数：galaxyId - 星系ID
     * 查询参数：
     *   page - 页码（默认1）
     *   size - 每页大小（默认20）
     *   userId - 当前用户ID（用于判断是否已点赞）
     * 返回值：评论列表（分页，树形结构）
     */
    @PostMapping("/list/{galaxyId}")
    public ResponseMessage getCommentList(@PathVariable @NotNull Integer galaxyId,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "20") int size,
                                          @RequestParam(required = false) Integer userId) {
        try {
            List<GalaxyCommentDto> comments = commentService.getCommentList(galaxyId, page, size, userId);
            return ResponseMessage.success(comments);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 点赞/取消点赞接口
     * 前端请求方式：POST
     * 请求URL：localhost:8081/galaxy/comment/like
     * 请求参数（JSON格式）：
     * {
     *   "userId": 1,         // 用户ID（必填）
     *   "commentId": 1       // 评论ID（必填）
     * }
     * 返回值：成功返回操作结果（true-点赞，false-取消点赞）
     */
    @PostMapping("/like")
    public ResponseMessage toggleLike(@RequestParam @NotNull Integer userId,
                                      @RequestParam @NotNull Integer commentId) {
        try {
            boolean isLiked = commentService.toggleLike(userId, commentId);
            return ResponseMessage.success(isLiked ? "点赞成功" : "取消点赞");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 删除评论接口
     * 前端请求方式：DELETE
     * 请求URL：localhost:8081/galaxy/comment/delete/{commentId}
     * 路径参数：commentId - 评论ID
     * 查询参数：userId - 用户ID（用于权限验证）
     * 返回值：成功返回成功信息，失败返回错误信息
     */
    @DeleteMapping("/delete/{commentId}")
    public ResponseMessage deleteComment(@PathVariable @NotNull Integer commentId,
                                         @RequestParam @NotNull Integer userId) {
        try {
            commentService.deleteComment(commentId, userId);
            return ResponseMessage.success("评论删除成功");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 获取评论详情接口（包含所有回复）
     * 前端请求方式：GET
     * 请求URL：localhost:8081/galaxy/comment/detail/{commentId}
     * 路径参数：commentId - 评论ID
     * 查询参数：userId - 当前用户ID（用于判断是否已点赞）
     * 返回值：评论详情及其所有回复
     */
    @GetMapping("/detail/{commentId}")
    public ResponseMessage getCommentDetail(@PathVariable @NotNull Integer commentId,
                                            @RequestParam(required = false) Integer userId) {
        try {
            GalaxyCommentDto comment = commentService.getCommentDetail(commentId, userId);
            return ResponseMessage.success(comment);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
}