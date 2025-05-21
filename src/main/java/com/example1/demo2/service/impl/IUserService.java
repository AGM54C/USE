package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.UserMapper;
import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.UserDto;
import com.example1.demo2.service.UserService;
import com.example1.demo2.util.BCryptUtil;
import com.example1.demo2.util.ConvertUtil;
import com.example1.demo2.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class IUserService implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByNickname(String nickname) {
        return userMapper.findByNickname(nickname);
    }

    @Override
    public User findByEmail(String Email){
        return userMapper.findByEmail(Email);
    }


    @Override
    public void delete(Integer Userid) {
        userMapper.delete(Userid);
    }

    @Override
    public User findById(Integer Userid) {
        return userMapper.findById(Userid);
    }

    @Override
    public void update(UserDto user) {
        //先把Dto转化成user，用于数据库操作
        User u=ConvertUtil.convertDtoToUser(user);
        userMapper.update(u);
    }

    @Override
    public void updatelastLoginTime(User u) {
        userMapper.updatelastLoginTime(u);
    }

    @Override
    public void updatepassword(String newpassword) {
        //使用全局变量ThreadLocal
        Map<String,Object> map= ThreadLocalUtil.get();
        Integer userId=(Integer)map.get("userId");
        String newpasswordHash=BCryptUtil.hashPassword(newpassword);
        userMapper.updatepassword(newpasswordHash,userId);
    }

    @Override
    public User findByMobile(String mobile) { return userMapper.findByMobile(mobile); }

    @Override
    public void updatemobile(String mobile) {
        //使用全局变量ThreadLocal
        Map<String,Object> map= ThreadLocalUtil.get();
        Integer userId=(Integer)map.get("userId");
        userMapper.updatemobile(mobile,userId);
    }

    @Override
    public void updateemail(String email) {
        //使用全局变量ThreadLocal
        Map<String,Object> map= ThreadLocalUtil.get();
        Integer userId=(Integer)map.get("userId");
        userMapper.updateemail(email,userId);
    }

    @Override
    public void register(String nickname, String password) {
        //加密处理
        String passwordHash= BCryptUtil.hashPassword(password);
        //添加
        userMapper.add(nickname,passwordHash);
    }


}
