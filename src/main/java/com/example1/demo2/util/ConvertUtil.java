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

}
