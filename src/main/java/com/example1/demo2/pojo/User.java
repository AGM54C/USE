package com.example1.demo2.pojo;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "tab_user")
public class User {

    /**
     * 用户ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    /**
     * JWT令牌版本
     */
    @Column(name = "token_version")
    private Integer tokenVersion; // 初始值为0

    /**
     * 邮箱地址
     */
    @Column(name = "email", unique = true, length = 100)
    private String email;

    /**
     * 手机号
     */
    @Column(name = "mobile", unique = true, length = 20)
    private String mobile;

    /**
     * 密码
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * 用户昵称
     */
    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    /**
     * 头像URL
     */
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    /**
     * 个人简介
     */
    @Column(name = "bio", length = 200)
    private String bio;

    /**
     * 用户状态：0-正常 1-锁定(封禁）
     */
    @Column(name = "status", nullable = false, columnDefinition = "tinyint(1) default 0")
    private Integer status;

    /**
     * 邮箱验证状态：0-未验证 1-已验证
     */
    @Column(name = "email_verified", nullable = false, columnDefinition = "tinyint(1) default 0")
    private Integer emailVerified;

    /**
     * 手机验证状态：0-未验证 1-已验证
     */
    @Column(name = "mobile_verified", nullable = false, columnDefinition = "tinyint(1) default 0")
    private Integer mobileVerified;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 更新时间，自动更新
     */
    @Column(name = "update_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;

    /**
     *推进燃料
     */
    @Column(name="fuel_value")
    private Integer fuelValue;

    /**
     * 知识星云
     */
    @Column(name = "knowledge_dust")
    private Integer knowledgeDust;


    // JPA回调方法，插入前自动设置创建时间和更新时间
    @PrePersist
    protected void onCreate() {
        this.createTime = this.updateTime = new Date();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public Integer getMobileVerified() {
        return mobileVerified;
    }

    public void setMobileVerified(Integer mobileVerified) {
        this.mobileVerified = mobileVerified;
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

    // JPA回调方法，更新前自动设置更新时间
    @PreUpdate
    protected void onUpdate() {
        this.updateTime = new Date();
    }

    public Integer getTokenVersion() {
        return tokenVersion;
    }

    public void setTokenVersion(Integer tokenVersion) {
        this.tokenVersion = tokenVersion;
    }

    public Integer getFuelValue() {
        return fuelValue;
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

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", tokenVersion=" + tokenVersion +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", status=" + status +
                ", emailVerified=" + emailVerified +
                ", mobileVerified=" + mobileVerified +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", lastLoginTime=" + lastLoginTime +
                ", fuelValue=" + fuelValue +
                ", knowledgeDust=" + knowledgeDust +
                '}';
    }
}


