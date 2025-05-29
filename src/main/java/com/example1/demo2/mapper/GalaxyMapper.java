package com.example1.demo2.mapper;

import com.example1.demo2.pojo.KnowledgeGalaxy;
import org.apache.ibatis.annotations.*;

@Mapper
public interface GalaxyMapper {
    /**
     * 根据星系名查找星系
     */
    @Select("select * from tab_knowledge_galaxy where name=#{name}")
    KnowledgeGalaxy getKnowledgeGalaxyByName(String name);

    /**
     * 根据星系ID查找星系
     */
    @Select("select * from tab_knowledge_galaxy where galaxy_id=#{galaxyId}")
    KnowledgeGalaxy getKnowledgeGalaxyById(String galaxyId);

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
    void delete(String galaxyId);

    /**
     * 根据邀请码查找星系
     */
    @Select("select * from tab_knowledge_galaxy where invite_code=#{inviteCode}")
    KnowledgeGalaxy getKnowledgeGalaxyByInviteCode(String inviteCode);

    /**
     * 更新星球的星系ID（将星球加入星系）
     */
    @Update("update tab_knowledge_planet set galaxy_id=#{galaxyId} where planet_id=#{planetId}")
    void addPlanetToGalaxy(@Param("galaxyId") String galaxyId, @Param("planetId") String planetId);

    /**
     * 从星系中移除星球
     */
    @Update("update tab_knowledge_planet set galaxy_id=null where planet_id=#{planetId}")
    void removePlanetFromGalaxy(@Param("galaxyId") String galaxyId, @Param("planetId") String planetId);
}