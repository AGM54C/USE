package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.*;
import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.User;
import com.example1.demo2.service.IGalaxyService;
import com.example1.demo2.service.IPlanetService;
import com.example1.demo2.service.IUserService;
import com.example1.demo2.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private GalaxyCommentMapper galaxyCommentMapper;

    @Autowired
    private PlanetCommentMapper planetCommentMapper;

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private PrivateMessageMapper privateMessageMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private GalaxyAdministratorMapper galaxyAdministratorMapper;

    // 使用 @Lazy 避免循环依赖
    @Autowired
    @Lazy
    private IGalaxyService galaxyService;

    @Autowired
    @Lazy
    private IPlanetService planetService;

    @Override
    public User findByNickname(String nickname) {
        return userMapper.findByNickname(nickname);
    }

    @Override
    public User findByEmail(String Email){
        return userMapper.findByEmail(Email);
    }


    /**
     * 删除用户 - 实现完整的级联删除
     *
     * 这是一个复杂的级联删除操作，需要按正确的顺序删除所有关联数据：
     *
     * 级联删除顺序（从依赖最深的开始）：
     * 1. 删除用户创建的所有星系（星系服务会级联删除星系的评论、管理员等）
     * 2. 删除用户创建的所有星球（星球服务会级联删除星球的评论等）
     * 3. 删除用户发表的所有星系评论的点赞记录
     * 4. 删除用户发表的所有星系评论
     * 5. 删除用户发表的所有星球评论的点赞记录
     * 6. 删除用户发表的所有星球评论
     * 7. 删除用户给其他评论的点赞记录
     * 8. 删除用户的好友关系
     * 9. 删除用户的私信记录
     * 10. 删除用户的通知记录
     * 11. 删除用户作为管理员的记录
     * 12. 清除其他用户对该用户的收藏引用
     * 13. 最后删除用户本身
     */
    @Override
    @Transactional
    public void delete(Integer userId) {
        // 检查用户是否存在
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 1. 删除用户创建的所有星系（使用星系服务的级联删除）
        List<KnowledgeGalaxy> galaxies = galaxyMapper.getGalaxiesByUserId(userId);
        if (galaxies != null && !galaxies.isEmpty()) {
            for (KnowledgeGalaxy galaxy : galaxies) {
                galaxyService.deleteGalaxy(galaxy.getGalaxyId());
            }
        }

        // 2. 删除用户创建的所有星球（使用星球服务的级联删除）
        List<String> planetIds = planetMapper.getPlanetIdsByUserId(userId);
        if (planetIds != null && !planetIds.isEmpty()) {
            for (String planetId : planetIds) {
                planetService.deleteWithComments(planetId);
            }
        }

        // 3. 删除用户发表的所有星系评论的点赞记录
        // 先获取用户的所有评论ID，然后删除这些评论的点赞
        List<Integer> galaxyCommentIds = galaxyCommentMapper.getCommentIdsByUserId(userId);
        if (galaxyCommentIds != null) {
            for (Integer commentId : galaxyCommentIds) {
                galaxyCommentMapper.deleteLikesByCommentId(commentId);
            }
        }

        // 4. 删除用户发表的所有星系评论
        galaxyCommentMapper.deleteCommentsByUserId(userId);

        // 5. 删除用户发表的所有星球评论的点赞记录
        List<Integer> planetCommentIds = planetCommentMapper.getCommentIdsByUserId(userId);
        if (planetCommentIds != null) {
            for (Integer commentId : planetCommentIds) {
                planetCommentMapper.deleteLikesByCommentId(commentId);
            }
        }

        // 6. 删除用户发表的所有星球评论
        planetCommentMapper.deleteCommentsByUserId(userId);

        // 7. 删除用户给其他评论的点赞记录
        galaxyCommentMapper.deleteLikesByUserId(userId);
        planetCommentMapper.deleteLikesByUserId(userId);

        // 8. 删除用户的好友关系
        friendMapper.deleteAllFriendshipsByUserId(userId);

        // 9. 删除用户的私信记录
        privateMessageMapper.deleteMessagesByUserId(userId);

        // 10. 删除用户的通知记录
        notificationMapper.deleteNotificationsByUserId(userId);

        // 11. 删除用户作为管理员的记录
        galaxyAdministratorMapper.deleteAdminRecordsByUserId(userId);

        // 12. 清除其他用户对该用户的收藏引用（将 favorite_planet_id 和 favorite_galaxy_id 中引用的清除）
        // 这一步是可选的，取决于业务需求

        // 13. 最后删除用户本身
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