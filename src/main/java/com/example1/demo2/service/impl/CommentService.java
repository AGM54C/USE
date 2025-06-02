package com.example1.demo2.service.impl;

import com.example1.demo2.pojo.PlanetComment;
import com.example1.demo2.pojo.dto.PlanetCommentDto;
import com.example1.demo2.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService implements ICommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public void create(PlanetCommentDto comment) {
        PlanetComment c= ConvertUtil.convertDtoToPlanetComment(comment);
        commentMapper.add(c);
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
    public void updateLikeCount(Long commentId, Integer likeCount) {
        commentMapper.updateLikeCount(commentId, likeCount);
    }

    @Override
    public void updateStatus(Long commentId, Integer status) {
        commentMapper.updateStatus(commentId, status);
    }

    @Override
    public List<PlanetComment> listByPlanetId(String planetId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return commentMapper.listByPlanetId(planetId, offset, size);
    }

    @Override
    public void deleteByPlanetId(String planetId) {
        commentMapper.deleteByPlanetId(planetId);
    }

    @Override
    public PlanetComment findByCommentId(Long commentId) {
        return commentMapper.findByCommentId(commentId);
    }
}
