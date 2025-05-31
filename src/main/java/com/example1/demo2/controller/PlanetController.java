package com.example1.demo2.controller;

import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.IPlanetService;
import com.example1.demo2.util.ConvertUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@RestController //接口返回对象，转化为json文本
@RequestMapping("/planet")  //localhost:8081/planet/**
@Validated
public class PlanetController {
    @Autowired
    private IPlanetService planetService;

    /**
     * 创建知识星球
     * 前端请求方式：POST
     * 请求URL：localhost:8081/planet/create
     * 请求参数（JSON格式）：
     * {
     *   "contentTitle": String,           // 星球标题（必填）
     *   "coverUrl": String,        // 封面图片URL
     *   "visibility": Integer,     // 可见性（0-私有，1-公开）
     *   "description": String      // 星球描述
     * }
     * 返回值：成功返回星球id，失败返回错误信息
     */
    @PostMapping("/create")         //localhost:8081/planet/create
    public ResponseMessage<String> create(@Valid @RequestBody KnowledgePlanetDto planet) {
        //查询星球
        KnowledgePlanet p = planetService.findByTitle(planet.getContentTitle());
        if(p!=null) {
            //星球名已经占用
            return ResponseMessage.error("星球名已被占用，请重新输入");
        }
        else{
            //没有占用
            planetService.create(planet);
            return ResponseMessage.success(planet.getPlanetId());
        }
    }

    /**
     * 查看星球信息
     * 前端请求方式：GET
     * 请求URL：localhost:8081/planet/planetinfo
     * 请求参数（Param格式）：
     * {
     *   "planetId": String         // 星球ID（必填）
     * }
     * 返回值：成功返回星球Dto类，失败返回错误信息
     */
    @GetMapping("/planetinfo")
    public ResponseMessage<KnowledgePlanetDto> planetinfo(@Valid @RequestBody KnowledgePlanetDto planet) {
        //根据星球名查询
        KnowledgePlanet p = planetService.findByTitle(planet.getContentTitle());
        if(p==null) {
            return ResponseMessage.error("星球不存在");
        }
        //转化为dto
        KnowledgePlanetDto dto =ConvertUtil.convertKnowledgePlanetToDto(p);
        return ResponseMessage.success(dto);
    }

    /**
     * 更新星球标题
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/planet/updateTitle
     * 请求参数（JSON格式）：
     * {
     *   "planetId": String,        // 星球ID（必填）
     *   "contentTitle": String            // 新标题（必填）
     * }
//     * 返回值：成功或失败信息
     */
    @PutMapping("/updateTitle")
    public ResponseMessage updateTitle(@Valid @RequestBody KnowledgePlanetDto dto) {
        KnowledgePlanet planet = planetService.findByPlanetId(dto.getPlanetId());
        if (planet == null) {
            return ResponseMessage.error("星球不存在");
        }
        if (planet.getContentTitle().equals(dto.getContentTitle())) {
            return ResponseMessage.success("标题未变更");
        }
        planetService.updateTitle(dto.getPlanetId(), dto.getContentTitle());
        return ResponseMessage.success("星球标题更新成功");
    }

    /**
     * 更新星球封面URL
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/planet/updateCoverUrl
     * 请求参数（JSON格式）：
     * {
     *   "planetId": String,        // 星球ID（必填）
     *   "coverUrl": String         // 新封面URL（必填）
     * }
     * 返回值：成功或失败信息
     */
    @PutMapping("/updateCoverUrl")
    public ResponseMessage updateCoverUrl(@Valid @RequestBody KnowledgePlanetDto dto) {
        KnowledgePlanet planet = planetService.findByPlanetId(dto.getPlanetId());
        if (planet == null) {
            return ResponseMessage.error("星球不存在");
        }
        if (Objects.equals(planet.getCoverUrl(), dto.getCoverUrl())) {
            return ResponseMessage.success("封面URL未变更");
        }
        planetService.updateCoverUrl(dto.getPlanetId(), dto.getCoverUrl());
        return ResponseMessage.success("星球封面URL更新成功");
    }

    /**
     * 更新星球可见性
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/planet/updateVisibility
     * 请求参数（JSON格式）：
     * {
     *   "planetId": String,        // 星球ID（必填）
     *   "visibility": Integer      // 可见性（0-私有，1-公开）
     * }
     * 返回值：成功或失败信息
     */
    @PutMapping("/updateVisibility")
    public ResponseMessage updateVisibility(@Valid @RequestBody KnowledgePlanetDto dto) {
        KnowledgePlanet planet = planetService.findByPlanetId(dto.getPlanetId());
        if (planet == null) {
            return ResponseMessage.error("星球不存在");
        }
        if (planet.getVisibility().equals(dto.getVisibility())) {
            return ResponseMessage.success("可见性未变更");
        }
        planetService.updateVisibility(dto.getPlanetId(), dto.getVisibility());
        return ResponseMessage.success("星球可见性更新成功");
    }

    /**
     * 更新星球简介
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/planet/updatedescription
     * 请求参数（JSON格式）：
     * {
     *   "planetId": String,        // 星球ID（必填）
     *   "description": String            // 新标题（必填）
     * }
     //     * 返回值：成功或失败信息
     */
    @PutMapping("/updatedescription")
    public ResponseMessage updatedescription(@Valid @RequestBody KnowledgePlanetDto dto) {
        KnowledgePlanet planet = planetService.findByPlanetId(dto.getPlanetId());
        if (planet == null) {
            return ResponseMessage.error("星球不存在");
        }
        if (planet.getDescription().equals(dto.getDescription())) {
            return ResponseMessage.success("简介未变更");
        }
        planetService.updatedescription(dto.getPlanetId(), dto.getDescription());
        return ResponseMessage.success("星球简介更新成功");
    }

    /**
     * 更新星球标题
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/planet/updateTitle
     * 请求参数（JSON格式）：
     * {
     *   "planetId": String,        // 星球ID（必填）
     *   "contentDetail": String            // 新内容（必填）
     * }
     //     * 返回值：成功或失败信息
     */
    @PutMapping("/updatecontentdetail")
    public ResponseMessage updatedetail(@Valid @RequestBody KnowledgePlanetDto dto) {
        KnowledgePlanet planet = planetService.findByPlanetId(dto.getPlanetId());
        if (planet == null) {
            return ResponseMessage.error("星球不存在");
        }
        if (planet.getContentDetail().equals(dto.getContentDetail())) {
            return ResponseMessage.success("内容未变更");
        }
        planetService.updatedetail(dto.getPlanetId(), dto.getContentDetail());
        return ResponseMessage.success("星球内容更新成功");
    }

    /**
     * 更新星球亮度
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/planet/updatebrightness
     * 请求参数（JSON格式）：
     * {
     *   "planetId": String,        // 星球ID（必填）
     *   "brightness": Integer            // 亮度（必填）
     * }
     //     * 返回值：成功或失败信息
     */
    @PutMapping("/updatebrightness")
    public ResponseMessage updatebrightness(@Valid @RequestBody KnowledgePlanetDto dto) {
        KnowledgePlanet planet = planetService.findByPlanetId(dto.getPlanetId());
        if (planet == null) {
            return ResponseMessage.error("星球不存在");
        }
        if (planet.getContentTitle().equals(dto.getBrightness())) {
            return ResponseMessage.success("亮度未变更");
        }
        planetService.updatebrightness(dto.getPlanetId(), dto.getBrightness());
        return ResponseMessage.success("星球亮度更新成功");
    }
    /**
     * 更新星球标题
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/planet/updateTitle
     * 请求参数（JSON格式）：
     * {
     *   "planetId": String,        // 星球ID（必填）
     *   "title": String            // 新标题（必填）
     * }
     //     * 返回值：成功或失败信息
     */
    @PutMapping("/updatefuelvalue")
    public ResponseMessage updatefuelvalue(@Valid @RequestBody KnowledgePlanetDto dto) {
        KnowledgePlanet planet = planetService.findByPlanetId(dto.getPlanetId());
        if (planet == null) {
            return ResponseMessage.error("星球不存在");
        }
        if (planet.getContentTitle().equals(dto.getFuelValue())) {
            return ResponseMessage.success("燃料未变更");
        }
        planetService.updatefuelvalue(dto.getPlanetId(), dto.getFuelValue());
        return ResponseMessage.success("星球燃料更新成功");
    }
    /**
     * 将评论加入星球
     * 前端请求方式：POST
     * 请求URL：localhost:8081/planet/{planetId}/addComment/{commentId}
     * 请求参数：
     *   - path: planetId           // 星球ID
     *   - path: commentId          // 评论ID
     * 返回值：成功或失败信息
     */
    @PostMapping("/{planetId}/addComment/{commentId}")
    public ResponseMessage addCommentToPlanet(
            @PathVariable String planetId,
            @PathVariable Long commentId
    ) {
        // 校验星球存在
        KnowledgePlanet planet = planetService.findByPlanetId(planetId);
        if (planet == null) {
            return ResponseMessage.error("星球不存在");
        }
        // 操作由commentService处理
        planetService.addCommentToPlanet(planetId, commentId);
        return ResponseMessage.success("评论已加入星球");
    }

    /**
     * 从星球中移除评论
     * 前端请求方式：DELETE
     * 请求URL：localhost:8081/planet/{planetId}/removeComment/{commentId}
     * 请求参数：
     *   - path: planetId           // 星球ID
     *   - path: commentId          // 评论ID
     * 返回值：成功或失败信息
     */
    @DeleteMapping("/{planetId}/removeComment/{commentId}")
    public ResponseMessage removeCommentFromPlanet(
            @PathVariable String planetId,
            @PathVariable Long commentId
    ) {
        // 操作由commentService处理
        planetService.removeCommentFromPlanet(planetId, commentId);
        return ResponseMessage.success("评论已从星球移除");
    }

    /**
     * 注销星球
     * 前端请求方式：DELETE
     * 请求URL：localhost:8081/planet/delete
     * 请求参数（JSON格式）：
     * {
     *   "planetId": String         // 星球ID（必填）
     * }
     * 返回值：成功或失败信息
     */
    @DeleteMapping("/delete")
    public ResponseMessage<String> delete(@Valid @RequestBody KnowledgePlanetDto planet) {
        // 通过id删除星球
        KnowledgePlanet p = planetService.findByPlanetId(planet.getPlanetId());
        if (p == null) {
            // 星球不存在
            return ResponseMessage.error("星球不存在或已被删除");
        } else {
            // 删除星球及其关联评论（级联删除）
            planetService.deleteWithComments(p.getPlanetId());
            return ResponseMessage.success("星球及关联评论已成功删除");
        }
    }
}
