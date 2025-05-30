package com.example1.demo2.service;

import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.UserDto;
import jakarta.validation.Valid;

import java.util.Date;
import java.util.List;

public interface IUserService {

    //根据用户名查询用户
    User findByNickname(String nickname);

    //注册用户
    void register(String nickname, String password);

    //根据邮箱查询用户
    User findByEmail(String email);

    //删除用户
    void delete(Integer userId);

    //根据ID查用户
    User findById(Integer userId);

    //更新昵称
    void updatenickname(String nickname,Integer userId);

    //更新最后登录时间
    void updatelastLoginTime(Integer userId);

    //更新密码
    void updatepassword(String newpassword, Integer userId);
    
    //更新邮箱
    void updateemail(String email, Integer userId);

    //更新头像url
    void updateurl(String avatarUrl, Integer userId);

    //更新bio
    void updatebio(String bio, Integer userId);

    List<KnowledgePlanet> getAllPlanets(Integer userId);

    List<String> searchPlanetIds(String keyword, Integer userId);

    void updateFavoritePlanet(Integer userId, String planetId);
}
