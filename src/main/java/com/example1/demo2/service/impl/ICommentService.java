package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.CommentMapper;
import com.example1.demo2.pojo.PlanetComment;
import com.example1.demo2.pojo.dto.PlanetCommentDto;
import com.example1.demo2.service.CommentService;
import com.example1.demo2.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ICommentService implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public void create(PlanetCommentDto comment) {
        PlanetComment c= ConvertUtil.convertDtoToPlanetComment(comment);
        commentMapper.add(c);
        comment.setPlanetId(c.getPlanetId());
    }

    @Override
    public PlanetComment findByParentId(Long parentId) {
        return commentMapper.findByParentId(parentId);
    }


    @Override
    public void update(PlanetCommentDto comment) {
        PlanetComment c= ConvertUtil.convertDtoToPlanetComment(comment);
        commentMapper.update(c);
    }

    @Override
    public void delete(Long commentId) {
        commentMapper.delete(commentId);
    }

    @Override
    public PlanetComment findByCommentId(Long commentId) {
        return commentMapper.findByCommentId(commentId);
    }
}
