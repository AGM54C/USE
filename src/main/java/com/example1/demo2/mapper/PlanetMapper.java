package com.example1.demo2.mapper;

import com.example1.demo2.pojo.KnowledgePlanet;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlanetMapper {
    //根据星球名查找
    @Select("select * from tab_knowledge_planet where title=#{title}")
    KnowledgePlanet findByTitle(String title);

    //创建星球
    @Insert("insert into tab_knowledge_planet(planet_id,user_id,title,theme_id,create_time,update_time)"
            +" values(#{planetId},#{userId},#{title},#{themeId},now(),now())")
    void add(KnowledgePlanet p);

    //根据星球ID查找
    @Select("select * from tab_knowledge_planet where planet_id=#{planetId}")
    KnowledgePlanet findByPlanetId(String planetId);

    //更新星球信息
    @Update("update tab_knowledge_planet set title=#{title}" +
            ",cover_url=#{coverUrl}" +
            ",visibility=#{visibility}" +
            ",update_time=now()" +
            ",description=#{description}"+
            " where planet_id=#{planetId}")
    void update(KnowledgePlanet p);

    //删除星球
    @Delete("delete from tab_knowledge_planet where planet_id=#{planetId}")
    void delete(String planetId);

    // 查询用户创建的所有星球
    @Select("select * from tab_knowledge_planet where user_id = #{userId}")
    List<KnowledgePlanet> selectAll(Integer userId);

    // 根据关键词搜索用户创建的星球ID(模糊查询)
    @Select("select planet_id from tab_knowledge_planet where title like CONCAT('%', #{keyword}, '%') and user_id = #{userId}")
    List<String> searchIdsByKeyword(String keyword, Integer userId);
}
