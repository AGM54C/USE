package com.example1.demo2.mapper;

import com.example1.demo2.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface UserMapper {
    //添加
    @Insert("insert into tab_user(nickname,password,email,create_time,update_time)"+" values(#{nickname},#{passwordHash},#{email},now(),now())")
    void add(String nickname, String passwordHash,String email) ;

    //根据用户名查询用户
    @Select("select * from tab_user where nickname=#{nickname}")
    User findByNickname(String nickname);

    //根据邮箱查询用户
    @Select("select * from tab_user where email=#{email}")
    User findByEmail(String email);

    //删除
    @Delete("delete from tab_user where user_id=#{Userid}")
    void delete(Integer Userid);

    //根据ID查询用户
    @Select("select * from tab_user where user_id=#{Userid}")
    User findById(Integer Userid);

    //更新用户最后登录时间
    @Update("update tab_user set last_login_time=now() where user_id=#{userId}")
    void updatelastLoginTime(Integer Userid);

    //更新用户密码
    @Update("update tab_user set password=#{newpasswordHash}" +
            " where user_id=#{userId}")
    void updatepassword(String newpasswordHash,Integer userId);

    //更新用户邮箱和邮箱绑定状态
    @Update("update tab_user set email=#{email} " +
            ",email_verified=1" +
            " where user_id=#{userId}")
    void updateemail(String email,Integer userId);


    //更新简介
    @Update("update tab_user set bio=#{bio} " +
            " where user_id=#{userId}")
    void updatebio(String bio, Integer userId);

    //更新头像url
    @Update("update tab_user set avatar_url=#{avatarUrl} " +
            " where user_id=#{userId}")
    void updateurl(String avatarUrl, Integer userId);

    //更新昵称
    @Update("update tab_user set nickname=#{nickname} " +
            " where user_id=#{userId}")
    void updatenickname(String nickname, Integer userId);

    //更新最喜欢星球
    @Update("update tab_user set favorite_planet_id=#{planetId} " +
            " where user_id=#{userId}")
    void updateFavoritePlanet(Integer userId, String planetId);

    //更新最喜欢星系
    @Update("update tab_user set favorite_galaxy_id=#{galaxyId} " +
            " where user_id=#{userId}")
    void updateFavoriteGalaxy(Integer userId, Integer galaxyId);

    /**
     * 封禁用户
     */
    @Update("UPDATE tab_user SET status = 1 WHERE user_id = #{userId}")
    void banUser(Integer userId);

    /**
     * 解封用户
     */
    @Update("UPDATE tab_user SET status = 0 WHERE user_id = #{userId}")
    void unbanUser(Integer userId);

    /**
     * 搜索用户（用于添加好友）
     */
    @Select("<script>" +
            "SELECT * FROM tab_user WHERE status = 0 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (nickname LIKE CONCAT('%', #{keyword}, '%') " +
            "OR email LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if> " +
            "AND user_id != #{excludeUserId} " +
            "LIMIT #{limit}" +
            "</script>")
    List<User> searchUsers(@Param("keyword") String keyword,
                           @Param("excludeUserId") Integer excludeUserId,
                           @Param("limit") int limit);

    /**
     * 更新用户燃料值
     * @param userId 用户ID
     * @param fuelValue 新的燃料值
     */
    @Update("UPDATE tab_user SET fuel_value = #{fuelValue} WHERE user_id = #{userId}")
    void updateFuelValue(@Param("userId") Integer userId, @Param("fuelValue") Integer fuelValue);

    /**
     * 更新用户知识星云值
     * @param userId 用户ID
     * @param knowledgeDust 新的知识星云值
     */
    @Update("UPDATE tab_user SET knowledge_dust = #{knowledgeDust} WHERE user_id = #{userId}")
    void updateKnowledgeDust(@Param("userId") Integer userId, @Param("knowledgeDust") Integer knowledgeDust);

    /**
     * 增加用户燃料值
     * @param userId 用户ID
     * @param amount 增加的数量
     */
    @Update("UPDATE tab_user SET fuel_value = fuel_value + #{amount} WHERE user_id = #{userId}")
    void increaseFuelValue(@Param("userId") Integer userId, @Param("amount") Integer amount);

    /**
     * 减少用户燃料值（确保不会小于0）
     * @param userId 用户ID
     * @param amount 减少的数量
     */
    @Update("UPDATE tab_user SET fuel_value = GREATEST(fuel_value - #{amount}, 0) WHERE user_id = #{userId}")
    void decreaseFuelValue(@Param("userId") Integer userId, @Param("amount") Integer amount);

    /**
     * 增加用户知识星云值
     * @param userId 用户ID
     * @param amount 增加的数量
     */
    @Update("UPDATE tab_user SET knowledge_dust = knowledge_dust + #{amount} WHERE user_id = #{userId}")
    void increaseKnowledgeDust(@Param("userId") Integer userId, @Param("amount") Integer amount);

    /**
     * 减少用户知识星云值（确保不会小于0）
     * @param userId 用户ID
     * @param amount 减少的数量
     */
    @Update("UPDATE tab_user SET knowledge_dust = GREATEST(knowledge_dust - #{amount}, 0) WHERE user_id = #{userId}")
    void decreaseKnowledgeDust(@Param("userId") Integer userId, @Param("amount") Integer amount);

}
