package com.example1.demo2.pojo;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "galaxy_admins")
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
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "galaxy_id", referencedColumnName = "galaxy_id", insertable = false, updatable = false)
    private KnowledgeGalaxy knowledgeGalaxy;

    /**
     * 用户ID（外键关联用户表）
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private List<User> users;

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
    }

    @PreUpdate
    protected void onUpdate() {
    }

    // Getters and Setters
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public KnowledgeGalaxy getGalaxy() {
        return knowledgeGalaxy;
    }

    public void setGalaxy(KnowledgeGalaxy knowledgeGalaxy) {
        this.knowledgeGalaxy = knowledgeGalaxy;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
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
        return "GalaxyAdmin{" +
                "adminId=" + adminId +
                ", knowledgeGalaxy=" + knowledgeGalaxy +
                ", users=" + users +
                ", roleType='" + roleType + '\'' +
                ", permissions='" + permissions + '\'' +
                ", appointedBy=" + appointedBy +
                ", appointTime=" + appointTime +
                ", status=" + status +
                '}';
    }
}