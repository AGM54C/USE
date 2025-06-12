package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.GalaxyMapper;
import com.example1.demo2.mapper.PlanetMapper;
import com.example1.demo2.mapper.UserMapper;
import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.User;
import com.example1.demo2.service.IUserService;
import com.example1.demo2.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public class UserService implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PlanetMapper planetMapper;
    @Autowired
    private GalaxyMapper galaxyMapper;

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
        // 对新密码进行哈希处理
        String passwordHash = BCryptUtil.hashPassword(newpassword);
        // 将哈希后的密码传递给 mapper，而不是原始密码
        userMapper.updatepassword(passwordHash, userId);
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
    public List<KnowledgeGalaxy> GetAllGalaxies(Integer userId) {
        return galaxyMapper.selectAll(userId);
    }

    @Override
    public KnowledgeGalaxy GetGalaxyByName(String name) {
        //模糊搜索星系名字
        List<KnowledgeGalaxy> galaxies = galaxyMapper.searchByName(name);
        if (galaxies != null && !galaxies.isEmpty()) {
            return galaxies.get(0); // 返回第一个匹配的星系
        }
        return null; // 返回null表示没有找到匹配的星系
    }

    @Override
    public void updateFavoriteGalaxy(Integer userId, Integer galaxyId) {
        userMapper.updateFavoriteGalaxy(userId, galaxyId);
    }

    @Override
    public List<KnowledgeGalaxy> GetGalaxiesByNameAndCreatorId(String name, Integer userId) {
        return galaxyMapper.searchByNameAndCreatorId(name, userId);
    }

    @Override
    public List<KnowledgeGalaxy> GetGalaxiesByNameExcludeUser(String name, Integer userId) {
        return galaxyMapper.searchByNameExcludeUser(name, userId);
    }

    @Override
    public KnowledgePlanet getFavorPlanet(String favorPlanetId) {
        return planetMapper.getFavorPlanet(favorPlanetId);
    }

    @Override
    public String uploadAvatar(MultipartFile file, Integer userId) {
        // 这里可以实现文件上传逻辑，返回上传后的头像URL
        // 例如，将文件保存到服务器并返回其访问路径
        String avatarUrl = "http://example.com/avatars/" + file.getOriginalFilename();
        userMapper.updateurl(avatarUrl, userId);
        return avatarUrl;
    }

    @Override
    public void register(String nickname, String password,String email) {
        //加密处理
        String passwordHash= BCryptUtil.hashPassword(password);
        //添加
        userMapper.add(nickname,passwordHash,email);
    }


}
