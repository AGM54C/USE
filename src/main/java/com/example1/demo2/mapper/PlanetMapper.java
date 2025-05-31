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
    @Insert("insert into tab_knowledge_planet(planet_id,user_id,content_title,description,theme_id,create_time,update_time)"
            +" values(#{planetId},#{userId},#{contentTitle},#{description},#{themeId},now(),now())")
    void add(KnowledgePlanet p);

    //根据星球ID查找
    @Select("select * from tab_knowledge_planet where planet_id=#{planetId}")
    KnowledgePlanet findByPlanetId(String planetId);

    //删除星球
    @Delete("delete from tab_knowledge_planet where planet_id=#{planetId}")
    void delete(String planetId);

    // 查询用户创建的所有星球
    @Select("select * from tab_knowledge_planet where user_id = #{userId}")
    List<KnowledgePlanet> selectAll(Integer userId);

    // 根据关键词搜索用户创建的星球ID(模糊查询)
    @Select("select planet_id from tab_knowledge_planet where title like CONCAT('%', #{keyword}, '%') and user_id = #{userId}")
    List<String> searchIdsByKeyword(String keyword, Integer userId);

    @Update("UPDATE tab_knowledge_planet " +
            "SET content_title = #{newTitle}, update_time = NOW() " +
            "WHERE planet_id = #{planetId}")
    void updateTitle(String planetId, String newTitle);

    @Update("UPDATE tab_knowledge_planet " +
            "SET cover_url = #{newCoverUrl}, update_time = NOW() " +
            "WHERE planet_id = #{planetId}")
    void updateCoverUrl(String planetId, String newCoverUrl);

    @Update("UPDATE tab_knowledge_planet " +
            "SET visibility = #{newVisibility}, update_time = NOW() " +
            "WHERE planet_id = #{planetId}")
    void updateVisibility(String planetId, Integer newVisibility);

    @Delete("DELETE FROM tab_knowledge_planet WHERE planet_id = #{planetId}")
    void deleteById(String planetId);

    @Select("SELECT * FROM tab_knowledge_planet WHERE title LIKE #{keyword}")
    List<KnowledgePlanet> searchPlanets(String s);

    @Update("UPDATE tab_knowledge_planet " +
            "SET description = #{description}, update_time = NOW() " +
            "WHERE planet_id = #{planetId}")
    void updatedescription(String planetId, String description);

    @Update("UPDATE tab_knowledge_planet " +
            "SET content_detail = #{contentDetail}, update_time = NOW() " +
            "WHERE planet_id = #{planetId}")
    void updatedetail(String planetId, String contentDetail);

    @Update("UPDATE tab_knowledge_planet " +
            "SET brightness = #{brightness}, update_time = NOW() " +
            "WHERE planet_id = #{planetId}")
    void updatebrightness(String planetId, Integer brightness);

    @Update("UPDATE tab_knowledge_planet " +
            "SET fuel_value = #{fuelValue}, update_time = NOW() " +
            "WHERE planet_id = #{planetId}")
    void updatefuelvalue(String planetId, Integer fuelValue);
}
