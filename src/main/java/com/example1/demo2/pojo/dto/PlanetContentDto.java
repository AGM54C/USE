package com.example1.demo2.pojo.dto;

import jakarta.validation.constraints.*;
import java.util.Date;

public class PlanetContentDto {
    // 创建时自动生成，无需前端传入
    private Integer contentId;

    @NotBlank(message = "所属星球ID不能为空")
    @Pattern(regexp = "^PLNT-\\d{8}-\\d{4}$", message = "星球ID格式错误")
    private String planetId;

    @NotNull(message = "内容类型不能为空")
    @Min(value = 0, message = "内容类型取值范围为0-2")
    @Max(value = 2, message = "内容类型取值范围为0-2")
    private Integer contentType;

    @NotBlank(message = "内容标题不能为空")
    @Size(max = 200, message = "内容标题长度不能超过200字符")
    private String title;

    // 根据contentType互斥校验
    @Size(max = 16777215, message = "内容长度超过限制") // mediumtext最大长度
    private String content;

    @Size(max = 255, message = "文件URL长度不能超过255字符")
    private String fileUrl;

    @Size(max = 200, message = "推荐语长度不能超过200字符")
    private String comment;

    // 自动维护版本号，前端无需传入
    private Integer version;

    // 自动维护状态，前端无需传入
    private Integer status;
    private Date createTime;
    private Date updateTime;

    // Getters and Setters
    public Integer getContentId() { return contentId; }
    public void setContentId(Integer contentId) { this.contentId = contentId; }
    public String getPlanetId() { return planetId; }
    public void setPlanetId(String planetId) { this.planetId = planetId; }
    public Integer getContentType() { return contentType; }
    public void setContentType(Integer contentType) { this.contentType = contentType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Integer getStatus() { return status; }

    @Override
    public String toString() {
        return "PlanetContentDto{" +
                "contentId=" + contentId +
                ", planetId='" + planetId + '\'' +
                ", contentType=" + contentType +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", comment='" + comment + '\'' +
                ", version=" + version +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public void setStatus(Integer status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}