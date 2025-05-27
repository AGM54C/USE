package com.example1.demo2.pojo;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

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
     * 星系名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 主题标签（JSON格式）
     */
    @Column(name = "theme_tags", columnDefinition = "TEXT")
    private String themeTags;

    /**
     * 权限类型：0-公开 1-私有
     */
    @Column(name = "permission_type", nullable = false, columnDefinition = "tinyint(1) default 0")
    private Integer permissionType;

    /**
     * 创建者ID（外键）
     */
    @Column(name = "creator_id", nullable = false)
    private Integer creatorId;

    /**
     * 成员数量
     */
    @Column(name = "member_count", nullable = false, columnDefinition = "int default 1")
    private Integer memberCount;

    /**
     * 最近活动时间
     */
    @Column(name = "last_activity_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastActivityTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    /**
     * 包含的知识星球列表
     */
    @OneToMany(mappedBy = "knowledgeGalaxy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KnowledgePlanet> knowledgePlanets;

    // JPA回调方法，插入前自动设置创建时间和更新时间
    @PrePersist
    protected void onCreate() {
        this.createTime = this.updateTime = new Date();
        if (this.memberCount == null) {
            this.memberCount = 1;
        }
        if (this.lastActivityTime == null) {
            this.lastActivityTime = new Date();
        }
    }

    // JPA回调方法，更新前自动设置更新时间
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThemeTags() {
        return themeTags;
    }

    public void setThemeTags(String themeTags) {
        this.themeTags = themeTags;
    }

    public Integer getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(Integer permissionType) {
        this.permissionType = permissionType;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Date getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(Date lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
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

    public List<KnowledgePlanet> getKnowledgePlanets() {
        return knowledgePlanets;
    }

    public void setKnowledgePlanets(List<KnowledgePlanet> knowledgePlanets) {
        this.knowledgePlanets = knowledgePlanets;
    }

    @Override
    public String toString() {
        return "KnowledgeGalaxy{" +
                "galaxyId=" + galaxyId +
                ", name='" + name + '\'' +
                ", themeTags='" + themeTags + '\'' +
                ", permissionType=" + permissionType +
                ", creatorId=" + creatorId +
                ", memberCount=" + memberCount +
                ", lastActivityTime=" + lastActivityTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", knowledgePlanets=" + knowledgePlanets +
                '}';
    }
}