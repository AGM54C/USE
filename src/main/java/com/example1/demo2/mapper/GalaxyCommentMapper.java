package com.example1.demo2.mapper;

import com.example1.demo2.pojo.GalaxyCommentLike;
import com.example1.demo2.pojo.GalaxyComment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface GalaxyCommentMapper {

    /**
     * 插入评论
     */
    @Insert("INSERT INTO tab_galaxy_comment(creator_id, galaxy_id, content, level, parent_comment_id, " +
            "reply_to_user_id, creator_role, release_time, update_time) " +
            "VALUES(#{user.userId}, #{knowledgeGalaxy.galaxyId}, #{content}, #{level}, #{parentId}, " +
            "#{replyToUserId}, #{creatorRole}, now(), now())")
    @Options(useGeneratedKeys = true, keyProperty = "galaxyCommentId", keyColumn = "galaxy_comment_id")
    void insertComment(GalaxyComment comment);

    /**
     * 根据ID查询评论
     */
    @Select("SELECT * FROM tab_galaxy_comment WHERE galaxy_comment_id = #{commentId}")
    @Results({
            @Result(property = "galaxyCommentId", column = "galaxy_comment_id"),
            @Result(property = "user", column = "creator_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.getUserById")),
            @Result(property = "knowledgeGalaxy", column = "galaxy_id",
                    one = @One(select = "com.example1.demo2.mapper.GalaxyMapper.getKnowledgeGalaxyById")),
            @Result(property = "parentId", column = "parent_comment_id"),
            @Result(property = "replyToUserId", column = "reply_to_user_id"),
            @Result(property = "createTime", column = "release_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    GalaxyComment getCommentById(Integer commentId);

    /**
     * 查询星系的一级评论（分页）
     */
    @Select("SELECT * FROM tab_galaxy_comment WHERE galaxy_id = #{galaxyId} " +
            "AND parent_comment_id = 0 AND status = 0 " +
            "ORDER BY release_time DESC LIMIT #{offset}, #{size}")
    @Results({
            @Result(property = "galaxyCommentId", column = "galaxy_comment_id"),
            @Result(property = "user", column = "creator_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.getUserById")),
            @Result(property = "knowledgeGalaxy", column = "galaxy_id",
                    one = @One(select = "com.example1.demo2.mapper.GalaxyMapper.getKnowledgeGalaxyById")),
            @Result(property = "parentId", column = "parent_comment_id"),
            @Result(property = "replyToUserId", column = "reply_to_user_id"),
            @Result(property = "createTime", column = "release_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    List<GalaxyComment> getFirstLevelComments(@Param("galaxyId") Integer galaxyId,
                                              @Param("offset") int offset,
                                              @Param("size") int size);

    /**
     * 查询评论的所有回复
     */
    @Select("SELECT * FROM tab_galaxy_comment WHERE parent_comment_id = #{parentId} " +
            "AND status = 0 ORDER BY release_time ASC")
    @Results({
            @Result(property = "galaxyCommentId", column = "galaxy_comment_id"),
            @Result(property = "user", column = "creator_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.getUserById")),
            @Result(property = "knowledgeGalaxy", column = "galaxy_id",
                    one = @One(select = "com.example1.demo2.mapper.GalaxyMapper.getKnowledgeGalaxyById")),
            @Result(property = "parentId", column = "parent_comment_id"),
            @Result(property = "replyToUserId", column = "reply_to_user_id"),
            @Result(property = "createTime", column = "release_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    List<GalaxyComment> getRepliesByParentId(Integer parentId);

    /**
     * 更新评论状态（软删除）
     */
    @Update("UPDATE tab_galaxy_comment SET status = #{status}, update_time = now() " +
            "WHERE galaxy_comment_id = #{commentId}")
    void updateCommentStatus(@Param("commentId") Integer commentId, @Param("status") Integer status);

    /**
     * 增加点赞数
     */
    @Update("UPDATE tab_galaxy_comment SET like_count = like_count + 1 WHERE galaxy_comment_id = #{commentId}")
    void increaseLikeCount(Integer commentId);

    /**
     * 减少点赞数
     */
    @Update("UPDATE tab_galaxy_comment SET like_count = like_count - 1 WHERE galaxy_comment_id = #{commentId} " +
            "AND like_count > 0")
    void decreaseLikeCount(Integer commentId);

    /**
     * 增加回复数
     */
    @Update("UPDATE tab_galaxy_comment SET reply_count = reply_count + 1 WHERE galaxy_comment_id = #{commentId}")
    void increaseReplyCount(Integer commentId);

    /**
     * 减少回复数
     */
    @Update("UPDATE tab_galaxy_comment SET reply_count = reply_count - 1 WHERE galaxy_comment_id = #{commentId} " +
            "AND reply_count > 0")
    void decreaseReplyCount(Integer commentId);

    /**
     * 插入点赞记录
     */
    @Insert("INSERT INTO tab_comment_like(user_id, comment_id, create_time) " +
            "VALUES(#{userId}, #{commentId}, now())")
    void insertLike(@Param("userId") Integer userId, @Param("commentId") Integer commentId);

    /**
     * 删除点赞记录
     */
    @Delete("DELETE FROM tab_comment_like WHERE user_id = #{userId} AND comment_id = #{commentId}")
    void deleteLike(@Param("userId") Integer userId, @Param("commentId") Integer commentId);

    /**
     * 检查是否已点赞
     */
    @Select("SELECT COUNT(*) > 0 FROM tab_comment_like WHERE user_id = #{userId} AND comment_id = #{commentId}")
    boolean isLiked(@Param("userId") Integer userId, @Param("commentId") Integer commentId);

    /**
     * 获取用户在星系中的角色
     */
    @Select("SELECT CASE " +
            "WHEN kg.user_id = #{userId} THEN 0 " +
            "WHEN EXISTS(SELECT 1 FROM tab_galaxy_administrator WHERE galaxy_id = #{galaxyId} AND user_id = #{userId}) THEN 1 " +
            "ELSE 2 END " +
            "FROM tab_knowledge_galaxy kg WHERE kg.galaxy_id = #{galaxyId}")
    Integer getUserRoleInGalaxy(@Param("userId") Integer userId, @Param("galaxyId") Integer galaxyId);
}
