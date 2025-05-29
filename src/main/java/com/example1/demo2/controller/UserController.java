package com.example1.demo2.controller;

import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.pojo.dto.UserDto;
import com.example1.demo2.pojo.dto.updatePasswordDto;
import com.example1.demo2.service.IUserService;
import com.example1.demo2.util.BCryptUtil;
import com.example1.demo2.util.ConvertUtil;
import com.example1.demo2.util.JWTUtil;
import com.example1.demo2.util.ThreadLocalUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController //接口返回对象，转化为json文本
@RequestMapping("/user")  //localhost:8081/user/**
@Validated
public class UserController {
    @Autowired
    private IUserService userService;

    //用户注册（必要参数：用户名，密码,返回值：用户ID）
    @PostMapping("/register")         //localhost:8081/user/register
    public ResponseMessage<String> register(@Valid @RequestBody UserDto user) {
        //查询用户
        System.out.println(user);
        User u=userService.findByNickname(user.getNickname());
        System.out.println(u);
        if(u!=null) {
            //用户名已经占用
            return ResponseMessage.error("用户名已被占用，请重新输入");
        }
        else{
            //没有占用
            userService.register(user.getNickname(),user.getPassword());
            u=userService.findByNickname(user.getNickname());
            return ResponseMessage.success(u.getUserId());
        }
    }

    //用户登录（必要参数：用户名，密码，返回值：登录JWT令牌）
    @PostMapping("/login")       //localhost:8081/user/login
    public ResponseMessage<String> login(@Valid @RequestBody UserDto user) {
        //查询用户并匹配密码
        User u=userService.findByNickname(user.getNickname());
        if(u!=null && BCryptUtil.verifyPassword(user.getPassword(),u.getPassword())) {
            //用户登录
            Map<String,Object> claims=new HashMap<>();
            claims.put("nickname",u.getNickname());
            //claims.put("password",u.getPassword());
            claims.put("userId",u.getUserId());
            claims.put("tokenVersion",u.getTokenVersion());
            String token=JWTUtil.GenToken(claims);

            //更新最后登录时间
            userService.updatelastLoginTime(u);

            //返回jwt令牌
            return ResponseMessage.success(token);
        }
        else{
            //用户或密码错误
            return ResponseMessage.error("用户名或密码错误");
        }
    }

    //查询用户信息（必要参数：用户登录JWT令牌，返回值：用户列表信息）
    @GetMapping("/userinfo")
    public ResponseMessage<UserDto> userinfo(){
        //根据用户名查询,使用全局变量ThreadLocal
        Map<String,Object> map=ThreadLocalUtil.get();
        String nickname= (String)map.get("nickname");
        User u=userService.findByNickname(nickname);
        if(u==null) {
            return ResponseMessage.error("用户不存在或登陆状态已过期");
        }
        //转化为userdto对象，防止数据泄漏
        UserDto user= ConvertUtil.convertUserToDto(u);
        return ResponseMessage.success(user);
    }

    /*
        ***注意***
        所有关于用户信息的更改后都会回收JWT令牌，此时要求用户重新登录更新JWT令牌版本
     */
    //更新用户基本信息(nickname,bio,url,update_time，返回值：成功或失败信息）
    @PutMapping("/update")
    public ResponseMessage update(@Valid @RequestBody UserDto user) {
        User u = userService.findById(user.getUserId());

        if(u==null) {
            return ResponseMessage.error("用户不存在");
        }
        // 判断用户信息是否有变化
        boolean isChanged = false;
        if (!Objects.equals(user.getNickname(), u.getNickname())) isChanged = true;
        if (!Objects.equals(user.getBio(), u.getBio())) isChanged = true;
        if (!Objects.equals(user.getAvatarUrl(), u.getAvatarUrl())) isChanged = true;

        if (!isChanged) {
            return ResponseMessage.success("没有需要修改的信息！");
        }

        //更新信息
        userService.update(user);
        return ResponseMessage.success("已更新用户信息，请重新登录！");
    }

    //更新密码（必要参数：原密码，新密码，二次输入新密码,返回值：成功或失败信息）
    @PutMapping("/updatepassword")
    public ResponseMessage updatepassword(@Valid @RequestBody updatePasswordDto password){
        //校验参数
        String oldpassword = password.getOldpassword();
        String newpassword = password.getNewpassword();
        String repassword = password.getRepassword();
        if(StringUtils.isEmpty(oldpassword)||StringUtils.isEmpty(newpassword)||StringUtils.isEmpty(repassword)){
            return ResponseMessage.error("缺少必要的参数!");
        }
        //验证原密码
        //根据用户名查找原密码，使用全局变量ThreadLocal
        Map<String,Object> map = ThreadLocalUtil.get();
        String nickname= (String)map.get("nickname");
        User u=userService.findByNickname(nickname);
        if(u==null) {
            return ResponseMessage.error("用户不存在或登录状态已过期");
        }
        if(!BCryptUtil.verifyPassword(oldpassword,u.getPassword())) {
            return ResponseMessage.error("原密码输入不正确！");
        }

        //验证新密码和旧密码
        if(BCryptUtil.verifyPassword(newpassword,u.getPassword())) {
            return ResponseMessage.success("新密码不能和原密码相同！");
        }

        //验证二次输入密码和新密码
        if(!newpassword.equals(repassword)){
            return ResponseMessage.error("两次输入的密码不相同！");
        }

        //完成密码更新
        userService.updatepassword(newpassword);
        return ResponseMessage.success("已更新用户密码,请重新登录！");
    }

    //更新绑定手机（必要参数：手机号，用户id，返回值：成功或失败信息）
    @PutMapping("/updatemobile")
    public ResponseMessage updatemobile(@Valid @RequestBody UserDto user){
        User u=userService.findByMobile(user.getMobile());
        if(u!=null){
            //手机已被其他用户绑定
            return ResponseMessage.error("该手机已绑定其他用户！");
        }
        userService.updatemobile(user.getMobile());
        return ResponseMessage.success("已更新手机绑定,请重新登录！");
    }

    //更新绑定邮箱（必要参数：邮箱，返回值：成功或失败信息）
    @PutMapping("/updateemail")
    public ResponseMessage updateemail(@Valid @RequestBody UserDto user){
        User u=userService.findByEmail(user.getEmail());
        if(u!=null){
            //邮箱已被其他用户绑定
            return ResponseMessage.error("该邮箱已绑定其他用户！");
        }
        userService.updateemail(user.getEmail());
        return ResponseMessage.success("已更新邮箱绑定，请重新登录！");
    }


    //注销用户（必要参数，用户ID，返回信息：成功或失败信息）
    @DeleteMapping("/delete")       //localhost:8081/user/delete
    public ResponseMessage<String> delete(@RequestBody UserDto user) {
        //通过id删除用户
        User u=userService.findById(user.getUserId());
        if(u==null) {
            //用户不存在
            return ResponseMessage.error("用户不存在或已被删除");
        }
        else{
            //删除用户
            userService.delete(user.getUserId());
            return ResponseMessage.success("成功删除");
        }
    }

}
