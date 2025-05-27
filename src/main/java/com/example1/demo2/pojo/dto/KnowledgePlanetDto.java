package com.example1.demo2.pojo.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.Date;

public class KnowledgePlanetDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // 创建时自动生成，无需前端传入
    @Pattern(regexp = "^PLNT-\\d{8}-\\d{4}$", message = "星球ID格式错误")
    private String planetId;

    // 从认证信息获取，前端无需传入
    @NotNull(message = "创建者ID不能为空")
    private Integer userId;

    @NotBlank(message = "星球标题不能为空")
    @Size(max = 100, message = "星球标题长度不能超过100字符")
    private String title;

    @Size(max = 200, message = "星球描述长度不能超过200字符")
    private String description;

    @Size(max = 255, message = "封面URL长度不能超过255字符")
    private String coverUrl;

    @NotNull(message = "主题分类ID不能为空")
    @Positive(message = "主题分类ID必须为正整数")
    private Integer themeId;

    //@NotBlank(message = "展示模型不能为空")
    @Min(value = 0, message = "展示模型取值范围为0-2")
    @Max(value = 2, message = "展示模型取值范围为0-2")
    private Integer modelType;

    @Size(max = 20, message = "颜色方案长度不能超过20字符")
    private String colorScheme;

    //@NotBlank(message = "可见性不能为空")
    @Min(value = 0, message = "可见性取值范围为0-2")
    @Max(value = 2, message = "可见性取值范围为0-2")
    private Integer visibility;

    // 自动计算，前端无需传入
    private Integer fuelValue;
    private Integer brightness;
    private Integer visitCount;

    // 自动维护，前端无需传入
    private Integer status;
    private Date createTime;
    private Date updateTime;

    // Getters and Setters
    public String getPlanetId() { return planetId; }
    public void setPlanetId(String planetId) { this.planetId = planetId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
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

    @Override
    public String toString() {
        return "PlanetDto{" +
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
}