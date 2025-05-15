package com.example1.demo2.service;

import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.UserDto;
import jakarta.validation.Valid;

public interface UserService {

    //根据用户名查询用户
    User findByNickname(String nickname);

    //注册用户
    void register(String nickname, String password);

    //根据邮箱查询用户
    User findByEmail(String email);

    //删除用户
    void delete(Integer UserId);

    //根据ID查用户
    User findById(Integer UserId);

    //更新用户基本信息
    void update(UserDto user);

    //更新最后登录时间
    void updatelastLoginTime(User u);

    //更新密码
    void updatepassword(String newpassword);

    //根据手机查用户
    User findByMobile(String mobile);

    //更新手机
    void updatemobile(String mobile);

    //更新邮箱
    void updateemail(String email);
}
