package com.example1.demo2.mapper;

import com.example1.demo2.pojo.KnowledgePlanet;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PlanetMapper {
    //根据星球名查找
    @Select("select * from tab_knowledge_planet where title=#{title}")
    KnowledgePlanet findByTitle(String title);

    //创建星球
    @Insert("insert into tab_knowledge_planet(planet_id,user_id,title,theme_id,create_time,update_time)"+" values(#{planetId},#{userId},#{title},#{themeId},now(),now())")
    void add(KnowledgePlanet p);
}
