package com.example1.demo2.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.example1.demo2.pojo.KnowledgePlanet;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PlanetAccessMapper {

    //根据星球名查找
    @Select("select * from tab_knowledge_planet where content_title=#{contentTitle}")
    KnowledgePlanet findByTitle(String title);
    //随机访问
    @Select("select * from tab_knowledge_planet order by rand() limit 1")
    KnowledgePlanet findRandomPlanet();

}
