package com.example1.demo2.service.impl;

import com.example1.demo2.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentService implements IContentService {
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
