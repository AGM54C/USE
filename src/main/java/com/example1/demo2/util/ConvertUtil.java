package com.example1.demo2.util;

import com.example1.demo2.pojo.*;
import com.example1.demo2.pojo.dto.*;

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
        //jwt版本
        dto.setTokenVersion(user.getTokenVersion());
        // 邮箱
        dto.setEmail(user.getEmail());
        // 手机号
        dto.setMobile(user.getMobile());
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
        // 手机验证状态
        dto.setMobileVerified(user.getMobileVerified());
        // 创建时间
        dto.setCreateTime(user.getCreateTime());
        // 更新时间
        dto.setUpdateTime(user.getUpdateTime());
        // 最后登录时间
        dto.setLastLoginTime(user.getLastLoginTime());
        return dto;
    }

    /**
     * 反向转换：UserDto 转 User
     */
    public static User convertDtoToUser(UserDto dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setTokenVersion(dto.getTokenVersion());
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());
        user.setPassword(dto.getPassword()); // 注意：此处需先对密码加密再存入实体
        user.setNickname(dto.getNickname());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setBio(dto.getBio());
        user.setStatus(dto.getStatus());
        user.setEmailVerified(dto.getEmailVerified());
        user.setMobileVerified(dto.getMobileVerified());
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
        dto.setTitle(planet.getTitle());
        dto.setDescription(planet.getDescription());
        dto.setCoverUrl(planet.getCoverUrl());
        dto.setThemeId(planet.getThemeId());
        dto.setModelType(planet.getModelType());
        dto.setColorScheme(planet.getColorScheme());
        dto.setVisibility(planet.getVisibility());
        dto.setFuelValue(planet.getFuelValue());
        dto.setBrightness(planet.getBrightness());
        dto.setVisitCount(planet.getVisitCount());
        dto.setStatus(planet.getStatus());
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
        planet.setTitle(dto.getTitle());
        planet.setDescription(dto.getDescription());
        planet.setCoverUrl(dto.getCoverUrl());
        planet.setThemeId(dto.getThemeId());
        planet.setModelType(dto.getModelType());
        planet.setColorScheme(dto.getColorScheme());
        planet.setVisibility(dto.getVisibility());
        planet.setFuelValue(dto.getFuelValue());
        planet.setBrightness(dto.getBrightness());
        planet.setVisitCount(dto.getVisitCount());
        planet.setStatus(dto.getStatus());
        // 时间字段由数据库自动管理，此处可省略或按需设置
        return planet;
    }

    /**
     * PlanetContent 转 DTO
     */
    public static PlanetContentDto convertPlanetContentToDto(PlanetContent content) {
        if (content == null) return null;
        PlanetContentDto dto = new PlanetContentDto();
        dto.setContentId(content.getContentId());
        dto.setPlanetId(content.getPlanetId());
        dto.setContentType(content.getContentType());
        dto.setTitle(content.getTitle());
        dto.setContent(content.getContent());
        dto.setFileUrl(content.getFileUrl());
        dto.setComment(content.getComment());
        dto.setVersion(content.getVersion());
        dto.setStatus(content.getStatus());
        dto.setCreateTime(content.getCreateTime());
        dto.setUpdateTime(content.getUpdateTime());
        return dto;
    }

    /**
     * PlanetContentDto 转实体
     */
    public static PlanetContent convertDtoToPlanetContent(PlanetContentDto dto) {
        if (dto == null) return null;
        PlanetContent content = new PlanetContent();
        content.setContentId(dto.getContentId());
        content.setPlanetId(dto.getPlanetId());
        content.setContentType(dto.getContentType());
        content.setTitle(dto.getTitle());
        content.setContent(dto.getContent());
        content.setFileUrl(dto.getFileUrl());
        content.setComment(dto.getComment());
        content.setVersion(dto.getVersion());
        content.setStatus(dto.getStatus());
        // 时间字段由数据库自动管理，此处可省略或按需设置
        return content;
    }
    //  PlanetComment转DTO
    public static PlanetCommentDto convertPlanetCommentToDto(PlanetComment comment) {
        PlanetCommentDto dto = new PlanetCommentDto();
        dto.setCommentId(comment.getCommentId());
        dto.setPlanetId(comment.getPlanetId());
        dto.setUserId(comment.getUserId());
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
        comment.setCommentId(dto.getCommentId());
        comment.setPlanetId(dto.getPlanetId());
        comment.setUserId(dto.getUserId());
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
            dto.setGalaxyId(galaxy.getGalaxyId().toString());
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
        if (dto.getGalaxyId() != null && !dto.getGalaxyId().isEmpty()) {
            try {
                // 尝试将字符串ID转换为整数
                // 如果是"GLXY-20250101-ABCD"格式，则提取数字部分
                // 如果是纯数字字符串，则直接转换
                galaxy.setGalaxyId(Integer.parseInt(dto.getGalaxyId()));
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

}
