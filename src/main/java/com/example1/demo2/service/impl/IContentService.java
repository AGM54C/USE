package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.ContentMapper;
import com.example1.demo2.pojo.PlanetContent;
import com.example1.demo2.pojo.dto.PlanetContentDto;
import com.example1.demo2.service.ContentService;
import com.example1.demo2.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IContentService implements ContentService {
    @Autowired
    private ContentMapper contentMapper;

    @Override
    public void update(PlanetContentDto content) {
        PlanetContent c = ConvertUtil.convertDtoToPlanetContent(content);
        contentMapper.update(c);
    }

    @Override
    public PlanetContent findByTitle(String title) {
        return contentMapper.findByTitle(title);
    }

    @Override
    public void create(PlanetContentDto content) {
        PlanetContent c = ConvertUtil.convertDtoToPlanetContent(content);
        contentMapper.add(c);
    }

    @Override
    public void delete(Integer contentId) {
        contentMapper.delete(contentId);
    }
}
