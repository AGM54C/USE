package com.example1.demo2.pojo.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

public class KnowledgeGalaxyDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // 创建时自动生成，无需前端传入
    @Pattern(regexp = "^GLXY-\\d{8}-\\d{4}$", message = "星系ID格式错误")
    private String galaxyId;

    // 从认证信息获取，前端无需传入
    @NotNull(message = "创建者用户ID不能为空")
    private Integer userId;

    @NotBlank(message = "星系名称不能为空")
    @Size(max = 100, message = "星系名称长度不能超过100字符")
    private String name;

    @NotBlank(message = "星系标签不能为空")
    @Size(max = 100, message = "星系标签长度不能超过100字符")
    private String label;

    @NotNull(message = "星系权限不能为空")
    @Min(value = 0, message = "星系权限取值范围为0-1")
    @Max(value = 1, message = "星系权限取值范围为0-1")
    private Integer permission = 1; // 默认值1（公开）

    // 后端生成或前端传入（可选）
    @Size(max = 20, message = "邀请码长度不能超过20字符")
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "邀请码只能包含字母和数字")
    private String inviteCode;

    // 自动维护，前端无需传入
    private Date createTime;
    private Date updateTime;

    // Getters and Setters
    public String getGalaxyId() {
        return galaxyId;
    }

    public void setGalaxyId(String galaxyId) {
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

    @Override
    public String toString() {
        return "KnowledgeGalaxyDto{" +
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