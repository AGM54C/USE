package com.example1.demo2.mapper;

import com.example1.demo2.pojo.PlanetComment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    //创建内容
    @Insert("insert into tab_planet_comment(planet_id,user_id,content,create_time,update_time,level,parent_id)"
            +" values(#{planet.planetId},#{userId},#{content},now(),now(),#{level},#{parentId})")
    void add(PlanetComment c);

    //根据父级评论查找
    @Select("select * from tab_planet_comment where parent_id=#{parentId}")
    PlanetComment findByParentId(Long parentId);

    //根据评论id查找
    @Select("select * from tab_planet_comment where comment_id=#{commentId}")
    PlanetComment findByCommentId(Long commentId);

    //更新评论信息
    @Update("update tab_planet_comment set like_count=#{likeCount}" +
            ",reply_count=#{replyCount}" +
            ",status=#{status}"+
            " where comment_id=#{commentId}")
    void update(PlanetComment c);

    //删除pingl
    @Delete("delete from tab_planet_comment where comment_id=#{commentId}")
    void delete(Long commentId);

    @Delete("DELETE FROM tab_planet_comment WHERE planet_id = #{planetId}")
    void deleteByPlanetId(String planetId);

    @Select("SELECT * FROM tab_planet_comment " +
            "WHERE planet_id = #{planetId} " +
            "ORDER BY create_time DESC " +
            "LIMIT #{offset}, #{size}")
    List<PlanetComment> listByPlanetId(String planetId, int offset, Integer size);

    @Update("UPDATE tab_planet_comment " +
            "SET status = #{status}, update_time = NOW() " +
            "WHERE comment_id = #{commentId}")
    void updateStatus(Long commentId, Integer status);

    @Update("UPDATE tab_planet_comment " +
            "SET like_count = #{likeCount}, update_time = NOW() " +
            "WHERE comment_id = #{commentId}")
    void updateLikeCount(Long commentId, Integer likeCount);

    @Update("UPDATE tab_planet_comment " +
            "SET planet_id = #{planetId}, update_time = NOW() " +
            "WHERE comment_id = #{commentId}")
    void updatePlanetId(Long commentId, String planetId);
}
