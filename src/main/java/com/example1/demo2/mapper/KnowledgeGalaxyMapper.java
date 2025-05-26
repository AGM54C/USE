package com.example1.demo2.mapper;

import com.example1.demo2.pojo.KnowledgeGalaxy;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface KnowledgeGalaxyMapper {

    // 添加星系
    @Insert("insert into tab_knowledge_galaxy(name, theme_tags, permission_type, creator_id, member_count, last_activity_time, create_time, update_time) " +
            "values(#{name}, #{themeTags}, #{permissionType}, #{creatorId}, #{memberCount}, #{lastActivityTime}, now(), now())")
    @Options(useGeneratedKeys = true, keyProperty = "galaxyId")
    void add(KnowledgeGalaxy galaxy);

    // 根据名称查询星系
    @Select("select * from tab_knowledge_galaxy where name = #{name}")
    KnowledgeGalaxy findByName(String name);

    // 根据ID查询星系
    @Select("select * from tab_knowledge_galaxy where galaxy_id = #{galaxyId}")
    KnowledgeGalaxy findById(Integer galaxyId);

    // 更新星系信息
    @Update("update tab_knowledge_galaxy set name = #{name}, theme_tags = #{themeTags}, " +
            "permission_type = #{permissionType}, update_time = now() " +
            "where galaxy_id = #{galaxyId}")
    void update(KnowledgeGalaxy galaxy);

    // 根据创建者ID查询星系列表
    @Select("select * from tab_knowledge_galaxy where creator_id = #{creatorId} order by create_time desc")
    List<KnowledgeGalaxy> findByCreatorId(Integer creatorId);

    // 查询用户加入的星系列表
    @Select("select g.* from tab_knowledge_galaxy g " +
            "inner join tab_galaxy_member m on g.galaxy_id = m.galaxy_id " +
            "where m.user_id = #{userId} order by g.last_activity_time desc")
    List<KnowledgeGalaxy> findJoinedGalaxies(Integer userId);

    // 删除星系
    @Delete("delete from tab_knowledge_galaxy where galaxy_id = #{galaxyId}")
    void delete(Integer galaxyId);

    // 搜索公开星系
    @Select("select * from tab_knowledge_galaxy where permission_type = 0 " +
            "and (name like concat('%', #{keyword}, '%') or theme_tags like concat('%', #{keyword}, '%')) " +
            "order by member_count desc, last_activity_time desc")
    List<KnowledgeGalaxy> searchPublicGalaxies(String keyword);

    // 更新最近活动时间
    @Update("update tab_knowledge_galaxy set last_activity_time = #{lastActivityTime} " +
            "where galaxy_id = #{galaxyId}")
    void updateLastActivityTime(@Param("galaxyId") Integer galaxyId, @Param("lastActivityTime") Date lastActivityTime);

    // 增加成员数
    @Update("update tab_knowledge_galaxy set member_count = member_count + 1 " +
            "where galaxy_id = #{galaxyId}")
    void incrementMemberCount(Integer galaxyId);

    // 减少成员数
    @Update("update tab_knowledge_galaxy set member_count = member_count - 1 " +
            "where galaxy_id = #{galaxyId}")
    void decrementMemberCount(Integer galaxyId);

    //重置成员计数
    @Update("update tab_knowledge_galaxy set member_couunt=0"+
    " where galaxy_id = #{galaxyId}")
    void resetMemberCount(Integer galaxyId);
}