package com.example1.demo2.mapper;

import com.example1.demo2.pojo.KnowledgePlanet;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlanetMapper {
    //根据星球名查找
    @Select("select * from tab_knowledge_planet where content_title=#{contentTitle}")
    KnowledgePlanet findByTitle(String title);

    /**
     * 根据星球ID查找星球
     */
    @Select("select * from tab_knowledge_planet where planet_id=#{planetId}")
    KnowledgePlanet getPlanetById(String planetId);

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
    @Select("select planet_id from tab_knowledge_planet where content_title like CONCAT('%', #{keyword}, '%') and user_id = #{userId}")
    List<String> searchIdsByKeyword(String keyword, Integer userId);

    @Update("UPDATE tab_knowledge_planet " +
            "SET content_title = #{newTitle}, update_time = NOW() " +
            "WHERE planet_id = #{planetId}")
    void updateTitle(String planetId, String newTitle);

    @Update("UPDATE tab_knowledge_planet " +
            "SET cover_url = #{newCoverUrl}, update_time = NOW() " +
            "WHERE planet_id = #{planetId}")
    void updateCoverUrl(String planetId, String newCoverUrl);

    @Delete("DELETE FROM tab_knowledge_planet WHERE planet_id = #{planetId}")
    void deleteById(String planetId);

    @Select("SELECT * FROM tab_knowledge_planet WHERE content_title LIKE #{keyword}")
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

    @Update("UPDATE tab_knowledge_planet " +
            "SET visit_count = visit_count+1, update_time = NOW() " +
            "WHERE planet_id = #{planetId}")
    void updatevisitCount(String planetId);

    @Update("UPDATE tab_knowledge_planet " +
            "SET visibility = 1, update_time = NOW() " +
            "WHERE planet_id = #{planetId}")
    void publish(String planetId);

    // 获取用户最喜欢星球
    @Select("select * from tab_knowledge_planet where planet_id = #{favorPlanetId}")
    KnowledgePlanet getFavorPlanet(String favorPlanetId);

    @Update("UPDATE tab_knowledge_planet " +
            "SET visibility = #{visibility}, update_time = NOW() " +
            "WHERE planet_id = #{planetId}")
    void updatevisibility(String planetId, Integer visibility);

    // ==================== 级联删除相关方法 ====================

    /**
     * 删除星球评论的所有点赞记录
     * 通过子查询找到该星球的所有评论ID，然后删除对应的点赞记录
     */
    @Delete("DELETE FROM tab_planet_comment_like WHERE planet_comment_id IN " +
            "(SELECT planet_comment_id FROM tab_planet_comment WHERE planet_id = #{planetId})")
    void deletePlanetCommentLikesByPlanetId(String planetId);

    /**
     * 删除星球的所有评论
     */
    @Delete("DELETE FROM tab_planet_comment WHERE planet_id = #{planetId}")
    void deletePlanetCommentsByPlanetId(String planetId);

    /**
     * 删除与星球相关的通知
     */
    @Delete("DELETE FROM tab_notification WHERE target_type = 2 AND target_id = #{planetId}")
    void deleteNotificationsByPlanetId(String planetId);

    /**
     * 获取用户创建的所有星球ID
     */
    @Select("SELECT planet_id FROM tab_knowledge_planet WHERE user_id = #{userId}")
    List<String> getPlanetIdsByUserId(Integer userId);

    /**
     * 删除用户创建的所有星球
     */
    @Delete("DELETE FROM tab_knowledge_planet WHERE user_id = #{userId}")
    void deletePlanetsByUserId(Integer userId);

    /**
     * 获取星球所属的星系ID
     */
    @Select("SELECT galaxy_id FROM tab_knowledge_planet WHERE planet_id = #{planetId}")
    String getGalaxyIdByPlanetId(String planetId);
}