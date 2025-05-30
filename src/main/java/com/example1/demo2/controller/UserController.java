package com.example1.demo2.controller;

import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.KnowledgeGalaxyDto;
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
import java.util.stream.Collectors;

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
     * 请求URL：localhost:8081/user/loadingplanets
     * 请求参数（JSON格式）：
     * {
     *   无
     * }
     * 返回值：星球列表
     */
    @GetMapping("/loadingplanets")
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
     * 搜索星球(模糊搜索)
     * 前端请求方式：GET
     * 请求URL：localhost:8081/user/selectplanet
     * 请求参数（Param格式）：
     * {
     *   "title":String             //搜索关键词
     * }
     * 返回值：星球列表
     */
    @GetMapping("/selectplanet")
    public ResponseMessage<List<String>> selectplanet(@RequestParam("title") String keyword) {
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

        // 搜索星球ID列表
        List<String> planetIds = userService.searchPlanetIds(keyword, userId);
        return ResponseMessage.success(planetIds);
    }

    /**
     * 选择最喜欢星球
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/user/setfavorplanet
     * 请求参数（JSON格式）：
     * {
     *   "planetId":String             //星球id
     * }
     * 返回
     * 返回值：成功返回星球ID，失败返回错误信息
     */
    @PutMapping("/setfavorplanet")
    public ResponseMessage<String> setfavorplanet(@RequestParam("planetId") String planetId) {
        // 获取当前用户ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        User u=userService.findById(userId);
        if(u==null) {
            //用户不存在
            return ResponseMessage.error("用户不存在");
        }

        // 更新用户的最喜欢星球
        userService.updateFavoritePlanet(userId, planetId);
        return ResponseMessage.success(planetId);
    }

    /**
     * 加载所有星系
     * 前端请求方式：GET
     * 请求URL：localhost:8081/user/loadinggalaxies
     * 请求参数（JSON格式）：无
     * 返回值：星系列表
     */
    @GetMapping("/loadinggalaxies")
    public ResponseMessage<List<KnowledgeGalaxy>> loadinggalaxies() {
        // 获取当前用户ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        User u=userService.findById(userId);
        if(u==null) {
            //用户不存在
            return ResponseMessage.error("用户不存在");
        }

        // 获取所有星系
        List<KnowledgeGalaxy> galaxies = userService.GetAllGalaxies(userId);
        return ResponseMessage.success(galaxies);
    }

    /**
     * 搜索当前用户创建的星系（模糊搜索）
     * 前端请求方式：GET
     * 请求URL：localhost:8081/user/selectmygalaxy
     * 请求参数（Param格式）：
     * {
     *   "name": String             // 星系名称（必填）
     * }
     * 返回值：用户自己的星系信息列表
     */
    @GetMapping("/selectmygalaxy")
    public ResponseMessage<List<KnowledgeGalaxyDto>> selectMyGalaxy(@RequestParam("name") String name) {
        // 获取当前用户ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        User u = userService.findById(userId);

        // 验证用户存在性
        if (u == null) {
            return ResponseMessage.error("用户不存在");
        }

        // 验证参数
        if (StringUtils.isBlank(name)) {
            return ResponseMessage.error("星系名称不能为空");
        }

        // 根据星系名称和用户ID获取用户创建的星系列表
        // 这里假设有一个方法可以根据名称和创建者ID进行模糊搜索
        List<KnowledgeGalaxy> myGalaxies = userService.GetGalaxiesByNameAndCreatorId(name, userId);

        if (myGalaxies == null || myGalaxies.isEmpty()) {
            return ResponseMessage.error("未找到您创建的相关星系");
        }

        // 转换为DTO列表
        List<KnowledgeGalaxyDto> galaxyDtoList = myGalaxies.stream()
                .map(galaxy -> ConvertUtil.convertKnowledgeGalaxyToDto(galaxy))
                .collect(Collectors.toList());

        return ResponseMessage.success(galaxyDtoList);
    }

    /**
     * 搜索他人公开的星系（模糊搜索）
     * 前端请求方式：GET
     * 请求URL：localhost:8081/user/selectothersgalaxy
     * 请求参数（Param格式）：
     * {
     *   "name": String             // 星系名称（必填）
     * }
     * 返回值：他人公开的星系信息列表
     */
    @GetMapping("/selectothersgalaxy")
    public ResponseMessage<List<KnowledgeGalaxyDto>> selectOthersGalaxy(@RequestParam("name") String name) {
        // 获取当前用户ID（用于排除自己的星系）
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        User u = userService.findById(userId);

        // 验证用户存在性
        if (u == null) {
            return ResponseMessage.error("用户不存在");
        }

        // 验证参数
        if (StringUtils.isBlank(name)) {
            return ResponseMessage.error("星系名称不能为空");
        }

        // 根据星系名称获取所有匹配的星系（排除当前用户创建的）
        // 这里假设有一个方法可以进行模糊搜索并排除特定用户的星系
        List<KnowledgeGalaxy> othersGalaxies = userService.GetGalaxiesByNameExcludeUser(name, userId);

        if (othersGalaxies == null || othersGalaxies.isEmpty()) {
            return ResponseMessage.error("未找到其他用户的相关星系");
        }

        // 过滤出公开的星系（permission != 0）
        List<KnowledgeGalaxy> publicGalaxies = othersGalaxies.stream()
                .filter(galaxy -> galaxy.getPermission() != 0)
                .collect(Collectors.toList());

        if (publicGalaxies.isEmpty()) {
            return ResponseMessage.error("未找到其他用户的公开星系");
        }

        // 转换为DTO列表
        List<KnowledgeGalaxyDto> galaxyDtoList = publicGalaxies.stream()
                .map(galaxy -> ConvertUtil.convertKnowledgeGalaxyToDto(galaxy))
                .collect(Collectors.toList());

        return ResponseMessage.success(galaxyDtoList);
    }

    /**
     * 选择最喜欢的星系
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/user/setfavorgalaxy
     * 请求参数（JSON格式）
     * {
     *     "galaxyId": String             // 星系ID（必填）
     * }
     * 返回值：成功返回星系ID，失败返回错误信息
     */
    @PutMapping("/setfavorgalaxy")
    public ResponseMessage<String> setfavorgalaxy(@RequestParam("galaxyId") String galaxyId) {
        // 获取当前用户ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        User u=userService.findById(userId);
        if(u==null) {
            //用户不存在
            return ResponseMessage.error("用户不存在");
        }

        // 更新用户的最喜欢星系
        userService.updateFavoriteGalaxy(userId, galaxyId);
        return ResponseMessage.success(galaxyId);
    }
}
