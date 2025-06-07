package com.example1.demo2.util;

import com.example1.demo2.pojo.*;
import com.example1.demo2.pojo.dto.*;

import java.util.ArrayList;
import java.util.List;

//实体类和Dto转换工具
public class ConvertUtil {
    /**
     * 将 User 实体转换为 UserDto（全属性映射）
     * @param user User 实体对象
     * @return 转换后的 UserDto 对象
     */
    public static UserDto convertUserToDto(User user) {
        UserDto dto = new UserDto();
        // 主键 ID
        dto.setUserId(user.getUserId());
        // 邮箱
        dto.setEmail(user.getEmail());
        // 密码（注意：UserDto 中的 @JsonIgnore 会忽略密码字段，但转换时仍需赋值）
        dto.setPassword(user.getPassword());
        // 昵称
        dto.setNickname(user.getNickname());
        // 头像 URL
        dto.setAvatarUrl(user.getAvatarUrl());
        // 个人简介
        dto.setBio(user.getBio());
        // 用户状态
        dto.setStatus(user.getStatus());
        // 邮箱验证状态
        dto.setEmailVerified(user.getEmailVerified());
        // 创建时间
        dto.setCreateTime(user.getCreateTime());
        // 更新时间
        dto.setUpdateTime(user.getUpdateTime());
        // 最后登录时间
        dto.setLastLoginTime(user.getLastLoginTime());
        // 最喜爱星球id
        dto.setFavoritePlanetId(user.getFavoritePlanetId());
        return dto;
    }

    /**
     * 反向转换：UserDto 转 User
     */
    public static User convertDtoToUser(UserDto dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // 注意：此处需先对密码加密再存入实体
        user.setNickname(dto.getNickname());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setBio(dto.getBio());
        user.setStatus(dto.getStatus());
        user.setEmailVerified(dto.getEmailVerified());
        user.setCreateTime(dto.getCreateTime());
        // 时间字段通常由系统自动生成，注册/更新时无需手动设置（可省略或按需处理）
        return user;
    }

    /**
     * KnowledgePlanet 转 DTO
     */
    public static KnowledgePlanetDto convertKnowledgePlanetToDto(KnowledgePlanet planet) {
        if (planet == null) return null;
        KnowledgePlanetDto dto = new KnowledgePlanetDto();
        dto.setPlanetId(planet.getPlanetId());
        dto.setUserId(planet.getUserId());
        dto.setContentTitle(planet.getContentTitle());
        dto.setContentDetail(planet.getContentDetail());
        dto.setDescription(planet.getDescription());
        dto.setCoverUrl(planet.getCoverUrl());
        dto.setThemeId(planet.getThemeId());
        dto.setModelType(planet.getModelType());
        dto.setColorScheme(planet.getColorScheme());
        dto.setVisibility(planet.getVisibility());
        dto.setFuelValue(planet.getFuelValue());
        dto.setBrightness(planet.getBrightness());
        dto.setVisitCount(planet.getVisitCount());
        dto.setCreateTime(planet.getCreateTime());
        dto.setUpdateTime(planet.getUpdateTime());
        return dto;
    }

    /**
     * KnowledgePlanetDto 转实体
     */
    public static KnowledgePlanet convertDtoToKnowledgePlanet(KnowledgePlanetDto dto) {
        if (dto == null) return null;
        KnowledgePlanet planet = new KnowledgePlanet();
        planet.setPlanetId(dto.getPlanetId());
        planet.setUserId(dto.getUserId());
        planet.setContentTitle(dto.getContentTitle());
        planet.setContentDetail(dto.getContentDetail());
        planet.setDescription(dto.getDescription());
        planet.setCoverUrl(dto.getCoverUrl());
        planet.setThemeId(dto.getThemeId());
        planet.setModelType(dto.getModelType());
        planet.setColorScheme(dto.getColorScheme());
        planet.setVisibility(dto.getVisibility());
        planet.setFuelValue(dto.getFuelValue());
        planet.setBrightness(dto.getBrightness());
        planet.setVisitCount(dto.getVisitCount());
        // 时间字段由数据库自动管理，此处可省略或按需设置
        return planet;
    }


    //  PlanetComment转DTO
    public static PlanetCommentDto convertPlanetCommentToDto(PlanetComment comment) {
        PlanetCommentDto dto = new PlanetCommentDto();
        dto.setPlanetCommentId(comment.getPlanetCommentId());
        // 只提取星球ID
        if (comment.getPlanet() != null) {
            dto.setPlanetId(comment.getPlanet().getPlanetId());
        }
        dto.setUserId(comment.getUser().getUserId());
        dto.setParentId(comment.getParentId());
        dto.setLevel(comment.getLevel());
        dto.setContent(comment.getContent());
        dto.setLikeCount(comment.getLikeCount());
        dto.setReplyCount(comment.getReplyCount());
        dto.setStatus(comment.getStatus());
        dto.setCreateTime(comment.getCreateTime());
        dto.setUpdateTime(comment.getUpdateTime());
        return dto;
    }

    // DTO转PlanetComment
    public static PlanetComment convertDtoToPlanetComment(PlanetCommentDto dto) {
        PlanetComment comment = new PlanetComment();
        comment.setPlanetCommentId(dto.getPlanetCommentId());
        // 关键处理：根据planetId创建临时星球对象
        if (dto.getPlanetId() != null) {
            KnowledgePlanet planet = new KnowledgePlanet();
            planet.setPlanetId(dto.getPlanetId());
            comment.setPlanet(planet); // 设置关联对象
        }
        User user=new User();
        user.setUserId(dto.getUserId());
        comment.setUser(user);
        comment.setParentId(dto.getParentId());
        comment.setLevel(dto.getLevel() != null ? dto.getLevel() : 1); // 默认层级1
        comment.setContent(dto.getContent());
        comment.setLikeCount(dto.getLikeCount() != null ? dto.getLikeCount() : 0); // 默认0
        comment.setReplyCount(dto.getReplyCount() != null ? dto.getReplyCount() : 0); // 默认0
        comment.setStatus(dto.getStatus() != null ? dto.getStatus() : 0); // 默认正常
        return comment;
    }

    /**
     * 将 KnowledgeGalaxy 实体转换为 KnowledgeGalaxyDto
     * @param galaxy KnowledgeGalaxy 实体对象
     * @return 转换后的 KnowledgeGalaxyDto 对象
     */
    public static KnowledgeGalaxyDto convertKnowledgeGalaxyToDto(KnowledgeGalaxy galaxy) {
        if (galaxy == null) return null;

        KnowledgeGalaxyDto dto = new KnowledgeGalaxyDto();

        // 主键ID转换：Integer -> String
        // 注意：这里需要将数据库的自增整数ID转换为字符串格式
        if (galaxy.getGalaxyId() != null) {
            dto.setGalaxyId(galaxy.getGalaxyId());
        }

        // 创建者用户ID
        dto.setUserId(galaxy.getUserId());

        // 星系名称
        dto.setName(galaxy.getName());

        // 星系标签
        dto.setLabel(galaxy.getLabel());

        // 星系权限（0私有，1公开）
        dto.setPermission(galaxy.getPermission());

        // 星系邀请码
        dto.setInviteCode(galaxy.getInviteCode());

        // 创建时间
        dto.setCreateTime(galaxy.getCreateTime());

        // 更新时间
        dto.setUpdateTime(galaxy.getUpdateTime());

        // 星系的星球数量（如果需要，可以添加）
        dto.setPlanetCount(galaxy.getPlanetCount());

        // 注意：实体中的关联对象（creator, planets, comments, administrators）
        // 在DTO中不需要，所以这里不进行转换

        return dto;
    }

    /**
     * 将 KnowledgeGalaxyDto 转换为 KnowledgeGalaxy 实体
     * @param dto KnowledgeGalaxyDto 对象
     * @return 转换后的 KnowledgeGalaxy 实体对象
     */
    public static KnowledgeGalaxy convertDtoToKnowledgeGalaxy(KnowledgeGalaxyDto dto) {
        if (dto == null) return null;

        KnowledgeGalaxy galaxy = new KnowledgeGalaxy();

        // 主键ID转换：String -> Integer
        // 注意：创建新记录时galaxyId可能为null（数据库自增生成）
        // 更新记录时galaxyId是必需的
        if (dto.getGalaxyId() != null) {
            try {
                // 尝试将字符串ID转换为整数
                // 如果是"GLXY-20250101-ABCD"格式，则提取数字部分
                // 如果是纯数字字符串，则直接转换
                galaxy.setGalaxyId(dto.getGalaxyId());
            } catch (NumberFormatException e) {
                // 如果无法转换为数字，说明可能是格式化的ID
                // 这种情况下galaxyId应该由数据库自动生成，所以不设置
                // 实际使用时根据业务需求决定如何处理
            }
        }

        // 创建者用户ID
        galaxy.setUserId(dto.getUserId());

        // 星系名称
        galaxy.setName(dto.getName());

        // 星系标签
        galaxy.setLabel(dto.getLabel());

        // 星系权限（默认值为1-公开）
        galaxy.setPermission(dto.getPermission() != null ? dto.getPermission() : 1);

        // 星系邀请码
        galaxy.setInviteCode(dto.getInviteCode());

        // 时间字段通常由数据库自动管理（通过@PrePersist和@PreUpdate）
        // 创建时不需要手动设置，更新时可根据需要设置
        // galaxy.setCreateTime(dto.getCreateTime());
        // galaxy.setUpdateTime(dto.getUpdateTime());

        return galaxy;
    }
    /**
     * 将GalaxyCommentDto转换为GalaxyComment实体
     */
    public static GalaxyComment convertDtoToGalaxyComment(GalaxyCommentDto dto) {
        if (dto == null) {
            return null;
        }

        GalaxyComment comment = new GalaxyComment();
        comment.setGalaxyCommentId(dto.getGalaxyCommentId());
        comment.setContent(dto.getContent());
        comment.setLevel(dto.getLevel());
        comment.setParentId(dto.getParentId());
        comment.setReplyToUserId(dto.getReplyToUserId());
        comment.setCreatorRole(dto.getCreatorRole());
        comment.setLikeCount(dto.getLikeCount());
        comment.setReplyCount(dto.getReplyCount());
        comment.setStatus(dto.getStatus());
        comment.setCreateTime(dto.getCreateTime());
        comment.setUpdateTime(dto.getUpdateTime());

        return comment;
    }

    /**
     * 将PlanetComment实体列表转换为DTO列表
     */
    public static List<PlanetCommentDto> convertPlanetCommentListToDto(List<PlanetComment> comments) {
        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
        }

        List<PlanetCommentDto> dtos = new ArrayList<>();
        for (PlanetComment comment : comments) {
            dtos.add(convertPlanetCommentToDto(comment));
        }
        return dtos;
    }
    /**
     * 将GalaxyComment实体列表转换为DTO列表
     */
    public static List<KnowledgePlanetDto> convertKnowledgePlanetListToDtoList(List<KnowledgePlanet> planets) {
        if (planets == null || planets.isEmpty()) {
            return new ArrayList<>();
        }

        List<KnowledgePlanetDto> dtoList = new ArrayList<>();
        for (KnowledgePlanet planet : planets) {
            dtoList.add(convertKnowledgePlanetToDto(planet));
        }
        return dtoList;
    }
}
