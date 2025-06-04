package com.example1.demo2.mapper;

import com.example1.demo2.pojo.SystemAdmin;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SystemAdminMapper {

    /**
     * 添加系统管理员
     */
    @Insert("INSERT INTO tab_system_admin(user_id, permissions, scope) " +
            "VALUES(#{userId}, #{permissions}, #{scope})")
    @Options(useGeneratedKeys = true, keyProperty = "adminId", keyColumn = "admin_id")
    void insertSystemAdmin(SystemAdmin admin);

    /**
     * 根据用户ID查询管理员信息
     */
    @Select("SELECT * FROM tab_system_admin WHERE user_id = #{userId} AND status = 0")
    SystemAdmin getAdminByUserId(Integer userId);

    /**
     * 检查用户是否为系统管理员
     */
    @Select("SELECT COUNT(*) > 0 FROM tab_system_admin " +
            "WHERE user_id = #{userId} AND status = 0")
    boolean isSystemAdmin(Integer userId);

    /**
     * 更新管理员权限
     */
    @Update("UPDATE tab_system_admin SET permissions = #{permissions}, " +
            "update_time = now() WHERE admin_id = #{adminId}")
    void updatePermissions(@Param("adminId") Integer adminId,
                           @Param("permissions") String permissions);

    /**
     * 更新管理员状态
     */
    @Update("UPDATE tab_system_admin SET status = #{status}, " +
            "update_time = now() WHERE admin_id = #{adminId}")
    void updateStatus(@Param("adminId") Integer adminId,
                      @Param("status") Integer status);

    /**
     * 获取所有活跃的系统管理员
     */
    @Select("SELECT * FROM tab_system_admin WHERE status = 0")
    List<SystemAdmin> getAllActiveAdmins();

    /**
     * 更新最后登录IP
     */
    @Update("UPDATE tab_system_admin SET last_login_ip = #{ip}, " +
            "update_time = now() WHERE user_id = #{userId}")
    void updateLastLoginIp(@Param("userId") Integer userId,
                           @Param("ip") String ip);
}