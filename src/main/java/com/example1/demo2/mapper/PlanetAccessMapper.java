package com.example1.demo2.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.example1.demo2.pojo.KnowledgePlanet;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlanetAccessMapper {

    //根据星球名查找(模糊查询)
    @Select("select * from tab_knowledge_planet where contentTitle like concat('%', #{contentTitle}, '%') and visibility=1 limit 10")
    List<KnowledgePlanet> findByTitle(String title);
    //随机访问
    @Select("select * from tab_knowledge_planet where visibility=1 order by rand() limit 1")
    KnowledgePlanet findRandomPlanet();

    @Select("select * from tab_knowledge_planet where visibility=1 order by visit_count limit 10")
    List<KnowledgePlanet> getTop10Planets();
}
