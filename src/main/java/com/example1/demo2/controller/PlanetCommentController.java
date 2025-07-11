package com.example1.demo2.controller;

import com.example1.demo2.mapper.PlanetCommentMapper;
import com.example1.demo2.pojo.dto.PlanetCommentDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.IPlanetCommentService;
import com.example1.demo2.service.IRewardService;
import com.example1.demo2.service.IUserService;
import com.example1.demo2.service.impl.RewardService;
import com.example1.demo2.util.ThreadLocalUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example1.demo2.pojo.User;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planet/comment")  // localhost:8081/planet/comment/**
@Validated
public class PlanetCommentController {

    @Autowired
    private IPlanetCommentService commentService;

    @Autowired
    private IRewardService rewardService;

    @Autowired
    private IUserService userService;

    private static final Logger logger = LoggerFactory.getLogger(RewardService.class);
    @Autowired
    private PlanetCommentMapper planetCommentMapper;

    /**
     * 发布评论接口
     * 前端请求方式：POST
     * 请求URL：localhost:8081/planet/comment/publish
     * 请求参数（JSON格式）：
     * {
     *   "userId": 1,                  // 用户ID（必填）
     *   "planetId": "1",              // 星球ID（必填，修改为String类型）
     *   "content": "这是一条评论",      // 评论内容（必填）
     *   "parentId": 0,                // 父评论ID（选填，默认0表示一级评论）
     *   "replyToUserId": null         // 被回复用户ID（选填）
     * }
     * 返回值：成功返回评论id，失败返回错误信息
     */
    @PostMapping("/publish")
    @Transactional
    public ResponseMessage publishComment(@Validated(PlanetCommentDto.Create.class)
                                          @RequestBody PlanetCommentDto commentDto) {
        try {
            // 使用ThreadLocal获取当前用户ID
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");
            commentDto.setUserId(userId);

            // 发布评论
            PlanetCommentDto result = commentService.publishComment(commentDto);
            // 检查用户是否被封禁
            User user = userService.findById(userId);
            if (user == null) {
                return ResponseMessage.error("用户不存在");
            }
            if (user.getStatus() != null && user.getStatus() == 1) {
                return ResponseMessage.error("您的账户已被封禁，无法发表评论");
            }

            // 奖励知识星云值
            try {
                Integer newDust = rewardService.rewardForComment(userId, false);
                logger.info("用户 {} 发表星球评论，获得知识星云值，当前值: {}", userId, newDust);
            } catch (Exception e) {
                logger.error("奖励知识星云值失败: {}", e.getMessage());
            }

            return ResponseMessage.success(result.getPlanetCommentId());
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }


    /**
     * 获取星球评论列表接口
     * 前端请求方式：POST
     * 请求URL：localhost:8081/planet/comment/list/{planetId}
     * 路径参数：planetId - 星球ID
     * 查询参数：
     *   page - 页码（默认1）
     *   size - 每页大小（默认20）
     *   userId - 当前用户ID（用于判断是否已点赞）
     * 返回值：评论列表（分页，树形结构）
     */
    @PostMapping("/list/{planetId}")
    public ResponseMessage getCommentList(@PathVariable @NotNull String planetId,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "20") int size,
                                          @RequestParam(required = false) Integer userId) {
            List<PlanetCommentDto> comments = commentService.getCommentList(planetId, page, size, userId);
            return ResponseMessage.success(comments);
    }

    /**
     * 点赞/取消点赞接口
     * 前端请求方式：POST
     * 请求URL：localhost:8081/planet/comment/like
     * 请求参数（param格式）：
     * {
     *   "userId": 1,         // 用户ID（必填）
     *   "commentId": 1       // 评论ID（必填）
     * }
     * 返回值：成功返回点赞数
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
     * 请求URL：localhost:8081/planet/comment/delete/{commentId}
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
     * 请求URL：localhost:8081/planet/comment/detail/{commentId}
     * 路径参数：commentId - 评论ID
     * 查询参数：userId - 当前用户ID（用于判断是否已点赞）
     * 返回值：评论详情及其所有回复
     */
    @GetMapping("/detail/{commentId}")
    public ResponseMessage getCommentDetail(@PathVariable @NotNull Integer commentId,
                                            @RequestParam(required = false) Integer userId) {
        try {
            PlanetCommentDto comment = commentService.getCommentDetail(commentId, userId);
            return ResponseMessage.success(comment);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
}