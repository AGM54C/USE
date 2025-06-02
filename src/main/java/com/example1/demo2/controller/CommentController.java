package com.example1.demo2.controller;

import java.util.List;
import com.example1.demo2.pojo.PlanetComment;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import com.example1.demo2.pojo.dto.PlanetCommentDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.util.ConvertUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;

@RestController //接口返回对象，转化为json文本
@RequestMapping("/comment")  //localhost:8081/comment/**
@Validated
public class CommentController {
        @Autowired
        private ICommentService commentService;

    /**
     * 创建评论
     * 前端请求方式：POST
     * 请求URL：localhost:8081/comment/create
     * 请求参数（JSON格式）：
     * {
     *   "planetId": String,        // 所属星球ID（必填）
     *   "userId": Integer,         // 评论用户ID（必填）
     *   "content": String,         // 评论内容（必填）
     *   "parentId": Long           // 父级评论ID（选填，默认为0）
     * }
     * 返回值：成功返回评论ID，失败返回错误信息
     */
    @PostMapping("/create")
    public ResponseMessage<String> create(@Valid @RequestBody PlanetCommentDto comment) {
        // 校验评论内容非空
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            return ResponseMessage.error("评论内容不能为空");
        }
        // 校验星球ID
        if (comment.getPlanetId() == null || comment.getPlanetId().trim().isEmpty()) {
            return ResponseMessage.error("必须指定所属星球ID");
        }
        // 校验父评论
        if (comment.getParentId() != 0) {
            PlanetComment parent = commentService.findByCommentId(comment.getParentId());
            if (parent != null) {
                comment.setLevel(parent.getLevel() + 1); // 设置评论层级
            }
        }
        commentService.create(comment);
        return ResponseMessage.success(comment.getCommentId());
    }

    /**
     * 查看评论详情
     * 前端请求方式：GET
     * 请求URL：localhost:8081/comment/commentinfo
     * 请求参数（Param格式）：
     * {
     *   "commentId": Long          // 评论ID（必填）
     * }
     * 返回值：评论详情
     */
        @GetMapping("/commentinfo")
        public ResponseMessage<KnowledgePlanetDto> commentinfo(@Valid @RequestBody PlanetCommentDto comment) {
            //根据评论ID查询
            PlanetComment c = commentService.findByCommentId(comment.getCommentId());
            if(c==null) {
                return ResponseMessage.error("评论不存在");
            }
            //转化为dto
            PlanetCommentDto dto= ConvertUtil.convertPlanetCommentToDto(c);
            return ResponseMessage.success(dto);
        }

    /**
     * 更新评论点赞数
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/comment/updateLikeCount
     * 请求参数（JSON格式）：
     * {
     *   "commentId": Long,         // 评论ID（必填）
     *   "likeCount": Integer       // 点赞数（必填）
     * }
     * 返回值：成功或失败信息
     */
    @PutMapping("/updateLikeCount")
    public ResponseMessage updateLikeCount(@Valid @RequestBody PlanetCommentDto comment) {
        // 根据评论ID查询
        PlanetComment c = commentService.findByCommentId(comment.getCommentId());
        if (c == null) {
            return ResponseMessage.error("评论不存在");
        }
        if (Objects.equals(comment.getLikeCount(), c.getLikeCount())) {
            return ResponseMessage.success("点赞数未变更");
        }
        // 更新信息
        commentService.updateLikeCount(comment.getCommentId(), comment.getLikeCount());
        return ResponseMessage.success("评论点赞数更新成功");
    }

    /**
     * 更新评论状态
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/comment/updateStatus
     * 请求参数（JSON格式）：
     * {
     *   "commentId": Long,         // 评论ID（必填）
     *   "status": Integer          // 状态（0-正常，1-隐藏，2-删除）
     * }
     * 返回值：成功或失败信息
     */
    @PutMapping("/updateStatus")
    public ResponseMessage updateStatus(@Valid @RequestBody PlanetCommentDto comment) {
        // 根据评论ID查询
        PlanetComment c = commentService.findByCommentId(comment.getCommentId());
        if (c == null) {
            return ResponseMessage.error("评论不存在");
        }
        if (Objects.equals(comment.getStatus(), c.getStatus())) {
            return ResponseMessage.success("状态未变更");
        }
        // 更新信息
        commentService.updateStatus(comment.getCommentId(), comment.getStatus());
        return ResponseMessage.success("评论状态更新成功");
    }

    /**
     * 获取星球下的所有评论
     * 前端请求方式：GET
     * 请求URL：localhost:8081/comment/listByPlanet
     * 请求参数（Param格式）：
     * {
     *   "planetId": String,        // 星球ID（必填）
     *   "page": Integer,           // 页码（选填，默认为1）
     *   "size": Integer            // 每页数量（选填，默认为10）
     * }
     * 返回值：成功返回评论列表，失败返回错误信息
     */
    @GetMapping("/listByPlanet")
    public ResponseMessage<List<PlanetCommentDto>> listByPlanet(
            @RequestParam("planetId") String planetId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        List<PlanetComment> comments = commentService.listByPlanetId(planetId, page, size);
        List<PlanetCommentDto> dtos = ConvertUtil.convertPlanetCommentListToDto(comments);
        return ResponseMessage.success(dtos);
    }

    /**
     * 删除评论
     * 前端请求方式：DELETE
     * 请求URL：localhost:8081/comment/delete
     * 请求参数（JSON格式）：
     * {
     *   "commentId": Long          // 评论ID（必填）
     * }
     * 返回值：成功或失败信息
     */
    @DeleteMapping("/delete")
    public ResponseMessage<String> delete(@Valid @RequestBody PlanetCommentDto comment) {
        // 通过id删除评论
        PlanetComment c = commentService.findByCommentId(comment.getCommentId());
        if (c == null) {
            // 评论不存在
            return ResponseMessage.error("评论不存在或已被删除");
        } else {
            // 删除评论
            commentService.delete(c.getCommentId());
            return ResponseMessage.success("评论已成功删除");
        }
    }

    /**
     * 批量删除星球下的所有评论
     * 前端请求方式：DELETE
     * 请求URL：localhost:8081/comment/deleteByPlanetId/{planetId}
     * 请求参数：
     *   - path: planetId           // 星球ID
     * 返回值：成功或失败信息
     */
    @DeleteMapping("/deleteByPlanetId/{planetId}")
    public ResponseMessage deleteCommentsByPlanet(@PathVariable String planetId) {
        commentService.deleteByPlanetId(planetId);
        return ResponseMessage.success("星球下的所有评论已删除");
    }
}
