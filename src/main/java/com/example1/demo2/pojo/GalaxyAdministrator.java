package com.example1.demo2.pojo;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tab_galaxy_admins")
public class GalaxyAdministrator {

    /**
     * 管理员ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Integer adminId;

    /**
     * 关联的知识星系ID（外键）
     * 多对一关系：多个管理员可以管理同一个星系
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "galaxy_id", nullable = false)
    private KnowledgeGalaxy knowledgeGalaxy;

    /**
     * 用户ID（外键关联用户表）
     * 多对一关系：多个管理员记录可以对应同一个用户（用户可以是多个星系的管理员）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 角色类型(0 创建者 1 其他管理员)
     */
    @Column(name = "role_type", nullable = false, columnDefinition = "tinyint default 0")
    private Integer roleType;

    /**
     * 权限列表（JSON存储）
     * 示例：["CONTENT_REVIEW", "MEMBER_MANAGE", "ACTIVITY_CREATE"]
     */
    @Column(name = "permissions", columnDefinition = "json")
    private String permissions;

    /**
     * 任命者ID（记录是谁赋予的权限）
     */
    @Column(name = "appointed_by")
    private Integer appointedBy;

    /**
     * 任命时间
     */
    @Column(name = "appoint_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date appointTime;

    /**
     * 状态：0-正常 1-已撤销
     */
    @Column(name = "status", columnDefinition = "tinyint(1) default 0")
    private Integer status;

    // JPA回调方法
    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = 0; // 默认状态正常
        }
        if (this.appointTime == null) {
            this.appointTime = new Date(); // 设置任命时间
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // 可以在这里添加更新时的逻辑
    }

    // Getters and Setters
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public KnowledgeGalaxy getKnowledgeGalaxy() {
        return knowledgeGalaxy;
    }

    public void setKnowledgeGalaxy(KnowledgeGalaxy knowledgeGalaxy) {
        this.knowledgeGalaxy = knowledgeGalaxy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public Integer getAppointedBy() {
        return appointedBy;
    }

    public void setAppointedBy(Integer appointedBy) {
        this.appointedBy = appointedBy;
    }

    public Date getAppointTime() {
        return appointTime;
    }

    public void setAppointTime(Date appointTime) {
        this.appointTime = appointTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GalaxyAdministrator{" +
                "adminId=" + adminId +
                ", knowledgeGalaxy=" + knowledgeGalaxy +
                ", user=" + user +
                ", roleType=" + roleType +
                ", permissions='" + permissions + '\'' +
                ", appointedBy=" + appointedBy +
                ", appointTime=" + appointTime +
                ", status=" + status +
                '}';

    }
}