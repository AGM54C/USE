package com.example1.demo2.pojo;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Entity
@Table(name = "tab_knowledge_planet")
public class KnowledgePlanet {

    /**
     * 星球ID(格式:PLNT-YYYYMMDD-XXXX)
     */
    @Id
    @Column(name = "planet_id", length = 20, nullable = false, unique = true)
    private String planetId;

    /**
     * 创建者ID（外键，关联用户表）
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * 星球标题（不超过100字，非空）
     */
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    /**
     * 星球描述（文本类型，可空）
     */
    @Column(name = "description", columnDefinition = "text")
    private String description;

    /**
     * 封面URL（不超过255字）
     */
    @Column(name = "cover_url", length = 255)
    private String coverUrl;

    /**
     * 主题分类ID（非空）
     */
    @Column(name = "theme_id", nullable = false)
    private Integer themeId;

    /**
     * 展示模型（0-文档型，1-图谱型，2-时间轴型，默认0）
     */
    @Column(name = "model_type", nullable = false, columnDefinition = "tinyint default 0")
    private Integer modelType;

    /**
     * 颜色方案（不超过20字）
     */
    @Column(name = "color_scheme", length = 20)
    private String colorScheme;

    /**
     * 可见性（0-私有，1-公开，2-邀请制，默认1）
     */
    @Column(name = "visibility", nullable = false, columnDefinition = "tinyint default 1")
    private Integer visibility;

    /**
     * 燃料值（默认0）
     */
    @Column(name = "fuel_value", nullable = false, columnDefinition = "int default 0")
    private Integer fuelValue;

    /**
     * 亮度值（默认0）
     */
    @Column(name = "brightness", nullable = false, columnDefinition = "int default 0")
    private Integer brightness;

    /**
     * 访问量（默认0）
     */
    @Column(name = "visit_count", nullable = false, columnDefinition = "int default 0")
    private Integer visitCount;

    /**
     * 状态（0-草稿，1-已发布，2-已删除，默认0）
     */
    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 0")
    private Integer status;

    /**
     * 创建时间（自动生成，不可更新）
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Override
    public String toString() {
        return "KnowledgePlanet{" +
                "planetId='" + planetId + '\'' +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", themeId=" + themeId +
                ", modelType=" + modelType +
                ", colorScheme='" + colorScheme + '\'' +
                ", visibility=" + visibility +
                ", fuelValue=" + fuelValue +
                ", brightness=" + brightness +
                ", visitCount=" + visitCount +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    /**
     * 更新时间（自动更新）
     */
    @Column(name = "update_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

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
    public String getPlanetId() {
        return planetId;
    }

    public void setPlanetId(String planetId) {
        this.planetId = planetId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getThemeId() {
        return themeId;
    }

    public void setThemeId(Integer themeId) {
        this.themeId = themeId;
    }

    public Integer getModelType() {
        return modelType;
    }

    public void setModelType(Integer modelType) {
        this.modelType = modelType;
    }

    public String getColorScheme() {
        return colorScheme;
    }

    public void setColorScheme(String colorScheme) {
        this.colorScheme = colorScheme;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public Integer getFuelValue() {
        return fuelValue;
    }

    public void setFuelValue(Integer fuelValue) {
        this.fuelValue = fuelValue;
    }

    public Integer getBrightness() {
        return brightness;
    }

    public void setBrightness(Integer brightness) {
        this.brightness = brightness;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
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