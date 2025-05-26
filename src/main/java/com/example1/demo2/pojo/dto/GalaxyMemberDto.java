package com.example1.demo2.pojo.dto;

import jakarta.validation.constraints.*;
import java.util.Date;

public class GalaxyMemberDto {

    @NotNull
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

    @NotNull(message = "加入时间不能为空")
    @PastOrPresent(message = "加入时间不能是未来时间")
    private Date joinTime;

    @Pattern(
            regexp = "^(\\[.*\\])?$",
            message = "操作权限必须是JSON数组格式或为空"
    )
    private String operationPermissions;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值不能小于0")
    @Max(value = 1, message = "状态值不能大于1")
    private Integer status;

    private Date createTime;
    private Date updateTime;

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
                '}';
    }

    /**
     * 验证角色类型是否有效
     * @return true if valid, false otherwise
     */
    public boolean isValidRoleType() {
        return roleType != null &&
                (roleType == 0 || roleType == 1 || roleType == 2);
    }

    /**
     * 验证状态是否有效
     * @return true if valid, false otherwise
     */
    public boolean isValidStatus() {
        return status != null &&
                (status == 0 || status == 1);
    }
}