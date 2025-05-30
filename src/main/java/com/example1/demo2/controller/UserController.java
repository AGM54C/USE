package com.example1.demo2.controller;

import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.pojo.dto.UserDto;
import com.example1.demo2.pojo.dto.updatePasswordDto;
import com.example1.demo2.service.IUserService;
import com.example1.demo2.service.impl.PlanetService;
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
import java.util.List;
import java.util.Map;

@RestController //接口返回对象，转化为json文本
@RequestMapping("/user")  //localhost:8081/user/**
@Validated
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private PlanetService planetService;

    /**
     * 用户注册接口
     * 前端请求方式：POST
     * 请求URL：localhost:8081/user/register
     * 请求参数（JSON格式）：
     * {
     *   "nickname": String               // 用户昵称（必填）
     *   "password": String               // 原始密码（必填）
     *   "email":String              //邮箱（必填）
     * }
     * 返回值：成功返回成功信息，失败返回错误信息
     */
    @PostMapping("/register")
    public ResponseMessage<String> register(@Valid @RequestBody UserDto user) {
        //查询用户
        User u=userService.findByNickname(user.getNickname());
        if(u!=null) {
            //用户名已经占用
            return ResponseMessage.error("用户名已被占用，请重新输入");
        }
        else{
            //没有占用
            userService.register(user.getNickname(),user.getPassword(),user.getEmail());
            return ResponseMessage.success("用户"+user.getNickname()+"成功注册！");
        }
    }

    /**
     * 用户登录接口
     * 前端请求方式：POST
     * 请求URL：localhost:8081/user/login
     * 请求参数（JSON格式）：
     * {
     *   "nickname": String               // 用户昵称（必填）
     *   "password": String               // 原始密码（必填）
     * }
     * 返回值：成功返回token，失败返回错误信息
     */
    @PostMapping("/login")
    public ResponseMessage<String> login(@Valid @RequestBody UserDto user) {
        //查询用户并匹配密码
        User u=userService.findByNickname(user.getNickname());
        if(u!=null && BCryptUtil.verifyPassword(user.getPassword(),u.getPassword())) {
            //用户登录
            Map<String,Object> claims=new HashMap<>();
            claims.put("userId",u.getUserId());
            String token=JWTUtil.GenToken(claims);

            //更新最后登录时间
            userService.updatelastLoginTime(u.getUserId());

            //返回jwt令牌
            return ResponseMessage.success(token);
        }
        else{
            //用户或密码错误
            return ResponseMessage.error("用户名或密码错误");
        }
    }

    /**
     * 用户信息查看接口
     * 前端请求方式：GET
     * 请求URL：localhost:8081/user/userinfo
     * 请求参数（JSON格式）：以下均为选填，取决于用户想查看的信息
     * {
     *   "nickname": String               // 用户昵称
     *   "password": String               // 加密密码（原则上不允许用户查看密码）
     *   "email":Stirng                   //邮箱
     *   "fuel_value": Integer            //燃料值
     *   ...
     * }
     * 返回值：成功返回整个用户dto，失败返回错误信息
     */
    @GetMapping("/userinfo")
    public ResponseMessage<UserDto> userinfo(){
        //根据id查询,使用全局变量ThreadLocal
        Map<String,Object> map=ThreadLocalUtil.get();
        Integer userId= (Integer)map.get("userId");
        User u = userService.findById(userId);
        if(u==null) {
            return ResponseMessage.error("用户不存在或登陆状态已过期");
        }
        //转化为userdto对象，防止数据泄漏
        UserDto user= ConvertUtil.convertUserToDto(u);
        return ResponseMessage.success(user);
    }

    /**
     * 用户更改昵称接口
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/user/updatenickname
     * 请求参数（JSON格式）：
     * {
     *   "nickname": String               // 用户更新的昵称（必填）
     * }
     * 返回值：成功返回成功信息，失败返回错误信息
     *  注意
     *  所有关于用户信息的更改后都会回收JWT令牌，此时要求用户重新登录更新JWT令牌版本
     */
    @PutMapping("/updatenickname")
    public ResponseMessage updatenickname(@Valid @RequestBody UserDto user) {
        //根据id查找
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId= (Integer)map.get("userId");
        User u1 = userService.findById(userId);
        if(u1==null) {
            return ResponseMessage.error("用户不存在");
        }

        //验证重复昵称
        User u2=userService.findByNickname(user.getNickname());
        if(u2!=null) {
            return ResponseMessage.error("该昵称已被占用");
        }

        //更新信息
        userService.updatenickname(user.getNickname(),userId);
        return ResponseMessage.success("已更新用户信息，请重新登录！");
    }

    /**
     * 用户更改url接口
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/user/updateurl
     * 请求参数（JSON格式）：
     * {
     *   "avatar_url": String               // 用户更新头像url（必填）
     * }
     * 返回值：成功返回成功信息，失败返回错误信息
     *  注意
     *  所有关于用户信息的更改后都会回收JWT令牌，此时要求用户重新登录更新JWT令牌版本
     */
    @PutMapping("/updateurl")
    public ResponseMessage updateurl(@Valid @RequestBody UserDto user) {
        //根据id查找
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId= (Integer)map.get("userId");
        User u = userService.findById(userId);
        if(u==null) {
            return ResponseMessage.error("用户不存在");
        }
        // 判断用户信息是否有变化
        if(u.getAvatarUrl().equals(user.getAvatarUrl())) {
            return ResponseMessage.success("头像没有修改！");
        }

        //更新信息
        userService.updateurl(user.getAvatarUrl(),userId);
        return ResponseMessage.success("已更新用户信息，请重新登录！");
    }

    /**
     * 用户更改bio接口
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/user/updatebio
     * 请求参数（JSON格式）：
     * {
     *   "bio": String               // 用户更新的简介（必填）
     * }
     * 返回值：成功返回成功信息，失败返回错误信息
     *  注意
     *  所有关于用户信息的更改后都会回收JWT令牌，此时要求用户重新登录更新JWT令牌版本
     */
    @PutMapping("/updatebio")
    public ResponseMessage updatebio(@Valid @RequestBody UserDto user) {
        //根据id查找
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId= (Integer)map.get("userId");
        User u = userService.findById(userId);
        if(u==null) {
            return ResponseMessage.error("用户不存在");
        }
        // 判断用户信息是否有变化
        if(u.getBio().equals(user.getBio())) {
            return ResponseMessage.success("简介没有修改！");
        }

        //更新信息
        userService.updatebio(user.getBio(),userId);
        return ResponseMessage.success("已更新用户信息，请重新登录！");
    }

    /**
     * 用户更改密码接口
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/user/updatepassword
     * 请求参数（JSON格式）：
     * {
     *   "oldpassword"：String                //用户原密码（必填）
     *   "newpassword": String               // 用户新密码（必填）
     *   "repassword": String                // 用户二次输入新密码（必填）
     * }
     * 返回值：成功返回成功信息，失败返回错误信息
     *  注意
     *  所有关于用户信息的更改后都会回收JWT令牌，此时要求用户重新登录更新JWT令牌版本
     */
    @PutMapping("/updatepassword")
    public ResponseMessage updatepassword(@Valid @RequestBody updatePasswordDto password){
        //校验参数
        String oldpassword = password.getOldpassword();
        String newpassword = password.getNewpassword();
        String repassword = password.getRepassword();

        //验证原密码
        //根据id查找原密码，使用全局变量ThreadLocal
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId= (Integer) map.get("userId");
        User u=userService.findById(userId);
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
        userService.updatepassword(newpassword,userId);
        return ResponseMessage.success("已更新用户密码,请重新登录！");
    }

    /**
     * 用户更改email接口
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/user/updateemail
     * 请求参数（JSON格式）：
     * {
     *   "email": String               // 用户更新的邮箱（必填）
     * }
     * 返回值：成功返回成功信息，失败返回错误信息
     *  注意
     *  所有关于用户信息的更改后都会回收JWT令牌，此时要求用户重新登录更新JWT令牌版本
     */
    @PutMapping("/updateemail")
    public ResponseMessage updateemail(@Valid @RequestBody UserDto user){
        //获取当前id
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId= (Integer) map.get("userId");
        User u1=userService.findById(userId);
        if(u1==null) {
            return ResponseMessage.error("用户不存在或登录状态已过期");
        }

        //获取邮箱绑定情况
        User u2=userService.findByEmail(user.getEmail());
        if(u2!=null){
            //邮箱已被其他用户绑定
            return ResponseMessage.error("该邮箱已绑定其他用户！");
        }
        userService.updateemail(user.getEmail(),userId);
        return ResponseMessage.success("已更新邮箱绑定，请重新登录！");
    }


    /**
     * 用户注销接口
     * 前端请求方式：Delete
     * 请求URL：localhost:8081/user/delete
     * 请求参数（JSON格式）：
     * {
     *   无
     * }
     * 返回值：成功返回成功信息，失败返回错误信息
     */
    @DeleteMapping("/delete")       //localhost:8081/user/delete
    public ResponseMessage<String> delete() {
        //通过id删除用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId= (Integer) map.get("userId");
        User u=userService.findById(userId);
        if(u==null) {
            //用户不存在
            return ResponseMessage.error("用户不存在或已被删除");
        }

        //删除用户
        userService.delete(userId);
        return ResponseMessage.success("成功删除");
    }

    /**
     * 加载所有星球数据接口
     * 前端请求方式：GET
     * 请求URL：localhost:8081/user/loading
     * 请求参数（JSON格式）：
     * {
     *   无
     * }
     * 返回值：星球列表
     */
    @GetMapping("/loading")
    public ResponseMessage<List<KnowledgePlanet>> loading() {
        //通过id查询用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId= (Integer) map.get("userId");
        User u=userService.findById(userId);
        if(u==null) {
            //用户不存在
            return ResponseMessage.error("用户不存在");
        }

        List<KnowledgePlanet> planets = userService.getAllPlanets(userId);
        return ResponseMessage.success(planets);
    }

    /**
     * 搜索星球
     * 前端请求方式：GET
     * 请求URL：localhost:8081/user/select
     * 请求参数（JSON格式）：
     * {
     *   "keyword":String             //搜索关键词
     * }
     * 返回值：星球ID列表
     */
    @GetMapping("/select")
    public ResponseMessage<List<String>> select(@RequestParam("keyword") String keyword) {
        // 获取当前用户ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        User u=userService.findById(userId);
        if(u==null) {
            //用户不存在
            return ResponseMessage.error("用户不存在");
        }

        if (StringUtils.isBlank(keyword)) {
            return ResponseMessage.error("搜索关键词不能为空");
        }
        List<String> planetIds = userService.searchPlanetIds(keyword,userId);
        return ResponseMessage.success(planetIds);
    }

    /**
     * 选择最喜欢星球
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/user/setfavor
     * 请求参数（JSON格式）：
     * {
     *   "planetId":String             //星球id
     * }
     * 返回
     * 返回值：星球ID
     */
    @PutMapping("/setfavor")
    public ResponseMessage<String> setfavor(@RequestParam("planetId") String planetId) {
        // 获取当前用户ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        User u=userService.findById(userId);
        if(u==null) {
            //用户不存在
            return ResponseMessage.error("用户不存在");
        }

        // 验证星球是否存在
        KnowledgePlanet planet = planetService.findByPlanetId(planetId);
        if (planet == null) {
            return ResponseMessage.error("星球不存在");
        }

        // 更新用户最喜欢的星球
        userService.updateFavoritePlanet(userId, planetId);

        return ResponseMessage.success(planetId);
    }
}
