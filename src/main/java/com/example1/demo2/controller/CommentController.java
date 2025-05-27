package com.example1.demo2.controller;

import com.example1.demo2.pojo.PlanetComment;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import com.example1.demo2.pojo.dto.PlanetCommentDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.CommentService;
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
        private CommentService commentService;

        //评论创建（必要参数：评论用户id，所属星球id，关联知识id，评论内容，父级评论id,返回值：评论id）
        @PostMapping("/create")         //localhost:8081/planet/create
        public ResponseMessage<String> create(@Valid @RequestBody PlanetCommentDto comment) {
            // 校验评论内容非空
            if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
                return ResponseMessage.error("评论内容不能为空");
            }
            // 校验父评论
            if(comment.getParentId()!=0){
                PlanetComment c=commentService.findByParentId(comment.getParentId());
                //非空则设置下一级
                comment.setLevel(c.getLevel()+1);
            }
            commentService.create(comment);
            return ResponseMessage.success(comment.getCommentId());
        }

        //查看评论详情(必要参数：评论id,返回值：评论信列表）
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

        //更新评论信息(like_count,status,reply_count等,必要参数：评论id，返回值：失败或成功）
        @PutMapping("/update")
        public ResponseMessage update(@Valid @RequestBody PlanetCommentDto comment) {
            //根据评论ID查询
            PlanetComment c = commentService.findByCommentId(comment.getCommentId());
            if(c==null) {
                return ResponseMessage.error("评论不存在");
            }
            // 判断信息是否有变化
            boolean isChanged = false;
            if (!Objects.equals(comment.getLikeCount(),c.getLikeCount())) isChanged = true;
            if (!Objects.equals(comment.getStatus(),c.getStatus())) isChanged = true;
            if (!Objects.equals(comment.getReplyCount(),c.getReplyCount())) isChanged = true;
            if (!isChanged) {
                return ResponseMessage.success("没有需要修改的信息！");
            }
            //更新信息
            commentService.update(comment);
            return ResponseMessage.success("已更新评论信息！");
        }

        //删除评论（必要参数：评论id，返回值：成功或失败）
        @DeleteMapping("/delete")       //localhost:8081/planet/delete
        public ResponseMessage<String> delete(@Valid @RequestBody PlanetCommentDto comment) {
            //通过id删除评论
            PlanetComment c = commentService.findByCommentId(comment.getCommentId());
            if(c==null) {
                //评论不存在
                return ResponseMessage.error("评论不存在或已被删除");
            }
            else{
                //删除评论
                commentService.delete(c.getCommentId());
                return ResponseMessage.success("评论ID"+comment.getPlanetId()+"已成功删除！");
            }
        }
}
