package com.example1.demo2.service;

import com.example1.demo2.pojo.PlanetComment;
import com.example1.demo2.pojo.dto.PlanetCommentDto;
import jakarta.validation.Valid;

import java.util.List;

public interface ICommentService {
    void create(@Valid PlanetCommentDto comment);

    PlanetComment findByParentId(Long parentId);

    PlanetComment findByCommentId(Long commentId);

    void update(@Valid PlanetCommentDto comment);

    void delete(Long commentId);

    void updateLikeCount(Long commentId, Integer likeCount);

    void updateStatus(Long commentId, Integer status);

    List<PlanetComment> listByPlanetId(String planetId, Integer page, Integer size);

    void deleteByPlanetId(String planetId);
}
