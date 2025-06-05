package com.example1.demo2.controller;

import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.dto.KnowledgeGalaxyDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.IGalaxyService;
import com.example1.demo2.service.IRewardService;
import com.example1.demo2.util.ConvertUtil;
import com.example1.demo2.util.ThreadLocalUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/galaxy")  //localhost:8081/galaxy/**
@Validated
public class KnowledgeGalaxyController {

    @Autowired
    private IGalaxyService galaxyService;

    @Autowired
    private IRewardService rewardService;

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
    @Transactional
    public ResponseMessage create(@Valid @RequestBody KnowledgeGalaxyDto galaxy) {
        // 获取当前用户ID
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer userId = (Integer) userInfo.get("userId");
        galaxy.setUserId(userId);

        // 检查知识星云值是否足够
        if (!rewardService.hasSufficientKnowledgeDust(userId, 1)) {
            return ResponseMessage.error("知识星云值不足，无法创建星系");
        }

        // 查询星系名是否已存在
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyByName(galaxy.getName());
        if (g != null) {
            return ResponseMessage.error("星系名已被占用，请重新输入");
        } else {
            // 创建星系
            galaxyService.createGalaxy(galaxy);

            // 扣除知识星云值
            try {
                Integer newDust = rewardService.consumeForGalaxyCreation(userId);
                Map<String, Object> result = new HashMap<>();
                result.put("galaxyId", galaxy.getGalaxyId());
                result.put("newKnowledgeDust", newDust);
                return ResponseMessage.success(result);
            } catch (Exception e) {
                // 如果扣除失败，需要回滚星系创建
                throw new RuntimeException("创建星系失败: " + e.getMessage());
            }
        }
    }



    /**
     * 查看星系信息接口（RESTful风格）
     * 前端请求方式：GET
     * 请求URL：localhost:8081/galaxy/21  （其中21是星系ID）
     * 返回值：成功返回星系完整信息，失败返回错误信息
     */
    @GetMapping("/{galaxyId}")
    public ResponseMessage<KnowledgeGalaxyDto> getGalaxyInfo(
            @PathVariable @Positive(message = "星系ID必须为正数") Integer galaxyId) {

        // 根据ID查找星系
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyById(galaxyId);
        if (g == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 转换为DTO返回
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
        // 获取当前用户ID
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer currentUserId = (Integer) userInfo.get("userId");

        // 根据ID查找星系
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyById(galaxy.getGalaxyId());
        if (g == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 验证是否为创建者
        if (!g.getUserId().equals(currentUserId)) {
            return ResponseMessage.error("只有星系创建者可以修改星系信息");
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
     * 请求参数（Param格式）：
     * {
     *   "galaxyId": 1    // 星系ID（必填）
     * }
     * 返回值：成功返回被删除的星系ID，失败返回错误信息
     */
    @DeleteMapping("/delete")
    public ResponseMessage delete(@Valid @RequestParam Integer galaxyId) {
        // 获取当前用户ID
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer currentUserId = (Integer) userInfo.get("userId");

        // 通过ID查找星系
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyById(galaxyId);
        if (g == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 验证是否为创建者
        if (!g.getUserId().equals(currentUserId)) {
            return ResponseMessage.error("只有星系创建者可以删除星系");
        }

        // 删除星系
        galaxyService.deleteGalaxy(g.getGalaxyId());
        return ResponseMessage.success(galaxyId);
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
    public ResponseMessage addPlanet(@RequestParam Integer galaxyId, @RequestParam String planetId) {
        // 获取当前用户ID
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer currentUserId = (Integer) userInfo.get("userId");

        // 检查星系是否存在
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyById(galaxyId);
        if (g == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 验证是否为创建者
        if (!g.getUserId().equals(currentUserId)) {
            return ResponseMessage.error("只有星系创建者可以添加星球");
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
    public ResponseMessage deletePlanet(@RequestParam Integer galaxyId, @RequestParam String planetId) {
        // 获取当前用户ID
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer currentUserId = (Integer) userInfo.get("userId");

        // 检查星系是否存在
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyById(galaxyId);
        if (g == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 验证是否为创建者
        if (!g.getUserId().equals(currentUserId)) {
            return ResponseMessage.error("只有星系创建者可以移除星球");
        }

        // 移除星球从星系
        galaxyService.removeKnowledgePlanetFromGalaxy(galaxyId, planetId);
        return ResponseMessage.success("成功移除星球从星系");
    }

    /**
     * 用户更改星系名称接口
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/galaxy/updateName
     * 请求参数（JSON格式）：
     * {
     *   "galaxyId": 1    // 星系ID（必填）
     * }
     * 返回值：成功返回新名字，失败返回错误信息
     */
    @PutMapping("/updateName")
    public ResponseMessage updateName(@RequestParam Integer galaxyId, @RequestParam String newName) {
        // 获取当前用户ID
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer currentUserId = (Integer) userInfo.get("userId");

        // 查找星系
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyById(galaxyId);
        if (g == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 验证是否为创建者
        if (!g.getUserId().equals(currentUserId)) {
            return ResponseMessage.error("只有星系创建者可以修改名称");
        }

        // 更新星系名称
        galaxyService.updateGalaxyName(galaxyId, newName);
        return ResponseMessage.success("星系名称已更新为：" + newName);
    }

    /**
     * 用户更改星系标签接口
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/galaxy/updateLabel
     * 请求参数（JSON格式）：
     * {
     *   "galaxyId": 1    // 星系ID（必填）
     * }
     * 返回值：成功返回新标签，失败返回错误信息
     */
    @PutMapping("/updateLabel")
    public ResponseMessage updateLabel(@RequestParam Integer galaxyId, @RequestParam String newLabel) {
        // 获取当前用户ID
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer currentUserId = (Integer) userInfo.get("userId");

        // 查找星系
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyById(galaxyId);
        if (g == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 验证是否为创建者
        if (!g.getUserId().equals(currentUserId)) {
            return ResponseMessage.error("只有星系创建者可以修改标签");
        }

        // 更新星系标签
        galaxyService.updateGalaxyLabel(galaxyId, newLabel);
        return ResponseMessage.success("星系标签已更新为：" + newLabel);
    }

    /**
     * 用户更改星系权限接口
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/galaxy/updatePermission
     * 请求参数（JSON格式）：
     * {
     *   "galaxyId": 1,    // 星系ID（必填）
     *   "newPermission": 0 // 新权限：0私有，1公开（必填）
     * }
     * 返回值：成功返回新权限，失败返回错误信息
     */
    @PutMapping("/updatePermission")
    public ResponseMessage updatePermission(@RequestParam Integer galaxyId, @RequestParam Integer newPermission) {
        // 获取当前用户ID
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer currentUserId = (Integer) userInfo.get("userId");

        // 查找星系
        KnowledgeGalaxy g = galaxyService.getKnowledgeGalaxyById(galaxyId);
        if (g == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 验证是否为创建者
        if (!g.getUserId().equals(currentUserId)) {
            return ResponseMessage.error("只有星系创建者可以修改权限");
        }

        // 更新星系权限
        galaxyService.updateGalaxyPermission(galaxyId, newPermission);
        return ResponseMessage.success("星系权限已更新为：" + (newPermission == 0 ? "私有" : "公开"));
    }
}


