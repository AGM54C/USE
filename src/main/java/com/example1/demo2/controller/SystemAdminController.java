package com.example1.demo2.controller;

import com.example1.demo2.pojo.SystemAdmin;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.ISystemAdminService;
import com.example1.demo2.util.ThreadLocalUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统管理员控制器
 * 处理系统级管理功能：删除违规内容、封禁用户等
 * 注意：所有接口都需要系统管理员权限
 */
@RestController
@RequestMapping("/admin")
@Validated
public class SystemAdminController {

    @Autowired
    private ISystemAdminService systemAdminService;

    /**
     * 删除星系违规评论
     * 前端请求方式：DELETE
     * 请求URL：localhost:8081/admin/galaxy/comment/{commentId}
     * 路径参数：commentId - 评论ID
     * 请求参数：reason - 删除原因
     * 返回值：成功信息
     * 权限：系统管理员
     */
    @DeleteMapping("/galaxy/comment/{commentId}")
    public ResponseMessage deleteGalaxyComment(
            @PathVariable @NotNull Integer commentId,
            @RequestParam @NotNull String reason) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer adminId = (Integer) userInfo.get("userId");

            // 验证系统管理员权限
            if (!systemAdminService.isSystemAdmin(adminId)) {
                return ResponseMessage.error("无系统管理员权限");
            }

            boolean success = systemAdminService.deleteGalaxyComment(
                    commentId, adminId, reason
            );

            if (success) {
                return ResponseMessage.success("评论已删除");
            }
            return ResponseMessage.error("删除失败");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 删除星球违规评论
     * 前端请求方式：DELETE
     * 请求URL：localhost:8081/admin/planet/comment/{commentId}
     * 路径参数：commentId - 评论ID
     * 请求参数：reason - 删除原因
     * 返回值：成功信息
     * 权限：系统管理员
     */
    @DeleteMapping("/planet/comment/{commentId}")
    public ResponseMessage deletePlanetComment(
            @PathVariable @NotNull Integer commentId,
            @RequestParam @NotNull String reason) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer adminId = (Integer) userInfo.get("userId");

            if (!systemAdminService.isSystemAdmin(adminId)) {
                return ResponseMessage.error("无系统管理员权限");
            }

            boolean success = systemAdminService.deletePlanetComment(
                    commentId, adminId, reason
            );

            if (success) {
                return ResponseMessage.success("评论已删除");
            }
            return ResponseMessage.error("删除失败");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 封禁用户
     * 前端请求方式：POST
     * 请求URL：localhost:8081/admin/user/ban
     * 请求参数（JSON格式）：
     * {
     *   "userId": 2,              // 被封禁用户ID（必填）
     *   "reason": "违规发言",      // 封禁原因（必填）
     *   "duration": 7             // 封禁天数（必填）
     * }
     * 返回值：成功信息
     * 权限：系统管理员
     */
    @PostMapping("/user/ban")
    public ResponseMessage banUser(@RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer adminId = (Integer) userInfo.get("userId");

            if (!systemAdminService.isSystemAdmin(adminId)) {
                return ResponseMessage.error("无系统管理员权限");
            }

            Integer userId = (Integer) request.get("userId");
            String reason = (String) request.get("reason");
            Integer duration = (Integer) request.get("duration");

            if (userId == null || reason == null || duration == null) {
                return ResponseMessage.error("参数错误");
            }

            boolean success = systemAdminService.banUser(userId, adminId, reason, duration);

            if (success) {
                return ResponseMessage.success("用户已封禁");
            }
            return ResponseMessage.error("封禁失败");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 解封用户
     * 前端请求方式：POST
     * 请求URL：localhost:8081/admin/user/unban/{userId}
     * 路径参数：userId - 被解封用户ID
     * 返回值：成功信息
     * 权限：系统管理员
     */
    @PostMapping("/user/unban/{userId}")
    public ResponseMessage unbanUser(@PathVariable @NotNull Integer userId) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer adminId = (Integer) userInfo.get("userId");

            if (!systemAdminService.isSystemAdmin(adminId)) {
                return ResponseMessage.error("无系统管理员权限");
            }

            boolean success = systemAdminService.unbanUser(userId, adminId);

            if (success) {
                return ResponseMessage.success("用户已解封");
            }
            return ResponseMessage.error("解封失败");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 获取所有系统管理员
     * 前端请求方式：GET
     * 请求URL：localhost:8081/admin/list
     * 返回值：系统管理员列表
     * 权限：系统管理员
     */
    @GetMapping("/list")
    public ResponseMessage<List<SystemAdmin>> getAllAdmins() {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer adminId = (Integer) userInfo.get("userId");

            if (!systemAdminService.isSystemAdmin(adminId)) {
                return ResponseMessage.error("无系统管理员权限");
            }

            List<SystemAdmin> admins = systemAdminService.getAllSystemAdmins();
            return ResponseMessage.success(admins);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 添加系统管理员（超级管理员功能）
     * 前端请求方式：POST
     * 请求URL：localhost:8081/admin/add
     * 请求参数（JSON格式）：
     * {
     *   "userId": 2,              // 被任命用户ID（必填）
     *   "permissions": "[...]"    // 权限JSON（必填）
     * }
     * 返回值：成功信息
     * 权限：超级管理员
     */
    @PostMapping("/add")
    public ResponseMessage addSystemAdmin(@RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer operatorId = (Integer) userInfo.get("userId");

            Integer userId = (Integer) request.get("userId");
            String permissions = (String) request.get("permissions");

            if (userId == null || permissions == null) {
                return ResponseMessage.error("参数错误");
            }

            boolean success = systemAdminService.addSystemAdmin(
                    userId, permissions, operatorId
            );

            if (success) {
                return ResponseMessage.success("系统管理员添加成功");
            }
            return ResponseMessage.error("添加失败");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
}