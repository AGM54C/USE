package com.example1.demo2.mapper;

import com.example1.demo2.pojo.GalaxyMember;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface GalaxyMemberMapper {

    // ==================== 基础查询操作 ====================

    // 根据成员ID查询
    @Select("select * from tab_galaxy_member where member_id = #{memberId}")
    GalaxyMember findById(Integer memberId);

    // 根据星系ID和用户ID查询成员信息
    @Select("select * from tab_galaxy_member where galaxy_id = #{galaxyId} and user_id = #{userId}")
    GalaxyMember findByGalaxyIdAndUserId(@Param("galaxyId") Integer galaxyId, @Param("userId") Integer userId);

    // 分页查询星系成员列表
    @Select("select * from tab_galaxy_member where galaxy_id = #{galaxyId} " +
            "order by role_type desc, join_time asc " +
            "limit #{pageSize} offset #{offset}")
    List<GalaxyMember> findByGalaxyIdWithPaging(@Param("galaxyId") Integer galaxyId,
                                                @Param("offset") Integer offset,
                                                @Param("pageSize") Integer pageSize);

    // 根据用户ID查询所有成员关系
    @Select("select * from tab_galaxy_member where user_id = #{userId} " +
            "order by join_time desc")
    List<GalaxyMember> findByUserId(Integer userId);

    // 根据星系ID和角色类型查询成员列表
    @Select("select * from tab_galaxy_member where galaxy_id = #{galaxyId} and role_type = #{roleType} " +
            "order by join_time asc")
    List<GalaxyMember> findByGalaxyIdAndRoleType(@Param("galaxyId") Integer galaxyId,
                                                 @Param("roleType") Integer roleType);

    // ==================== 统计查询操作 ====================

    // 统计星系成员总数
    @Select("select count(*) from tab_galaxy_member where galaxy_id = #{galaxyId}")
    Integer countByGalaxyId(Integer galaxyId);

    // 统计星系中特定角色的成员数量
    @Select("select count(*) from tab_galaxy_member where galaxy_id = #{galaxyId} and role_type = #{roleType}")
    Integer countByGalaxyIdAndRoleType(@Param("galaxyId") Integer galaxyId,
                                       @Param("roleType") Integer roleType);

    // 统计活跃成员数量（基于最后活动时间）
    @Select("select count(*) from tab_galaxy_member " +
            "where galaxy_id = #{galaxyId} and update_time > #{sinceDate}")
    Integer countActiveMembers(@Param("galaxyId") Integer galaxyId,
                               @Param("sinceDate") Date sinceDate);

    // ==================== 成员管理操作 ====================

    // 添加成员
    @Insert("insert into tab_galaxy_member(galaxy_id, user_id, role_type, join_time, " +
            "operation_permissions, status, create_time, update_time) " +
            "values(#{galaxyId}, #{userId}, #{roleType}, #{joinTime}, " +
            "#{operationPermissions}, #{status}, now(), now())")
    @Options(useGeneratedKeys = true, keyProperty = "memberId")
    void insert(GalaxyMember member);

    // 添加成员（简化版本，用于创建星系时自动添加创建者）
    @Insert("insert into tab_galaxy_member(galaxy_id, user_id, role_type, join_time, " +
            "operation_permissions, status, create_time, update_time) " +
            "values(#{galaxyId}, #{userId}, 2, now(), " +
            "'[\"ALL_PERMISSIONS\"]', 0, now(), now())")
    void addMember(@Param("galaxyId") Integer galaxyId,
                   @Param("userId") Integer userId,
                   @Param("roleDesc") String roleDesc);

    // 删除单个成员
    @Delete("delete from tab_galaxy_member where galaxy_id = #{galaxyId} and user_id = #{userId}")
    void deleteByGalaxyIdAndUserId(@Param("galaxyId") Integer galaxyId,
                                   @Param("userId") Integer userId);

    // 删除星系的所有成员（用于删除星系时的级联删除）
    @Delete("delete from tab_galaxy_member where galaxy_id = #{galaxyId}")
    void deleteByGalaxyId(Integer galaxyId);

    // 移除成员（软删除方式，仅更新状态）
    @Update("update tab_galaxy_member set status = 2, update_time = now() " +
            "where galaxy_id = #{galaxyId} and user_id = #{userId}")
    void removeMember(@Param("galaxyId") Integer galaxyId,
                      @Param("userId") Integer userId);

    // ==================== 角色和权限管理 ====================

    // 更新成员角色和默认权限
    @Update("update tab_galaxy_member set role_type = #{roleType}, " +
            "operation_permissions = #{permissions}, update_time = now() " +
            "where galaxy_id = #{galaxyId} and user_id = #{userId}")
    void updateRole(@Param("galaxyId") Integer galaxyId,
                    @Param("userId") Integer userId,
                    @Param("roleType") Integer roleType,
                    @Param("permissions") String permissions);

    // 更新成员权限
    @Update("update tab_galaxy_member set operation_permissions = #{permissions}, " +
            "update_time = now() " +
            "where galaxy_id = #{galaxyId} and user_id = #{userId}")
    void updatePermissions(@Param("galaxyId") Integer galaxyId,
                           @Param("userId") Integer userId,
                           @Param("permissions") String permissions);

    // ==================== 状态管理操作 ====================

    // 更新成员状态
    @Update("update tab_galaxy_member set status = #{status}, update_time = now() " +
            "where galaxy_id = #{galaxyId} and user_id = #{userId}")
    void updateStatus(@Param("galaxyId") Integer galaxyId,
                      @Param("userId") Integer userId,
                      @Param("status") Integer status);

    // 更新成员最后活动时间
    @Update("update tab_galaxy_member set update_time = #{activityTime} " +
            "where galaxy_id = #{galaxyId} and user_id = #{userId}")
    void updateLastActivityTime(@Param("galaxyId") Integer galaxyId,
                                @Param("userId") Integer userId,
                                @Param("activityTime") Date activityTime);

    // ==================== 工具查询操作 ====================

    // 检查用户是否为星系成员
    @Select("select exists(select 1 from tab_galaxy_member " +
            "where galaxy_id = #{galaxyId} and user_id = #{userId} and status = 0)")
    boolean isMember(@Param("galaxyId") Integer galaxyId,
                     @Param("userId") Integer userId);

    // 获取用户在特定星系的角色类型
    @Select("select role_type from tab_galaxy_member " +
            "where galaxy_id = #{galaxyId} and user_id = #{userId} and status = 0")
    Integer getUserRoleType(@Param("galaxyId") Integer galaxyId,
                            @Param("userId") Integer userId);

    // ==================== 批量操作支持 ====================

    // 批量插入成员（用于批量邀请）
    @Insert("<script>" +
            "insert into tab_galaxy_member(galaxy_id, user_id, role_type, join_time, " +
            "operation_permissions, status, create_time, update_time) values " +
            "<foreach collection='members' item='member' separator=','>" +
            "(#{member.galaxyId}, #{member.userId}, #{member.roleType}, #{member.joinTime}, " +
            "#{member.operationPermissions}, #{member.status}, now(), now())" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("members") List<GalaxyMember> members);

    // 批量更新状态（用于批量禁用/启用）
    @Update("<script>" +
            "update tab_galaxy_member set status = #{status}, update_time = now() " +
            "where galaxy_id = #{galaxyId} and user_id in " +
            "<foreach collection='userIds' item='userId' open='(' separator=',' close=')'>" +
            "#{userId}" +
            "</foreach>" +
            "</script>")
    void batchUpdateStatus(@Param("galaxyId") Integer galaxyId,
                           @Param("userIds") List<Integer> userIds,
                           @Param("status") Integer status);
}