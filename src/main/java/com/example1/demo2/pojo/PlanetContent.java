package com.example1.demo2.pojo;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tab_planet_content")
public class PlanetContent {

    /**
     * 内容ID（自增主键）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id", nullable = false)
    private Integer contentId;

    /**
     * 所属星球ID（外键，关联知识星球表）
     */
    @Column(name = "planet_id", length = 20, nullable = false)
    private String planetId;

    /**
     * 内容类型（0-Markdown，1-PDF，2-视频，默认0）
     */
    @Column(name = "content_type", nullable = false, columnDefinition = "tinyint default 0")
    private Integer contentType;

    /**
     * 内容标题（不超过200字，非空）
     */
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    /**
     * 内容正文（Markdown格式，mediumtext类型）
     */
    @Column(name = "content", columnDefinition = "mediumtext")
    private String content;

    /**
     * 文件URL（不超过255字）
     */
    @Column(name = "file_url", length = 255)
    private String fileUrl;

    /**
     * 推荐语（文本类型，可空）
     */
    @Column(name = "comment", columnDefinition = "text")
    private String comment;

    /**
     * 版本号（默认1）
     */
    @Column(name = "version", nullable = false, columnDefinition = "int default 1")
    private Integer version;

    /**
     * 状态（0-草稿，1-已发布，2-待审核，3-已删除，默认0）
     */
    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 0")
    private Integer status;

    /**
     * 创建时间（自动生成，不可更新）
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 更新时间（自动更新）
     */
    @Column(name = "update_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    /**
     * 包含的评论列表
     */
    @OneToMany(mappedBy = "planetContent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanetComment> planetComments;

    // JPA 回调方法：插入前自动设置时间
    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createTime = now;
        this.updateTime = now;
    }

    // JPA 回调方法：更新前自动更新时间
    @PreUpdate
    protected void onUpdate() {
        this.updateTime = new Date();
    }

    // Getter 和 Setter 方法
    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public String getPlanetId() {
        return planetId;
    }

    public void setPlanetId(String planetId) {
        this.planetId = planetId;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public List<PlanetComment> getPlanetComments() {
        return planetComments;
    }

    public void setPlanetComments(List<PlanetComment> planetComments) {
        this.planetComments = planetComments;
    }

    @Override
    public String toString() {
        return "PlanetContent{" +
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

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public Date getUpdateTime() {
        return updateTime;
    }
}