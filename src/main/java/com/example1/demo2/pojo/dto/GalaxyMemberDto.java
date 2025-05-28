package com.example1.demo2.pojo.dto;

import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.List;

public class GalaxyMemberDto {

    private Integer memberId;

    @NotNull(message = "星系ID不能为空")
    @Positive(message = "星系ID必须为正整数")
    private Integer galaxyId;

    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须为正整数")
    private Integer userId;

    @NotNull(message = "角色类型不能为空")
    @Min(value = 0, message = "角色类型值不能小于0")
    @Max(value = 2, message = "角色类型值不能大于2")
    private Integer roleType;

    @PastOrPresent(message = "加入时间不能是未来时间")
    private Date joinTime;

    @Pattern(
            regexp = "^(\\[.*\\])?$",
            message = "操作权限必须是JSON数组格式或为空"
    )
    private String operationPermissions;

    @Min(value = 0, message = "状态值不能小于0")
    @Max(value = 1, message = "状态值不能大于1")
    private Integer status;


    private Date createTime;
    private Date updateTime;


    private List<Integer> createdPlanetIds;

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

    public Date getLastActivityTime() {
        return updateTime;
    }

    public void setLastActivityTime(Date lastActivityTime) {
        this.updateTime = lastActivityTime;
    }

    public List<Integer> getCreatedPlanetIds() {
        return createdPlanetIds;
    }

    public void setCreatedPlanetIds(List<Integer> createdPlanetIds) {
        this.createdPlanetIds = createdPlanetIds;
    }

    @Override
    public String toString() {
        return "GalaxyMemberDto{" +
                "memberId=" + memberId +
                ", galaxyId=" + galaxyId +
                ", userId=" + userId +
                ", roleType=" + roleType +
                ", joinTime=" + joinTime +
                ", operationPermissions='" + operationPermissions + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createdPlanetIds=" + createdPlanetIds +
                '}';
    }

    /**
     * 角色类型常量定义
     * 便于在业务逻辑中使用
     */
    public static class RoleType {
        public static final int MEMBER = 0;    // 普通成员
        public static final int ADMIN = 1;     // 管理员
        public static final int CREATOR = 2;   // 创建者
    }

    /**
     * 状态常量定义
     */
    public static class Status {
        public static final int NORMAL = 0;    // 正常
        public static final int DISABLED = 1;  // 禁用
    }

    /**
     * 验证角色类型是否有效
     * @return true if valid, false otherwise
     */
    public boolean isValidRoleType() {
        return roleType != null &&
                (roleType == RoleType.MEMBER ||
                        roleType == RoleType.ADMIN ||
                        roleType == RoleType.CREATOR);
    }

    /**
     * 验证状态是否有效
     * @return true if valid, false otherwise
     */
    public boolean isValidStatus() {
        return status != null &&
                (status == Status.NORMAL ||
                        status == Status.DISABLED);
    }

    /**
     * 检查是否为管理员或创建者
     * @return true if the member has admin or creator role
     */
    public boolean hasAdminPrivileges() {
        return roleType != null &&
                (roleType == RoleType.ADMIN || roleType == RoleType.CREATOR);
    }

    /**
     * 检查是否为创建者
     * @return true if the member is a creator
     */
    public boolean isCreator() {
        return roleType != null && roleType == RoleType.CREATOR;
    }
}