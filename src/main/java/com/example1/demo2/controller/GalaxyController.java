package com.example1.demo2.controller;

import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.pojo.dto.knowledgeGalaxyDto;
import com.example1.demo2.service.KnowledgeGalaxyService;
import com.example1.demo2.service.PlanetService;
import com.example1.demo2.util.ThreadLocalUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/galaxy")
@Validated
public class GalaxyController {

    @Autowired
    private KnowledgeGalaxyService KnowledgeGalaxyService;
    private PlanetService planetService;

    // 创建星系
    @PostMapping("/create")
    public ResponseMessage<String> create(@Valid @RequestBody knowledgeGalaxyDto galaxyDto) {
        // 获取当前用户ID
        Map<String, Object> map = ThreadLocalUtil.get();
        User userId = (User) map.get("userId");

        // 检查星系名称是否已存在
        KnowledgeGalaxy existingGalaxy = KnowledgeGalaxyService.findByName(galaxyDto.getName());
        if (existingGalaxy != null) {
            return ResponseMessage.error("星系名称已存在，请重新输入");
        }

        galaxyDto.setCreator(userId);
        KnowledgeGalaxyService.create(galaxyDto);
        return ResponseMessage.success("星系\"" + galaxyDto.getName() + "\"创建成功");
    }

    // 获取星系信息
    @GetMapping("/info/{galaxyId}")
    public ResponseMessage<knowledgeGalaxyDto> getInfo(@PathVariable Integer galaxyId) {
        KnowledgeGalaxy galaxy = KnowledgeGalaxyService.findById(galaxyId);
        if (galaxy == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 检查权限（私有星系只有成员可见）
        if (galaxy.getPermissionType() == 1) {
            Map<String, Object> map = ThreadLocalUtil.get();
            Integer userId = (Integer) map.get("userId");
            if (!KnowledgeGalaxyService.isMember(galaxyId, userId)) {
                return ResponseMessage.error("无权限访问该私有星系");
            }
        }

        knowledgeGalaxyDto galaxyDto = KnowledgeGalaxyService.convertToDto(galaxy);
        return ResponseMessage.success(galaxyDto);
    }

    // 更新星系信息
    @PutMapping("/update")
    public ResponseMessage<String> update(@Valid @RequestBody knowledgeGalaxyDto galaxyDto) {
        // 验证操作权限
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        KnowledgeGalaxy galaxy = KnowledgeGalaxyService.findById(galaxyDto.getGalaxyId());
        if (galaxy == null) {
            return ResponseMessage.error("星系不存在");
        }

        if (!galaxy.getCreator().equals(userId)) {
            return ResponseMessage.error("只有创建者可以修改星系信息");
        }

        // 检查是否有修改
        boolean isChanged = false;
        if (!Objects.equals(galaxyDto.getName(), galaxy.getName())) isChanged = true;
        if (!Objects.equals(galaxyDto.getThemeTags(), galaxy.getThemeTags())) isChanged = true;
        if (!Objects.equals(galaxyDto.getPermissionType(), galaxy.getPermissionType())) isChanged = true;

        if (!isChanged) {
            return ResponseMessage.success("没有需要修改的信息");
        }

        KnowledgeGalaxyService.update(galaxyDto);
        return ResponseMessage.success("星系信息更新成功");
    }

    // 获取用户创建的星系列表
    @GetMapping("/myGalaxies")
    public ResponseMessage<List<knowledgeGalaxyDto>> getMyGalaxies() {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        List<knowledgeGalaxyDto> galaxies = KnowledgeGalaxyService.findByCreatorId(userId);
        return ResponseMessage.success(galaxies);
    }

    // 获取用户加入的星系列表
    @GetMapping("/joinedGalaxies")
    public ResponseMessage<List<knowledgeGalaxyDto>> getJoinedGalaxies() {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        List<knowledgeGalaxyDto> galaxies = KnowledgeGalaxyService.findJoinedGalaxies(userId);
        return ResponseMessage.success(galaxies);
    }

    // 加入星系
    @PostMapping("/join/{galaxyId}")
    public ResponseMessage<String> join(@PathVariable Integer galaxyId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        KnowledgeGalaxy galaxy = KnowledgeGalaxyService.findById(galaxyId);
        if (galaxy == null) {
            return ResponseMessage.error("星系不存在");
        }

        if (KnowledgeGalaxyService.isMember(galaxyId, userId)) {
            return ResponseMessage.error("您已经是该星系成员");
        }

        KnowledgeGalaxyService.addMember(galaxyId, userId);
        return ResponseMessage.success("成功加入星系\"" + galaxy.getName() + "\"");
    }

    // 退出星系
    @PostMapping("/leave/{galaxyId}")
    public ResponseMessage<String> leave(@PathVariable Integer galaxyId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        KnowledgeGalaxy galaxy = KnowledgeGalaxyService.findById(galaxyId);
        if (galaxy == null) {
            return ResponseMessage.error("星系不存在");
        }

        if (galaxy.getCreator().equals(userId)) {
            return ResponseMessage.error("创建者不能退出自己的星系");
        }

        if (!KnowledgeGalaxyService.isMember(galaxyId, userId)) {
            return ResponseMessage.error("您不是该星系成员");
        }

        KnowledgeGalaxyService.removeMember(galaxyId, userId);
        return ResponseMessage.success("已退出星系\"" + galaxy.getName() + "\"");
    }

    // 删除星系
    @DeleteMapping("/delete/{galaxyId}")
    public ResponseMessage<String> delete(@PathVariable Integer galaxyId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        KnowledgeGalaxy galaxy = KnowledgeGalaxyService.findById(galaxyId);
        if (galaxy == null) {
            return ResponseMessage.error("星系不存在或已被删除");
        }

        if (!galaxy.getCreator().equals(userId)) {
            return ResponseMessage.error("只有创建者可以删除星系");
        }

        KnowledgeGalaxyService.delete(galaxyId);
        return ResponseMessage.success("星系\"" + galaxy.getName() + "\"已成功删除");
    }

    //将星球加入星系
    @PostMapping("/addPlanet/{galaxyId}/{planetId}")
    public ResponseMessage<String> addPlanet(@PathVariable Integer galaxyId,
                                             @PathVariable String planetId) {
        // Get the current user from ThreadLocal
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        // Check if galaxy exists
        KnowledgeGalaxy galaxy = KnowledgeGalaxyService.findById(galaxyId);
        if (galaxy == null) {
            return ResponseMessage.error("星系不存在");
        }

        // Check if user has permission (e.g., is the creator or a member)
        if (!galaxy.getCreatorId().equals(userId) &&
                !KnowledgeGalaxyService.isMember(galaxyId, userId)) {
            return ResponseMessage.error("您没有权限添加星球到此星系");
        }

        try {
            // Fetch the planet object
            KnowledgePlanet planet = planetService.findByPlanetId(planetId);
            if (planet == null) {
                return ResponseMessage.error("星球不存在");
            }

            // Check if user owns the planet or has permission to add it
            if (!planet.getUserId().equals(userId)) {
                return ResponseMessage.error("您没有权限操作此星球");
            }

            // Add the planet to the galaxy
            KnowledgeGalaxyService.addPlanet(galaxyId, planet);
            return ResponseMessage.success("星球成功加入星系");
        } catch (Exception e) {
            return ResponseMessage.error("添加星球失败：" + e.getMessage());
        }
    }

    @PostMapping("/removePlanet/{galaxyId}/{planetId}")
    public ResponseMessage<String> removePlanet(@PathVariable Integer galaxyId,
                                                @PathVariable String planetId) {
        // Get the current user from ThreadLocal
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        // Check if galaxy exists
        KnowledgeGalaxy galaxy = KnowledgeGalaxyService.findById(galaxyId);
        if (galaxy == null) {
            return ResponseMessage.error("星系不存在");
        }

        // Check if user has permission (e.g., is the creator)
        if (!galaxy.getCreatorId().equals(userId)) {
            return ResponseMessage.error("只有星系创建者可以移除星球");
        }

        try {
            // Fetch the planet object
            KnowledgePlanet planet = planetService.findByPlanetId(planetId);
            if (planet == null) {
                return ResponseMessage.error("星球不存在");
            }

            // Remove the planet from the galaxy
            KnowledgeGalaxyService.removePlanet(galaxyId, planet);
            return ResponseMessage.success("星球已从星系中移除");
        } catch (Exception e) {
            return ResponseMessage.error("移除星球失败：" + e.getMessage());
        }
    }
    // 搜索公开星系
    @GetMapping("/search")
    public ResponseMessage<List<knowledgeGalaxyDto>> search(@RequestParam String keyword) {
        List<knowledgeGalaxyDto> galaxies = KnowledgeGalaxyService.searchPublicGalaxies(keyword);
        return ResponseMessage.success(galaxies);
    }
}