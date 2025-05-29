package com.example1.demo2.service;

import com.example1.demo2.pojo.PlanetComment;
import com.example1.demo2.pojo.dto.PlanetCommentDto;
import jakarta.validation.Valid;

public interface ICommentService {
    void create(@Valid PlanetCommentDto comment);

    PlanetComment findByParentId(Long parentId);

    PlanetComment findByCommentId(Long commentId);

    void update(@Valid PlanetCommentDto comment);

    void delete(Long commentId);
}
