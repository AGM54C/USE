package com.example1.demo2.pojo;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "tab_galaxy_member")
public class GalaxyMember {
    /**
     * 成员ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer memberId;

    /**
     * 星系ID（外键）
     * 关联到知识星系表
     */
    @Column(name = "galaxy_id", nullable = false)
    private Integer galaxyId;

    /**
     * 用户ID（外键）
     * 关联到用户表
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * 角色类型
     * 0-普通成员 1-管理员 2-创建者
     */
    @Column(name = "role_type", nullable = false, columnDefinition = "tinyint(1) default 0")
    private Integer roleType;

    /**
     * 加入时间
     */
    @Column(name = "join_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date joinTime;

    /**
     * 操作权限（JSON格式）
     * 例如：["添加星球", "删除内容", "管理成员"]
     */
    @Column(name = "operation_permissions", columnDefinition = "TEXT")
    private String operationPermissions;

    /**
     * 状态：0-正常 1-禁用
     */
    @Column(name = "status", nullable = false, columnDefinition = "tinyint(1) default 0")
    private Integer status;

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
     * 该成员创建的知识星球列表
     * 一对多关系，由KnowledgePlanet中的字段维护
     */
    @OneToMany(mappedBy = "creatorMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KnowledgePlanet> createdPlanets;


    /**
     * JPA回调方法，插入前自动设置创建时间和更新时间
     * 同时设置默认值
     */
    @PrePersist
    protected void onCreate() {
        this.createTime = this.updateTime = new Date();
        if (this.joinTime == null) {
            this.joinTime = new Date();
        }
        if (this.status == null) {
            this.status = 0; // 默认状态为正常
        }
        if (this.roleType == null) {
            this.roleType = 0; // 默认为普通成员
        }
    }

    /**
     * JPA回调方法，更新前自动设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        this.updateTime = new Date();
    }

    // Getters and Setters
    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

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

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public String getOperationPermissions() {
        return operationPermissions;
    }

    public void setOperationPermissions(String operationPermissions) {
        this.operationPermissions = operationPermissions;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public List<KnowledgePlanet> getCreatedPlanets() {
        return createdPlanets;
    }

    public void setCreatedPlanets(List<KnowledgePlanet> createdPlanets) {
        this.createdPlanets = createdPlanets;
    }

    public Date getLastActivityTime() {
        return updateTime; // 最后活动时间等同于更新时间
    }

    public void setLastActivityTime(Date lastActivityTime) {
        this.updateTime = lastActivityTime; // 更新最后活动时间
    }

    @Override
    public String toString() {
        return "GalaxyMember{" +
                "memberId=" + memberId +
                ", galaxyId=" + galaxyId +
                ", userId=" + userId +
                ", roleType=" + roleType +
                ", joinTime=" + joinTime +
                ", operationPermissions='" + operationPermissions + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    /**
     * 角色类型常量定义
     * 便于在业务逻辑中使用
     */
    public static class RoleType {
        public static final int MEMBER = 0;    // 普通成员
        public static final int ADMIN = 0;     // 管理员
        public static final int CREATOR = 1;   // 创建者
    }

    /**
     * 状态常量定义
     */
    public static class Status {
        public static final int NORMAL = 0;    // 正常
        public static final int DISABLED = 1;  // 禁用
    }
}