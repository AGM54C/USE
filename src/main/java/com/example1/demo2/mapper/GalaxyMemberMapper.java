package com.example1.demo2.mapper;

import com.example1.demo2.pojo.GalaxyMember;
import com.example1.demo2.pojo.KnowledgePlanet;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface GalaxyMemberMapper {

    // ==================== 基础查询操作 ====================

    @Select("SELECT * FROM tab_galaxy_member WHERE member_id = #{memberId} AND status = 0")
    GalaxyMember findById(Integer memberId);

    @Select("SELECT * FROM tab_galaxy_member WHERE galaxy_id = #{galaxyId} AND user_id = #{userId} AND status = 0")
    GalaxyMember findByGalaxyIdAndUserId(@Param("galaxyId") Integer galaxyId, @Param("userId") Integer userId);

    @Select("SELECT * FROM tab_galaxy_member WHERE galaxy_id = #{galaxyId} AND status = 0 " +
            "ORDER BY role_type DESC, join_time ASC " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<GalaxyMember> findByGalaxyIdWithPaging(@Param("galaxyId") Integer galaxyId,
                                                @Param("offset") Integer offset,
                                                @Param("pageSize") Integer pageSize);

    @Select("SELECT * FROM tab_galaxy_member WHERE user_id = #{userId} AND status = 0 " +
            "ORDER BY join_time DESC")
    List<GalaxyMember> findByUserId(Integer userId);

    @Select("SELECT * FROM tab_galaxy_member WHERE galaxy_id = #{galaxyId} AND role_type = #{roleType} AND status = 0 " +
            "ORDER BY join_time ASC")
    List<GalaxyMember> findByGalaxyIdAndRoleType(@Param("galaxyId") Integer galaxyId,
                                                 @Param("roleType") Integer roleType);

    // ==================== 统计查询操作 ====================

    @Select("SELECT COUNT(*) FROM tab_galaxy_member WHERE galaxy_id = #{galaxyId} AND status = 0")
    Integer countByGalaxyId(Integer galaxyId);

    @Select("SELECT COUNT(*) FROM tab_galaxy_member WHERE galaxy_id = #{galaxyId} AND role_type = #{roleType} AND status = 0")
    Integer countByGalaxyIdAndRoleType(@Param("galaxyId") Integer galaxyId,
                                       @Param("roleType") Integer roleType);

    @Select("SELECT COUNT(*) FROM tab_galaxy_member " +
            "WHERE galaxy_id = #{galaxyId} AND status = 0 AND update_time > #{sinceDate}")
    Integer countActiveMembers(@Param("galaxyId") Integer galaxyId,
                               @Param("sinceDate") Date sinceDate);

    // ==================== 成员管理操作 ====================

    @Insert("INSERT INTO tab_galaxy_member(galaxy_id, user_id, role_type, role_desc, join_time, " +
            "operation_permissions, status, create_time, update_time) " +
            "VALUES(#{galaxyId}, #{user.userId}, #{roleType}, #{roleDesc}, NOW(), " +
            "#{operationPermissions}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "memberId")
    void insert(GalaxyMember member);

    @Insert("INSERT INTO tab_galaxy_member(galaxy_id, user_id, role_type, role_desc, join_time, " +
            "operation_permissions, status, create_time, update_time) " +
            "VALUES(#{galaxyId}, #{userId.userId}, 2, #{roleDesc}, NOW(), " +
            "'[\"ALL_PERMISSIONS\"]', 1, NOW(), NOW())")
    void addMember(@Param("galaxyId") Integer galaxyId,
                   @Param("userId") Integer userId,
                   @Param("roleDesc") String roleDesc);

    @Delete("DELETE FROM tab_galaxy_member WHERE galaxy_id = #{galaxyId} AND user_id = #{userId}")
    void deleteByGalaxyIdAndUserId(@Param("galaxyId") Integer galaxyId,
                                   @Param("userId") Integer userId);

    @Delete("DELETE FROM tab_galaxy_member WHERE galaxy_id = #{galaxyId}")
    void deleteByGalaxyId(Integer galaxyId);

    @Update("UPDATE tab_galaxy_member SET status = 2, update_time = NOW() " +
            "WHERE galaxy_id = #{galaxyId} AND user_id = #{userId.userId}")
    void removeMember(@Param("galaxyId") Integer galaxyId,
                      @Param("userId") Integer userId);

    // ==================== 星球相关操作 ====================

    @Insert("INSERT INTO tab_planet_member(galaxy_id, planet_id, user_id, role_type, join_time, " +
            "status, create_time, update_time) " +
            "VALUES(#{galaxyId}, #{planet.planetId}, #{userId}, 1, NOW(), 1, NOW(), NOW())")
    void addPlanet(@Param("galaxyId") Integer galaxyId,
                   @Param("planet") KnowledgePlanet knowledgePlanet,
                   @Param("roleDesc") String roleDesc);

    @Delete("DELETE FROM tab_planet_member WHERE galaxy_id = #{galaxyId} AND planet_id = #{planet.planetId}")
    void removePlanet(@Param("galaxyId") Integer galaxyId,
                      @Param("planet") KnowledgePlanet knowledgePlanet);

    // ==================== 角色和权限管理 ====================

    @Update("UPDATE tab_galaxy_member SET role_type = #{roleType}, " +
            "operation_permissions = #{permissions}, update_time = NOW() " +
            "WHERE galaxy_id = #{galaxyId} AND user_id = #{userId}")
    void updateRole(@Param("galaxyId") Integer galaxyId,
                    @Param("userId") Integer userId,
                    @Param("roleType") Integer roleType,
                    @Param("permissions") String permissions);

    @Update("UPDATE tab_galaxy_member SET operation_permissions = #{permissions}, " +
            "update_time = NOW() " +
            "WHERE galaxy_id = #{galaxyId} AND user_id = #{userId}")
    void updatePermissions(@Param("galaxyId") Integer galaxyId,
                           @Param("userId") Integer userId,
                           @Param("permissions") String permissions);

    // ==================== 状态管理操作 ====================

    @Update("UPDATE tab_galaxy_member SET status = #{status}, update_time = NOW() " +
            "WHERE galaxy_id = #{galaxyId} AND user_id = #{userId}")
    void updateStatus(@Param("galaxyId") Integer galaxyId,
                      @Param("userId") Integer userId,
                      @Param("status") Integer status);

    @Update("UPDATE tab_galaxy_member SET update_time = #{activityTime} " +
            "WHERE galaxy_id = #{galaxyId} AND user_id = #{userId}")
    void updateLastActivityTime(@Param("galaxyId") Integer galaxyId,
                                @Param("userId") Integer userId,
                                @Param("activityTime") Date activityTime);

    // ==================== 工具查询操作 ====================

    @Select("SELECT EXISTS(SELECT 1 FROM tab_galaxy_member " +
            "WHERE galaxy_id = #{galaxyId} AND user_id = #{userId} AND status = 1)")
    boolean isMember(@Param("galaxyId") Integer galaxyId,
                     @Param("userId") Integer userId);

    @Select("SELECT role_type FROM tab_galaxy_member " +
            "WHERE galaxy_id = #{galaxyId} AND user_id = #{userId} AND status = 1")
    Integer getUserRoleType(@Param("galaxyId") Integer galaxyId,
                            @Param("userId") Integer userId);

    // ==================== 批量操作支持 ====================

    @Insert("<script>" +
            "INSERT INTO tab_galaxy_member(galaxy_id, user_id, role_type, role_desc, join_time, " +
            "operation_permissions, status, create_time, update_time) VALUES " +
            "<foreach collection='members' item='member' separator=','>" +
            "(#{member.galaxyId}, #{member.userId}, #{member.roleType}, #{member.roleDesc}, NOW(), " +
            "#{member.operationPermissions}, #{member.status}, NOW(), NOW())" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("members") List<GalaxyMember> members);

    @Update("<script>" +
            "UPDATE tab_galaxy_member SET status = #{status}, update_time = NOW() " +
            "WHERE galaxy_id = #{galaxyId} AND user_id IN " +
            "<foreach collection='userIds' item='userId' open='(' separator=',' close=')'>" +
            "#{userId}" +
            "</foreach>" +
            "</script>")
    void batchUpdateStatus(@Param("galaxyId") Integer galaxyId,
                           @Param("userIds") List<Integer> userIds,
                           @Param("status") Integer status);
}