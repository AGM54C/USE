package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.PlanetMapper;
import com.example1.demo2.mapper.UserMapper;
import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.UserDto;
import com.example1.demo2.service.IUserService;
import com.example1.demo2.util.BCryptUtil;
import com.example1.demo2.util.ConvertUtil;
import com.example1.demo2.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class UserService implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PlanetMapper planetMapper;

    @Override
    public User findByNickname(String nickname) {
        return userMapper.findByNickname(nickname);
    }

    @Override
    public User findByEmail(String Email){
        return userMapper.findByEmail(Email);
    }


    @Override
    public void delete(Integer userId) {
        userMapper.delete(userId);
    }

    @Override
    public User findById(Integer userId) {
        return userMapper.findById(userId);
    }

    @Override
    public void updatenickname(String nickname,Integer userId) {
        userMapper.updatenickname(nickname,userId);
    }

    @Override
    public void updatelastLoginTime(Integer userId) {
        userMapper.updatelastLoginTime(userId);
    }

    @Override
    public void updatepassword(String newpassword, Integer userId) {
        String passwordhash=BCryptUtil.hashPassword(newpassword);
        userMapper.updatepassword(newpassword,userId);
    }

    @Override
    public void updateemail(String email, Integer userId) {
        userMapper.updateemail(email,userId);
    }

    @Override
    public void updateurl(String avatarUrl, Integer userId) {
        userMapper.updateurl(avatarUrl,userId);
    }

    @Override
    public void updatebio(String bio, Integer userId) {
        userMapper.updatebio(bio,userId);
    }

    @Override
    public List<KnowledgePlanet> getAllPlanets(Integer userId) {
        return planetMapper.selectAll(userId);
    }

    @Override
    public List<String> searchPlanetIds(String keyword, Integer userId) {
        return planetMapper.searchIdsByKeyword(keyword,userId);
    }

    @Override
    public void updateFavoritePlanet(Integer userId, String planetId) {
        userMapper.updateFavoritePlanet(userId, planetId);
    }

    @Override
    public void register(String nickname, String password) {
        //加密处理
        String passwordHash= BCryptUtil.hashPassword(password);
        //添加
        userMapper.add(nickname,passwordHash);
    }


}
