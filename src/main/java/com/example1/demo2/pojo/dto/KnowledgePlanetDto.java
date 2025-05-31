package com.example1.demo2.pojo.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.Date;

public class KnowledgePlanetDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // 创建时自动生成，无需前端传入
    @Pattern(regexp = "^PLNT-\\d{8}-[A-Z0-9]{4}$", message = "星球ID格式错误")
    private String planetId;

    // 从认证信息获取，前端无需传入
    private Integer userId;

    @Size(max = 100, message = "星球标题长度不能超过100字符")
    private String contentTitle;

    /**
     * 知识详情
     */
    private String contentDetail;

    @Size(max = 200, message = "星球描述长度不能超过200字符")
    private String description;

    @Size(max = 255, message = "封面URL长度不能超过255字符")
    private String coverUrl;

    @Positive(message = "主题分类ID必须为正整数")
    private Integer themeId;

    @Min(value = 0, message = "展示模型取值范围为0-2")
    @Max(value = 2, message = "展示模型取值范围为0-2")
    private Integer modelType = 0; // 默认值0

    @Size(max = 20, message = "颜色方案长度不能超过20字符")
    private String colorScheme;

    @Min(value = 0, message = "可见性取值范围为0-2")
    @Max(value = 2, message = "可见性取值范围为0-2")
    private Integer visibility = 1; // 默认值1


    // 自动计算，前端无需传入
    private Integer fuelValue = 0; // 默认值0
    private Integer brightness = 0; // 默认值0
    private Integer visitCount = 0; // 默认值0

    // 自动维护，前端无需传入
    private Integer status = 0; // 默认值0
    private Date createTime;
    private Date updateTime;

    // Getters and Setters
    public String getPlanetId() { return planetId; }
    public void setPlanetId(String planetId) { this.planetId = planetId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public Integer getThemeId() { return themeId; }
    public void setThemeId(Integer themeId) { this.themeId = themeId; }
    public Integer getModelType() { return modelType; }
    public void setModelType(Integer modelType) { this.modelType = modelType; }
    public String getColorScheme() { return colorScheme; }
    public void setColorScheme(String colorScheme) { this.colorScheme = colorScheme; }
    public Integer getVisibility() { return visibility; }
    public void setVisibility(Integer visibility) { this.visibility = visibility; }
    public Integer getFuelValue() { return fuelValue; }
    public void setFuelValue(Integer fuelValue) { this.fuelValue = fuelValue; }
    public Integer getBrightness() { return brightness; }
    public void setBrightness(Integer brightness) { this.brightness = brightness; }
    public Integer getVisitCount() { return visitCount; }
    public void setVisitCount(Integer visitCount) { this.visitCount = visitCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentDetail() {
        return contentDetail;
    }

    public void setContentDetail(String contentDetail) {
        this.contentDetail = contentDetail;
    }

    @Override
    public String toString() {
        return "KnowledgePlanetDto{" +
                "planetId='" + planetId + '\'' +
                ", userId=" + userId +
                ", contentTitle='" + contentTitle + '\'' +
                ", contentDetail='" + contentDetail + '\'' +
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
}