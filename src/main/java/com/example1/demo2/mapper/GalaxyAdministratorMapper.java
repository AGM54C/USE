package com.example1.demo2.mapper;

import com.example1.demo2.pojo.GalaxyAdministrator;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface GalaxyAdministratorMapper {

    /**
     * 添加星系管理员
     */
    @Insert("INSERT INTO tab_galaxy_administrator(galaxy_id, user_id, role_type, " +
            "permissions, appointed_by, appoint_time) " +
            "VALUES(#{galaxyId}, #{userId}, #{roleType}, #{permissions}, " +
            "#{appointedBy}, now())")
    @Options(useGeneratedKeys = true, keyProperty = "adminId", keyColumn = "admin_id")
    void insertGalaxyAdmin(@Param("galaxyId") Integer galaxyId,
                           @Param("userId") Integer userId,
                           @Param("roleType") Integer roleType,
                           @Param("permissions") String permissions,
                           @Param("appointedBy") Integer appointedBy);

    /**
     * 检查用户是否为星系管理员
     */
    @Select("SELECT COUNT(*) > 0 FROM tab_galaxy_administrator " +
            "WHERE galaxy_id = #{galaxyId} AND user_id = #{userId} AND status = 0")
    boolean isGalaxyAdmin(@Param("galaxyId") Integer galaxyId,
                          @Param("userId") Integer userId);

    /**
     * 获取星系的所有管理员
     */
    @Select("SELECT * FROM tab_galaxy_administrator WHERE galaxy_id = #{galaxyId} " +
            "AND status = 0 ORDER BY appoint_time DESC")
    List<GalaxyAdministrator> getGalaxyAdmins(Integer galaxyId);

    /**
     * 撤销管理员权限
     */
    @Update("UPDATE tab_galaxy_administrator SET status = 1 " +
            "WHERE galaxy_id = #{galaxyId} AND user_id = #{userId}")
    void revokeAdmin(@Param("galaxyId") Integer galaxyId,
                     @Param("userId") Integer userId);

    /**
     * 获取用户管理的所有星系
     */
    @Select("SELECT galaxy_id FROM tab_galaxy_administrator " +
            "WHERE user_id = #{userId} AND status = 0")
    List<Integer> getUserManagedGalaxies(Integer userId);
}