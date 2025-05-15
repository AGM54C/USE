package com.example1.demo2.mapper;

import com.example1.demo2.pojo.User;
import org.apache.ibatis.annotations.*;


@Mapper
public interface UserMapper {
    //添加
    @Insert("insert into tab_user(nickname,password,create_time,update_time,token_version)"+" values(#{nickname},#{passwordHash},now(),now(),0)")
    void add(String nickname, String passwordHash) ;

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

    //根据手机查询用户
    @Select("select * from tab_user where mobile=#{mobile}")
    User findByMobile(String mobile);

    //更新用户基本信息
    @Update("update tab_user set nickname=#{nickname}" +
            ",bio=#{bio}" +
            ",avatar_url=#{avatarUrl}" +
            ",update_time=now()" +
            ",token_version=token_version+1"+
            " where user_id=#{userId}")
    void update(User u);

    //更新用户最后登录时间
    @Update("update tab_user set last_login_time=now() where user_id=#{userId}")
    void updatelastLoginTime(User u);

    //更新用户密码
    @Update("update tab_user set password=#{newpasswordHash}" +
            ",token_version=token_version+1"+
            " where user_id=#{userId}")
    void updatepassword(String newpasswordHash,Integer userId);

    //更新用户邮箱和邮箱绑定状态
    @Update("update tab_user set email=#{email} " +
            ",token_version=token_version+1"+
            ",email_verified=1" +
            " where user_id=#{userId}")
    void updateemail(String email,Integer userId);

    //更新用户手机号和手机绑定状态
    @Update("update tab_user set mobile=#{mobile}" +
            ",token_version=token_version+1"+
            ",mobile_verified=1"+
            " where user_id=#{userId}")
    void updatemobile(String mobile,Integer userId);
}
