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
    /**
     * KnowledgeGalaxy 转 DTO
     * 将实体对象转换为数据传输对象，用于API响应和前端展示
     * @param galaxy KnowledgeGalaxy 实体对象
     * @return 转换后的 knowledgeGalaxyDto 对象，如果输入为null则返回null
     */
    public static knowledgeGalaxyDto convertGalaxyToDto(KnowledgeGalaxy galaxy) {
        if (galaxy == null) return null;

        knowledgeGalaxyDto dto = new knowledgeGalaxyDto();

        // 主键 ID - 星系的唯一标识符
        dto.setGalaxyId(galaxy.getGalaxyId());

        // 星系名称 - 用户定义的星系名称
        dto.setName(galaxy.getName());

        // 主题标签 - JSON格式的标签数组，用于分类和搜索
        dto.setThemeTags(galaxy.getThemeTags());

        // 权限类型 - 0表示公开，1表示私有
        dto.setPermissionType(galaxy.getPermissionType());

        // 创建者 ID - 指向创建此星系的用户
        dto.setCreatorId(galaxy.getCreatorId());

        // 成员数量 - 当前加入此星系的用户总数
        dto.setMemberCount(galaxy.getMemberCount());

        // 最后活跃时间 - 记录星系最近一次有活动的时间
        dto.setLastActivityTime(galaxy.getLastActivityTime());

        // 创建时间 - 星系创建的时间戳
        dto.setCreateTime(galaxy.getCreateTime());

        // 更新时间 - 星系信息最后修改的时间戳
        dto.setUpdateTime(galaxy.getUpdateTime());

        return dto;
    }

    /**
     * knowledgeGalaxyDto 转实体
     * 将DTO对象转换为实体对象，用于数据库操作
     * 注意：时间字段由JPA的@PrePersist和@PreUpdate自动管理
     * @param dto knowledgeGalaxyDto 对象
     * @return 转换后的 KnowledgeGalaxy 实体对象，如果输入为null则返回null
     */
    public static KnowledgeGalaxy convertDtoToGalaxy(knowledgeGalaxyDto dto) {
        if (dto == null) return null;

        KnowledgeGalaxy galaxy = new KnowledgeGalaxy();

        // 主键 ID - 新增时通常为null，由数据库自动生成
        galaxy.setGalaxyId(dto.getGalaxyId());

        // 星系名称 - 业务核心字段，必须设置
        galaxy.setName(dto.getName());

        // 主题标签 - JSON格式的分类标签
        galaxy.setThemeTags(dto.getThemeTags());

        // 权限类型 - 控制星系的访问权限
        galaxy.setPermissionType(dto.getPermissionType());

        // 创建者 ID - 建立用户关联关系
        galaxy.setCreatorId(dto.getCreatorId());

        // 成员数量 - 如果未设置，@PrePersist会自动设置为1
        galaxy.setMemberCount(dto.getMemberCount());

        // 最后活跃时间 - 业务逻辑控制的时间字段
        galaxy.setLastActivityTime(dto.getLastActivityTime());

        // 重要说明：createTime和updateTime由JPA自动管理
        // @PrePersist方法会在插入前设置createTime和updateTime
        // @PreUpdate方法会在更新前自动更新updateTime
        // 因此通常不需要手动设置这些时间字段

        return galaxy;
    }
    /**
     * GalaxyMember 转 DTO
     * 将星系成员实体对象转换为数据传输对象，用于API响应和前端展示
     * 这个方法遵循了ConvertUtil中已有的转换模式，确保一致性
     * @param member GalaxyMember 实体对象
     * @return 转换后的 GalaxyMemberDto 对象，如果输入为null则返回null
     */
    public static GalaxyMemberDto convertMemberToDto(GalaxyMember member) {
        // 空值检查 - 防止空指针异常，这是所有转换方法的标准做法
        if (member == null) return null;

        // 创建新的DTO对象
        GalaxyMemberDto dto = new GalaxyMemberDto();

        // 主键ID - 成员记录的唯一标识符
        dto.setMemberId(member.getMemberId());

        // 星系ID - 标识该成员属于哪个星系
        dto.setGalaxyId(member.getGalaxyId());

        // 用户ID - 标识这个成员对应的用户
        dto.setUserId(member.getUserId());

        // 角色类型 - 定义成员在星系中的权限级别
        // 0: 普通成员, 1: 管理员, 2: 创建者
        dto.setRoleType(member.getRoleType());

        // 操作权限 - JSON格式的权限列表，控制成员可执行的操作
        dto.setOperationPermissions(member.getOperationPermissions());

        // 成员状态 - 0: 正常, 1: 禁用等状态标识
        dto.setStatus(member.getStatus());

        // 加入时间 - 记录成员何时加入星系
        dto.setJoinTime(member.getJoinTime());

        // 最后活跃时间 - 追踪成员最近一次活动的时间
        dto.setLastActivityTime(member.getLastActivityTime());

        return dto;
    }

    /**
     * GalaxyMemberDto 转实体
     * 将DTO对象转换为实体对象，用于数据库操作和业务逻辑处理
     * 注意：某些时间字段可能由业务逻辑或数据库触发器自动管理
     * @param dto GalaxyMemberDto 对象
     * @return 转换后的 GalaxyMember 实体对象，如果输入为null则返回null
     */
    public static GalaxyMember convertDtoToMember(GalaxyMemberDto dto) {
        // 空值检查 - 确保输入参数的有效性
        if (dto == null) return null;

        // 创建新的实体对象
        GalaxyMember member = new GalaxyMember();

        // 主键ID - 新增记录时通常为null，由数据库自动生成
        member.setMemberId(dto.getMemberId());

        // 星系ID - 必须字段，建立成员与星系的关联关系
        member.setGalaxyId(dto.getGalaxyId());

        // 用户ID - 必须字段，建立成员与用户的关联关系
        member.setUserId(dto.getUserId());

        // 角色类型 - 业务核心字段，决定成员的权限范围
        member.setRoleType(dto.getRoleType());

        // 操作权限 - JSON格式的权限配置，支持细粒度的权限控制
        member.setOperationPermissions(dto.getOperationPermissions());

        // 成员状态 - 控制成员是否可以正常使用星系功能
        member.setStatus(dto.getStatus());

        // 加入时间 - 重要的审计信息，记录成员关系建立的时间
        member.setJoinTime(dto.getJoinTime());

        // 最后活跃时间 - 用于统计分析和活跃度计算
        member.setLastActivityTime(dto.getLastActivityTime());

        return member;
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
