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
     * @param commentId 评论ID
     * @return 返回对应的星系对象
     */
    @Select("SELECT *.kg FROM tab_knowledge_galaxy kg" +
            "JOIN tab_galaxy_comment gc ON kg.galaxy_id = gc.galaxy_id " +
            "WHERE gc.galaxy_comment_id = #{commentId}")
    KnowledgeGalaxy getGalaxyByCommentId(Integer commentId);

    /**
     * 检查用户是否是星系管理员
     * @param galaxyId 星系ID
     * @param currentUserId 当前用户ID
     * @return 如果是管理员返回true，否则返回false
     */
    @Select("SELECT COUNT(*) > 0 FROM tab_galaxy_admins " +
            "WHERE galaxy_id = #{galaxyId} AND user_id = #{currentUserId} AND status = 0")
    boolean isUserGalaxyAdmin(Integer galaxyId, Integer currentUserId);

    /**
     * 检查用户是否是系统管理员
     * @param currentUserId 当前用户ID
     * @return 如果是系统管理员返回true，否则返回false
     */
    @Select("SELECT COUNT(*) > 0 FROM tab_system_admin WHERE user_id = #{currentUserId}")
    boolean isUserSystemAdmin(Integer currentUserId);


    @Delete("DELETE FROM tab_galaxy_comment WHERE galaxy_id = #{galaxyId}")
    void deleteGalaxyComment(Integer galaxyId);
}