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
     * 更新星球的星系ID（将星球加入星系）
     */
    @Update("update tab_knowledge_planet set galaxy_id=#{galaxyId} where planet_id=#{planetId}")
    void addPlanetToGalaxy(@Param("galaxyId") Integer galaxyId, @Param("planetId") String planetId);

    /**
     * 从星系中移除星球
     */
    @Update("update tab_knowledge_planet set galaxy_id=null where planet_id=#{planetId}")
    void removePlanetFromGalaxy(@Param("galaxyId") Integer galaxyId, @Param("planetId") String planetId);

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
}