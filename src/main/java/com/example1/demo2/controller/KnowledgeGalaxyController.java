package com.example1.demo2.controller;

import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.dto.KnowledgeGalaxyDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.IGalaxyService;
import com.example1.demo2.util.ConvertUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/galaxy")  //localhost:8081/galaxy/**
@Validated
public class KnowledgeGalaxyController {

    @Autowired
    private IGalaxyService galaxyService;

    /**
     * 创建知识星系接口
     * 前端请求方式：POST
     * 请求URL：localhost:8081/galaxy/create
     * 请求参数（JSON格式）：
     * {
     *   "userId": 1,              // 创建者ID（必填）
     *   "name": "人工智能",        // 星系名称（必填）
     *   "label": "AI技术",         // 星系标签（必填）
     *   "permission": 1,          // 权限：0私有，1公开（选填，默认1）
     *   "inviteCode": "AI2025"    // 邀请码（选填）
     * }
     * 返回值：成功返回星系ID，失败返回错误信息
     */
    @PostMapping("/create")
    public ResponseMessage create(@Valid @RequestBody KnowledgeGalaxyDto galaxy) {
        // 查询星系名是否已存在
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyByName(galaxy.getName());
        if (g != null) {
            // 星系名已经占用
            return ResponseMessage.error("星系名已被占用，请重新输入");
        } else {
            // 创建星系
            galaxyService.createGalaxy(galaxy);
            return ResponseMessage.success(galaxy.getGalaxyId());
        }
    }

    /**
     * 查看星系信息接口
     * 前端请求方式：GET
     * 请求URL：localhost:8081/galaxy/galaxyinfo
     * 请求参数（JSON格式）：
     * {
     *   "name": "人工智能"    // 星系名称（必填）
     * }
     * 返回值：成功返回星系完整信息，失败返回错误信息
     */
    @GetMapping("/galaxyinfo")
    public ResponseMessage galaxyinfo(@Valid @RequestBody KnowledgeGalaxyDto galaxy) {
        // 根据星系名查询
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyByName(galaxy.getName());
        if (g == null) {
            return ResponseMessage.error("星系不存在");
        }
        // 转化为dto
        KnowledgeGalaxyDto dto = ConvertUtil.convertKnowledgeGalaxyToDto(g);
        return ResponseMessage.success(dto);
    }

    /**
     * 更新星系信息接口
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/galaxy/update
     * 请求参数（JSON格式）：
     * {
     *   "galaxyId": 1,           // 星系ID（必填）
     *   "name": "人工智能2.0",    // 星系名称（选填）
     *   "label": "AI前沿技术",    // 星系标签（选填）
     *   "permission": 0,         // 权限（选填）
     *   "inviteCode": "AI2025V2" // 邀请码（选填）
     * }
     * 返回值：成功返回更新成功信息，失败返回错误信息
     */
    @PutMapping("/update")
    public ResponseMessage update(@Valid @RequestBody KnowledgeGalaxyDto galaxy) {
        // 根据ID查找星系
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyById(galaxy.getGalaxyId());
        if (g == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 判断星系信息是否有变化
        boolean isChanged = false;
        if (!Objects.equals(galaxy.getName(), g.getName())) isChanged = true;
        if (!Objects.equals(galaxy.getLabel(), g.getLabel())) isChanged = true;
        if (!Objects.equals(galaxy.getPermission(), g.getPermission())) isChanged = true;
        if (!Objects.equals(galaxy.getInviteCode(), g.getInviteCode())) isChanged = true;

        if (!isChanged) {
            return ResponseMessage.success("没有需要修改的信息！");
        }

        // 更新信息
        galaxyService.updateGalaxy(galaxy);
        return ResponseMessage.success("已更新星系信息！");
    }

    /**
     * 删除星系接口
     * 前端请求方式：DELETE
     * 请求URL：localhost:8081/galaxy/delete
     * 请求参数（JSON格式）：
     * {
     *   "galaxyId": 1    // 星系ID（必填）
     * }
     * 返回值：成功返回被删除的星系ID，失败返回错误信息
     */
    @DeleteMapping("/delete")
    public ResponseMessage delete(@Valid @RequestBody KnowledgeGalaxyDto galaxy) {
        // 通过ID查找星系
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyById(galaxy.getGalaxyId());
        if (g == null) {
            // 星系不存在
            return ResponseMessage.error("星系不存在或已被删除");
        } else {
            // 删除星系
            galaxyService.deleteGalaxy(g.getGalaxyId());
            return ResponseMessage.success(galaxy.getGalaxyId());
        }
    }

    /**
     * 添加知识星球到星系接口
     * 前端请求方式：POST
     * 请求URL：localhost:8081/galaxy/addplanet
     * 请求参数（JSON格式）：
     * {
     *   "galaxyId": 1,                      // 星系ID（必填）
     *   "planetId": "PLNT-20250101-ABCD"    // 星球ID（必填）
     * }
     * 返回值：成功返回成功信息，失败返回错误信息
     */
    @PostMapping("/addplanet")
    public ResponseMessage addPlanet(@RequestParam String galaxyId, @RequestParam String planetId) {
        // 检查星系是否存在
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyById(galaxyId);
        if (g == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 添加星球到星系
        galaxyService.addKnowledgePlanetToGalaxy(galaxyId, planetId);
        return ResponseMessage.success("成功添加星球到星系");
    }
    /**
     * 移除知识星球到星系接口
     * 前端请求方式：Delete
     * 请求URL：localhost:8081/galaxy/deleteplanet
     * 请求参数（JSON格式）：
     * {
     *   "galaxyId": 1,                      // 星系ID（必填）
     *   "planetId": "PLNT-20250101-ABCD"    // 星球ID（必填）
     * }
     * 返回值：成功返回成功信息，失败返回错误信息
     */
    @DeleteMapping("/deleteplanet")
    public ResponseMessage deletePlanet(@RequestParam String galaxyId, @RequestParam String planetId) {
        // 检查星系是否存在
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyById(galaxyId);
        if (g == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 移除星球从星系
        galaxyService.removeKnowledgePlanetFromGalaxy(galaxyId, planetId);
        return ResponseMessage.success("成功移除星球从星系");
    }
}


