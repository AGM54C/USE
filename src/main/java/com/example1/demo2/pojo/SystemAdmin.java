package com.example1.demo2.pojo;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "system_admins")
public class SystemAdmin {

    /**
     * 管理员ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Integer adminId;

    /**
     * 关联的用户ID（外键）
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    /**
     * 权限列表（JSON存储）
     * 示例：["USER_BAN", "CONTENT_DELETE", "GALAXY_ACCESS", "REPORT_REVIEW"]
     */
    @Column(name = "permissions", columnDefinition = "json", nullable = false)
    private String permissions;

    /**
     * 管辖范围（JSON存储星系ID列表，空值表示全部）
     * 示例：[1001, 1002]
     */
    @Column(name = "scope", columnDefinition = "json")
    private String scope;

    /**
     * 最后登录IP
     */
    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;

    /**
     * 状态：0-活跃 1-停用
     */
    @Column(name = "status", columnDefinition = "tinyint(1) default 0")
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

    // JPA回调方法
    @PrePersist
    protected void onCreate() {
        this.createTime = this.updateTime = new Date();
        if (this.status == null) {
            this.status = 0; // 默认状态活跃
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = new Date();
    }

    // Getters and Setters
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
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

    @Override
    public String toString() {
        return "SystemAdmin{" +
                "adminId=" + adminId +
                ", userId=" + userId +
                ", permissions='" + permissions + '\'' +
                ", scope='" + scope + '\'' +
                ", lastLoginIp='" + lastLoginIp + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}