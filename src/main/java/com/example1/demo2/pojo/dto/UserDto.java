package com.example1.demo2.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.hibernate.validator.constraints.URL;

/**
 * 用户数据传输对象（DTO）
 * 用于前端与后端之间的数据传输和校验
 * 字段定义与User实体类保持严格映射
 */
public class UserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // ================== 基本信息字段 ==================
    /** 用户ID（对应数据库user_id字段） */
    private Integer userId;

    /**
     * 用户邮箱
     * - 格式校验：必须符合邮箱格式
     * - 长度校验：最大长度100个字符
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 用户密码
     * - 安全校验：至少8位，包含字母、数字和特殊字符
     * - 访问控制：只允许写入，响应时自动忽略
     */
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "密码至少8位，需包含字母、数字和特殊字符"
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 用户昵称
     * - 非空校验：昵称不能为空
     * - 长度校验：最大长度50个字符（与数据库保持一致）
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    /**
     * 头像URL
     * - 格式校验：必须符合URL格式
     * - 长度校验：最大长度255个字符
     */
    @URL(message = "头像URL格式不正确")
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatarUrl;

    /**
     * 个人简介
     * - 长度校验：最大长度200个字符
     */
    @Size(max = 200, message = "个人简介长度不能超过200个字符")
    private String bio;

    // ================== 状态标志字段 ==================
    /** 用户状态：0-正常，1-锁定(封禁) */
    private Integer status;

    /** 邮箱验证状态：0-未验证，1-已验证 */
    private Integer emailVerified;

    // ================== 时间戳字段 ==================
    /** 用户创建时间（自动生成，不可更新） */
    private Date createTime;

    /** 用户信息更新时间（自动更新） */
    private Date updateTime;

    /** 最后登录时间 */
    private Date lastLoginTime;

    // ================== 业务指标字段 ==================
    /** 用户推进燃料值（用于星球能量系统） */
    private Integer fuelValue;

    /** 用户知识星云值（用于知识等级系统） */
    private Integer knowledgeDust;

    // ================== 关联关系字段 ==================
    /** 用户创建的知识星球ID列表 */
    private List<Integer> createdPlanetIds;

    /** 最喜欢的星球ID */
    private String favoritePlanetId;

    /** 用户发表的评论ID列表 */
    private List<Long> commentIds;

    // ================== Getter 和 Setter 方法 ==================
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFavoritePlanetId() {
        return favoritePlanetId;
    }

    public void setFavoritePlanetId(String favoritePlanetId) {
        this.favoritePlanetId = favoritePlanetId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Integer emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getFuelValue() {
        return fuelValue;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", status=" + status +
                ", emailVerified=" + emailVerified +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", lastLoginTime=" + lastLoginTime +
                ", fuelValue=" + fuelValue +
                ", knowledgeDust=" + knowledgeDust +
                ", createdPlanetIds=" + createdPlanetIds +
                ", favoritePlanetId='" + favoritePlanetId + '\'' +
                ", commentIds=" + commentIds +
                '}';
    }

    public void setFuelValue(Integer fuelValue) {
        this.fuelValue = fuelValue;
    }

    public Integer getKnowledgeDust() {
        return knowledgeDust;
    }

    public void setKnowledgeDust(Integer knowledgeDust) {
        this.knowledgeDust = knowledgeDust;
    }

    public List<Integer> getCreatedPlanetIds() {
        return createdPlanetIds;
    }

    public void setCreatedPlanetIds(List<Integer> createdPlanetIds) {
        this.createdPlanetIds = createdPlanetIds;
    }

    public List<Long> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(List<Long> commentIds) {
        this.commentIds = commentIds;
    }

}