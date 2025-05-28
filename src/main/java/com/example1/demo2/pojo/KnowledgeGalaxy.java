package com.example1.demo2.pojo;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
@Entity
@Table(name = "tab_knowledge_galaxy")
public class KnowledgeGalaxy {
    /**
     * 星系ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "galaxy_id")
    private Integer galaxyId;

    /**
     * 星系名称
     */
    @Column(name = "user_id",length = 20, nullable = false)
    private Integer userId;

    /**
     * 星系标签
     */
    @Column(name = "label", length = 100, nullable = false)
    private String label;

    /**
     * 星系权限(0 私有  1公开）
     */
    @Column(name = "permission", nullable = false, columnDefinition = "tinyint default 1")
    private Integer permission;

    /**
     * 星系邀请码
     */
    @Column(name = "invite_code", length = 20, unique = true)
    private String inviteCode;

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
}
