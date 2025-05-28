package com.example1.demo2.pojo;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tab_knowledge_galaxy")
public class KnowledgeGalaxy {
    /**
     * 星系ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "galaxy_id")
    private Integer galaxyId;

    /**
     * 创建者用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * 星系名称
     */
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /**
     * 星系标签
     */
    @Column(name = "label", length = 100, nullable = false)
    private String label;

    /**
     * 星系权限(0 私有  1公开）
     */
    @Column(name = "permission", nullable = false, columnDefinition = "tinyint default 1")
    private Integer permission;

    /**
     * 星系邀请码
     */
    @Column(name = "invite_code", length = 20, unique = true)
    private String inviteCode;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime = new Date();

    /**
     * 创建者（多对一关联用户表）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User creator;

    /**
     *所包含的知识星球（一对多关联知识星球表）
     */
    @OneToMany(mappedBy = "galaxyId", fetch = FetchType.LAZY)
    private List<KnowledgePlanet> planets;

    /**
     *所包含的评论（一对多关联评论表）
     */
    @OneToMany(mappedBy = "knowledgeGalaxy", fetch = FetchType.LAZY)
    private List<GalaxyComment> comments;

    /**
     * 星系管理员（一对多关联管理员表）
     */
    @OneToMany(mappedBy = "knowledgeGalaxy", fetch = FetchType.LAZY)
    private List<GalaxyAdministrator> administrators;

    // JPA回调方法
    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createTime = now;
        this.updateTime = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = new Date();
    }

    // Getters and Setters
    public Integer getGalaxyId() {
        return galaxyId;
    }

    public void setGalaxyId(Integer galaxyId) {
        this.galaxyId = galaxyId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<KnowledgePlanet> getPlanets() {
        return planets;
    }

    public void setPlanets(List<KnowledgePlanet> planets) {
        this.planets = planets;
    }

    public List<GalaxyComment> getComments() {
        return comments;
    }

    public void setComments(List<GalaxyComment> comments) {
        this.comments = comments;
    }

    public List<GalaxyAdministrator> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(List<GalaxyAdministrator> administrators) {
        this.administrators = administrators;
    }

    @Override
    public String toString() {
        return "KnowledgeGalaxy{" +
                "galaxyId=" + galaxyId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", permission=" + permission +
                ", inviteCode='" + inviteCode + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}