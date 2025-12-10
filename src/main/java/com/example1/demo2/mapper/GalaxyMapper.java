package com.example1.demo2.mapper;

import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.KnowledgePlanet;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GalaxyMapper {
    @Select("select * from tab_knowledge_galaxy where user_id=#{userId} order by create_time desc")
    List<KnowledgeGalaxy> selectAll(Integer userId);


    /**
     * 根据星系名查找星系
     */
    @Select("select * from tab_knowledge_galaxy where name=#{name}")
    KnowledgeGalaxy getKnowledgeGalaxyByName(String name);

    /**
     * 根据星系ID查找星系
     */
    @Select("select * from tab_knowledge_galaxy where galaxy_id=#{galaxyId}")
    KnowledgeGalaxy getKnowledgeGalaxyById(Integer galaxyId);

    /**
     * 创建星系
     * 注意：galaxy_id是自增主键，不需要插入
     */
    @Insert("insert into tab_knowledge_galaxy(user_id, name, label, permission, invite_code, create_time, update_time) " +
            "values(#{userId}, #{name}, #{label}, #{permission}, #{inviteCode}, now(), now())")
    @Options(useGeneratedKeys = true, keyProperty = "galaxyId", keyColumn = "galaxy_id")
    void add(KnowledgeGalaxy galaxy);

    /**
     * 更新星系信息
     */
    @Update("update tab_knowledge_galaxy set " +
            "name=#{name}, " +
            "label=#{label}, " +
            "permission=#{permission}, " +
            "invite_code=#{inviteCode}, " +
            "update_time=now() " +
            "where galaxy_id=#{galaxyId}")
    void update(KnowledgeGalaxy galaxy);

    /**
     * 删除星系
     */
    @Delete("delete from tab_knowledge_galaxy where galaxy_id=#{galaxyId}")
    void delete(Integer galaxyId);

    /**
     * 根据邀请码查找星系
     */
    @Select("select * from tab_knowledge_galaxy where invite_code=#{inviteCode}")
    KnowledgeGalaxy getKnowledgeGalaxyByInviteCode(String inviteCode);

    /**
     * 更新星球的星系ID(将星球加入星系)
     */
    @Update("update tab_knowledge_planet set galaxy_id=#{galaxyId} where planet_id=#{planetId}")
    void addPlanetToGalaxy(@Param("galaxyId") Integer galaxyId, @Param("planetId") String planetId);

    /**
     * 从星系中移除星球
     */
    @Update("update tab_knowledge_planet set galaxy_id=null where planet_id=#{planetId} and galaxy_id=#{galaxyId}")
    void removePlanetFromGalaxy(@Param("galaxyId") Integer galaxyId, @Param("planetId") String planetId);

    /**
     * 增加星系的星球数量
     */
    @Update("update tab_knowledge_galaxy set planet_count=planet_count+1 where galaxy_id=#{galaxyId}")
    void incrementPlanetCount(Integer galaxyId);

    /**
     * 减少星系的星球数量
     */
    @Update("update tab_knowledge_galaxy set planet_count=planet_count-1 where galaxy_id=#{galaxyId}")
    void decrementPlanetCount(Integer galaxyId);

    /**
     * 查询星系权限
     */
    @Select("select permission from tab_knowledge_galaxy where galaxy_id=#{galaxyId}")
    Integer getGalaxyPermissionById(String galaxyId);

    /**
     * 模糊搜索星系名字
     * @param name 星系名称
     */
    @Select("select * from tab_knowledge_galaxy where name like concat('%', #{name}, '%')")
    List<KnowledgeGalaxy> searchByName(String name);

    /**
     * 根据星系名称和创建者ID查询星系
     * @param name 星系名称
     * @param userId 创建者用户ID
     */
    @Select("select * from tab_knowledge_galaxy where name like concat('%', #{name}, '%') and user_id=#{userId}")
    List<KnowledgeGalaxy> searchByNameAndCreatorId(String name, Integer userId);

    /**
     * 根据星系名称排除特定用户的星系
     * @param name 星系名称
     * @param userId 排除的用户ID
     */
    @Select("select * from tab_knowledge_galaxy where name like concat('%', #{name}, '%') and user_id != #{userId}")
    List<KnowledgeGalaxy> searchByNameExcludeUser(String name, Integer userId);

    /**
     * 获取最大星系ID
     * return 返回当前最大的星系ID，如果没有星系则返回null
     */
    @Select("select max(galaxy_id) from tab_knowledge_galaxy")
    Integer getMaxGalaxyId();

    /**
     * 获取指定星系下的所有知识星球
     * @param galaxyId 星系ID
     * @return 返回该星系下的所有知识星球列表
     */
    @Select("select * from tab_knowledge_planet where galaxy_id=#{galaxyId}")
    List<KnowledgePlanet> getPlanetsByGalaxyId(Integer galaxyId);

    /**
     * 根据评论ID获取对应的星系
     * 注意：tab_galaxy_comment 表的主键列名是 galaxy_comment_id
     */
    @Select("SELECT kg.* FROM tab_knowledge_galaxy kg " +
            "JOIN tab_galaxy_comment gc ON kg.galaxy_id = gc.galaxy_id " +
            "WHERE gc.galaxy_comment_id = #{commentId}")
    KnowledgeGalaxy getGalaxyByCommentId(Integer commentId);

    /**
     * 检查用户是否是星系管理员
     */
    @Select("SELECT COUNT(*) > 0 FROM tab_galaxy_admins " +
            "WHERE galaxy_id = #{galaxyId} AND user_id = #{currentUserId} AND status = 0")
    boolean isUserGalaxyAdmin(Integer galaxyId, Integer currentUserId);

    /**
     * 检查用户是否是系统管理员
     */
    @Select("SELECT COUNT(*) > 0 FROM tab_system_admin WHERE user_id = #{currentUserId}")
    boolean isUserSystemAdmin(Integer currentUserId);

    /**
     * 删除星系的所有评论
     */
    @Delete("DELETE FROM tab_galaxy_comment WHERE galaxy_id = #{galaxyId}")
    void deleteGalaxyComments(Integer galaxyId);

    /**
     * 删除单条星系评论
     */
    @Delete("DELETE FROM tab_galaxy_comment WHERE galaxy_comment_id = #{commentId}")
    void deleteGalaxyComment(Integer commentId);

    // ==================== 级联删除相关方法 ====================

    /**
     * 删除星系评论的所有点赞记录
     *
     * 重要说明：
     * - tab_galaxy_comment 表的主键是 galaxy_comment_id
     * - tab_galaxy_comment_like 表中引用评论的外键列名需要根据实际情况调整
     * - 如果 like 表的外键列名是 comment_id，使用下面这个版本
     * - 如果 like 表的外键列名也是 galaxy_comment_id，请将 comment_id 改为 galaxy_comment_id
     */
    @Delete("DELETE FROM tab_galaxy_comment_like WHERE comment_id IN " +
            "(SELECT galaxy_comment_id FROM tab_galaxy_comment WHERE galaxy_id = #{galaxyId})")
    void deleteGalaxyCommentLikesByGalaxyId(Integer galaxyId);

    /**
     * 删除星系的所有管理员记录
     */
    @Delete("DELETE FROM tab_galaxy_admins WHERE galaxy_id = #{galaxyId}")
    void deleteGalaxyAdminsByGalaxyId(Integer galaxyId);

    /**
     * 将星系下所有星球的galaxy_id设置为null（解除星球与星系的关联）
     */
    @Update("UPDATE tab_knowledge_planet SET galaxy_id = NULL WHERE galaxy_id = #{galaxyId}")
    void detachPlanetsFromGalaxy(Integer galaxyId);

    /**
     * 删除与星系相关的通知
     */
    @Delete("DELETE FROM tab_notification WHERE target_type = 1 AND target_id = #{galaxyId}")
    void deleteNotificationsByGalaxyId(Integer galaxyId);

    /**
     * 获取用户创建的所有星系
     */
    @Select("SELECT * FROM tab_knowledge_galaxy WHERE user_id = #{userId}")
    List<KnowledgeGalaxy> getGalaxiesByUserId(Integer userId);

    /**
     * 删除用户创建的所有星系
     */
    @Delete("DELETE FROM tab_knowledge_galaxy WHERE user_id = #{userId}")
    void deleteGalaxiesByUserId(Integer userId);

    /**
     * 获取星系下所有星球的ID列表
     */
    @Select("SELECT planet_id FROM tab_knowledge_planet WHERE galaxy_id = #{galaxyId}")
    List<String> getPlanetIdsByGalaxyId(Integer galaxyId);
}