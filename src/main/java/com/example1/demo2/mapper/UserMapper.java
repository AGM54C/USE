package com.example1.demo2.mapper;

import com.example1.demo2.pojo.User;
import org.apache.ibatis.annotations.*;


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
}
