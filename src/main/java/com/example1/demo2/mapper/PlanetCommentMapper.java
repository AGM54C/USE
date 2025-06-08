package com.example1.demo2.mapper;

import com.example1.demo2.pojo.PlanetComment;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PlanetCommentMapper {

    /**
     * 插入评论
     * 修正版本：确保正确返回自增主键
     */
    @Insert("INSERT INTO tab_planet_comment(user_id, planet_id, content, level, parent_comment_id, " +
            "reply_to_user_id, create_time, update_time) " +
            "VALUES(#{user.userId}, #{planet.planetId}, #{content}, #{level}, #{parentId}, " +
            "#{replyToUserId}, now(), now())")
    @Options(useGeneratedKeys = true, keyProperty = "planetCommentId", keyColumn = "planet_comment_id")
    int insertComment(PlanetComment comment); // 修改返回类型为 int

    /**
     * 根据ID查询评论
     * 修正：将方法引用改为正确的方法名
     */
    @Select("SELECT * FROM tab_planet_comment WHERE planet_comment_id = #{commentId}")
    @Results({
            @Result(property = "planetCommentId", column = "planet_comment_id"),
            @Result(property = "user", column = "user_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.findById")),
            @Result(property = "planet", column = "planet_id",
                    one = @One(select = "com.example1.demo2.mapper.PlanetMapper.getPlanetById")),
            @Result(property = "parentId", column = "parent_comment_id"),
            @Result(property = "replyToUserId", column = "reply_to_user_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    PlanetComment getCommentById(Integer commentId);

    /**
     * 查询星球的一级评论（分页）
     * 修正：将方法引用改为正确的方法名
     */
    @Select("SELECT * FROM tab_planet_comment WHERE planet_id = #{planetId} " +
            "AND parent_comment_id = 0 AND status = 0 " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    @Results({
            @Result(property = "planetCommentId", column = "planet_comment_id"),
            @Result(property = "user", column = "user_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.findById")),
            @Result(property = "knowledgePlanet", column = "planet_id",
                    one = @One(select = "com.example1.demo2.mapper.PlanetMapper.getPlanetById")),
            @Result(property = "parentId", column = "parent_comment_id"),
            @Result(property = "replyToUserId", column = "reply_to_user_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    List<PlanetComment> getFirstLevelComments(@Param("planetId") @NotNull String planetId,
                                              @Param("offset") int offset,
                                              @Param("size") int size);

    /**
     * 查询评论的所有回复
     * 修正：将方法引用改为正确的方法名
     */
    @Select("SELECT * FROM tab_planet_comment WHERE parent_comment_id = #{parentId} " +
            "AND status = 0 ORDER BY create_time ASC")
    @Results({
            @Result(property = "planetCommentId", column = "planet_comment_id"),
            @Result(property = "user", column = "user_id",
                    one = @One(select = "com.example1.demo2.mapper.UserMapper.findById")),
            @Result(property = "planet", column = "planet_id",
                    one = @One(select = "com.example1.demo2.mapper.PlanetMapper.getPlanetById")),
            @Result(property = "parentId", column = "parent_comment_id"),
            @Result(property = "replyToUserId", column = "reply_to_user_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    List<PlanetComment> getRepliesByParentId(Integer parentId);

    /**
     * 更新评论状态（软删除）
     */
    @Update("UPDATE tab_planet_comment SET status = #{status}, update_time = now() " +
            "WHERE planet_comment_id = #{commentId}")
    void updateCommentStatus(@Param("commentId") Integer commentId, @Param("status") Integer status);

    /**
     * 硬删除评论
     */
    @Delete("DELETE FROM tab_planet_comment WHERE planet_comment_id = #{commentId}")
    void deleteComment(Integer commentId);

    /**
     * 增加点赞数
     */
    @Update("UPDATE tab_planet_comment SET like_count = like_count + 1 WHERE planet_comment_id = #{commentId}")
    void increaseLikeCount(Integer commentId);

    /**
     * 减少点赞数
     */
    @Update("UPDATE tab_planet_comment SET like_count = like_count - 1 WHERE planet_comment_id = #{commentId} " +
            "AND like_count > 0")
    void decreaseLikeCount(Integer commentId);

    /**
     * 增加回复数
     */
    @Update("UPDATE tab_planet_comment SET reply_count = reply_count + 1 WHERE planet_comment_id = #{commentId}")
    void increaseReplyCount(Integer commentId);

    /**
     * 减少回复数
     */
    @Update("UPDATE tab_planet_comment SET reply_count = reply_count - 1 WHERE planet_comment_id = #{commentId} " +
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
     * 更新评论的星球 ID
     * @param commentId 评论 ID
     * @param planetId 星球 ID
     */
    @Update("UPDATE tab_planet_comment SET planet_id = #{planetId} WHERE planet_comment_id = #{commentId}")
    void updatePlanetId(Long commentId, String planetId);

    /**
     * 根据星球 ID 删除评论
     * @param planetId 星球 ID
     */
    @Delete("DELETE FROM tab_planet_comment WHERE planet_id = #{planetId}")
    void deleteByPlanetId(String planetId);

    // 假设存在 PlanetMapper 中有删除星球的方法，这里补充 PlanetMapper 接口的删除方法
    interface PlanetMapper {
        /**
         * 根据星球 ID 删除星球
         * @param planetId 星球 ID
         */
        @Delete("DELETE FROM tab_knowlege_planet WHERE planet_id = #{planetId}")
        void deleteById(String planetId);
    }
}