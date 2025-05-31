package com.example1.demo2.pojo.dto;

import java.io.Serializable;
import jakarta.validation.constraints.*;
import java.util.Date;

public class PlanetCommentDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // 评论ID（创建时自动生成，更新时必须存在）
    @Null(groups = CreateGroup.class, message = "创建评论时无需指定ID")
    @NotNull(groups = UpdateGroup.class, message = "更新评论时ID不能为空")
    private Long commentId;

    // 星球ID（必填，格式校验）
    @NotBlank(message = "星球ID不能为空")
    @Pattern(
            regexp = "^PLNT-\\d{8}-[A-Z0-9]{4}$",
            message = "星球ID格式错误，需为PLNT-YYYYMMDD-XXXX"
    )
    private String planetId;

    // 用户ID（必填，从认证信息获取）
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    // 父评论ID（默认0，表示一级评论）
    @Min(value = 0, message = "父评论ID不能为负数")
    private Long parentId = 0L;

    // 评论层级（自动计算，前端无需传入）
    @Null(message = "评论层级由系统自动计算")
    private Integer level;

    // 评论内容（必填，长度校验）
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论内容不能超过2000字符")
    private String content;

    // 点赞数（自动维护，前端无需传入）
    @Null(message = "点赞数由系统自动维护")
    private Integer likeCount;

    // 回复数（自动维护，前端无需传入）
    @Null(message = "回复数由系统自动维护")
    private Integer replyCount;

    // 状态（可选，范围校验，默认0-正常）
    @Min(value = 0, message = "状态取值范围为0-2")
    @Max(value = 2, message = "状态取值范围为0-2")
    private Integer status;

    // 创建时间（自动生成，不可修改）
    @Null(message = "创建时间由系统自动生成")
    private Date createTime;

    // 更新时间（自动更新，不可修改）
    @Null(message = "更新时间由系统自动生成")
    private Date updateTime;

    // 分组校验接口
    public interface CreateGroup { }
    public interface UpdateGroup { }

    // Getter 和 Setter
    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public String getPlanetId() { return planetId; }
    public void setPlanetId(String planetId) { this.planetId = planetId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}