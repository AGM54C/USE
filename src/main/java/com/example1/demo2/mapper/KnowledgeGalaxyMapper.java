package com.example1.demo2.mapper;

import com.example1.demo2.pojo.KnowledgeGalaxy;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface KnowledgeGalaxyMapper {

    // 添加星系
    @Insert("INSERT INTO tab_knowledge_galaxy(name, theme_tags, permission_type, creator_id, member_count, last_activity_time, create_time, update_time) " +
            "VALUES(#{name}, #{themeTags}, #{permissionType}, #{creator.userId}, #{memberCount}, #{lastActivityTime}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "galaxyId")
    void add(KnowledgeGalaxy galaxy);

    // 根据名称查询星系
    @Select("SELECT * FROM tab_knowledge_galaxy WHERE name = #{name} AND status = 1")
    KnowledgeGalaxy findByName(String name);

    // 根据ID查询星系
    @Select("SELECT * FROM tab_knowledge_galaxy WHERE galaxy_id = #{galaxyId} AND status = 1")
    KnowledgeGalaxy findById(Integer galaxyId);

    // 更新星系信息
    @Update("UPDATE tab_knowledge_galaxy SET " +
            "name = #{name}, " +
            "theme_tags = #{themeTags}, " +
            "permission_type = #{permissionType}, " +
            "update_time = NOW() " +
            "WHERE galaxy_id = #{galaxyId}")
    void update(KnowledgeGalaxy galaxy);

    // 根据创建者ID查询星系列表
    @Select("SELECT * FROM tab_knowledge_galaxy WHERE creator_id = #{creatorId} AND status = 1 ORDER BY create_time DESC")
    List<KnowledgeGalaxy> findByCreatorId(Integer creatorId);

    // 查询用户加入的星系列表
    @Select("SELECT g.* FROM tab_knowledge_galaxy g " +
            "INNER JOIN tab_galaxy_member m ON g.galaxy_id = m.galaxy_id " +
            "WHERE m.user_id = #{userId} AND g.status = 1 " +
            "ORDER BY g.last_activity_time DESC")
    List<KnowledgeGalaxy> findJoinedGalaxies(Integer userId);

    // 删除星系（软删除）
    @Update("UPDATE tab_knowledge_galaxy SET status = 0, update_time = NOW() WHERE galaxy_id = #{galaxyId}")
    void delete(Integer galaxyId);

    // 搜索公开星系
    @Select("SELECT * FROM tab_knowledge_galaxy " +
            "WHERE permission_type = 0 AND status = 1 " +
            "AND (name LIKE CONCAT('%', #{keyword}, '%') OR theme_tags LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY member_count DESC, last_activity_time DESC")
    List<KnowledgeGalaxy> searchPublicGalaxies(String keyword);

    // 更新最近活动时间
    @Update("UPDATE tab_knowledge_galaxy SET last_activity_time = #{lastActivityTime}, update_time = NOW() " +
            "WHERE galaxy_id = #{galaxyId}")
    void updateLastActivityTime(@Param("galaxyId") Integer galaxyId, @Param("lastActivityTime") Date lastActivityTime);

    // 增加成员数
    @Update("UPDATE tab_knowledge_galaxy SET member_count = member_count + 1, update_time = NOW() " +
            "WHERE galaxy_id = #{galaxyId}")
    void incrementMemberCount(Integer galaxyId);

    // 减少成员数
    @Update("UPDATE tab_knowledge_galaxy SET member_count = member_count - 1, update_time = NOW() " +
            "WHERE galaxy_id = #{galaxyId}")
    void decrementMemberCount(Integer galaxyId);

    // 重置成员计数
    @Update("UPDATE tab_knowledge_galaxy SET member_count = 0, update_time = NOW() " +
            "WHERE galaxy_id = #{galaxyId}")
    void resetMemberCount(Integer galaxyId);

    // 增加星球计数
    @Update("UPDATE tab_knowledge_galaxy SET planet_count = planet_count + 1, update_time = NOW() " +
            "WHERE galaxy_id = #{galaxyId}")
    void increasePlanetCount(Integer galaxyId);

    // 减少星球计数
    @Update("UPDATE tab_knowledge_galaxy SET planet_count = planet_count - 1, update_time = NOW() " +
            "WHERE galaxy_id = #{galaxyId}")
    void decreasePlanetCount(Integer galaxyId);

    // 检查星系是否存在
    @Select("SELECT COUNT(*) FROM tab_knowledge_galaxy WHERE galaxy_id = #{galaxyId} AND status = 1")
    boolean existsById(Integer galaxyId);
}