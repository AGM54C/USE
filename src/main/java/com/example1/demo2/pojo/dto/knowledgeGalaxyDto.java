package com.example1.demo2.pojo.dto;

import jakarta.validation.constraints.*;
import java.util.Date;

public class knowledgeGalaxyDto {

    @NotNull
    private Integer galaxyId;

    @NotBlank(message = "星系名称不能为空")
    @Size(max = 100, message = "星系名称长度不能超过100个字符")
    private String name;

    @Pattern(
            regexp = "^\\[.*\\]$",
            message = "主题标签必须是JSON数组格式"
    )
    private String themeTags;

    @NotNull(message = "权限类型不能为空")
    @Min(value = 0, message = "权限类型值不能小于0")
    @Max(value = 1, message = "权限类型值不能大于1")
    private Integer permissionType;

    @NotNull(message = "创建者ID不能为空")
    private Integer creatorId;

    @Min(value = 0, message = "成员数量不能为负数")
    private Integer memberCount;

    private Date lastActivityTime;
    private Date createTime;
    private Date updateTime;

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

    @Override
    public String toString() {
        return "KnowledgeGalaxyDto{" +
                "galaxyId=" + galaxyId +
                ", name='" + name + '\'' +
                ", themeTags='" + themeTags + '\'' +
                ", permissionType=" + permissionType +
                ", creatorId=" + creatorId +
                ", memberCount=" + memberCount +
                ", lastActivityTime=" + lastActivityTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}