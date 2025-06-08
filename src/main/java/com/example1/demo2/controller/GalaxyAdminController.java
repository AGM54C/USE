package com.example1.demo2.controller;

import com.example1.demo2.pojo.GalaxyAdministrator;
import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.dto.KnowledgeGalaxyDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.IGalaxyAdminService;
import com.example1.demo2.service.IGalaxyService;
import com.example1.demo2.util.ConvertUtil;
import com.example1.demo2.util.ThreadLocalUtil;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 星系管理员控制器
 * 处理星系管理员的任命、撤销等功能
 */
@RestController
@RequestMapping("/galaxy/admin")
@Validated
public class GalaxyAdminController {

    @Autowired
    private IGalaxyAdminService galaxyAdminService;

    @Autowired
    private IGalaxyService galaxyService;

    /**
     * 任命星系管理员
     * 前端请求方式：POST
     * 请求URL：localhost:8081/galaxy/admin/appoint
     * 请求参数（JSON格式）：
     * {
     * "galaxyId": 1,    // 星系ID（必填）
     * "userId": 2       // 被任命用户ID（必填）
     * }
     * 返回值：成功信息
     * 权限：只有星系创建者可以任命管理员
     */
    @PostMapping("/appoint")
    public ResponseMessage appointAdmin(@RequestBody Map<String, Integer> request) {
        try {
            Integer galaxyId = request.get("galaxyId");
            Integer appointUserId = request.get("userId");

            if (galaxyId == null || appointUserId == null) {
                return ResponseMessage.error("参数错误");
            }

            // 获取当前用户（必须是星系创建者）
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer currentUserId = (Integer) userInfo.get("userId");

            boolean success = galaxyAdminService.addGalaxyAdmin(
                    galaxyId, appointUserId, currentUserId
            );

            if (success) {
                return ResponseMessage.success("管理员任命成功");
            }
            return ResponseMessage.error("任命失败");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 撤销星系管理员
     * 前端请求方式：DELETE
     * 请求URL：localhost:8081/galaxy/admin/revoke
     * 请求参数（JSON格式）：
     * {
     * "galaxyId": 1,    // 星系ID（必填）
     * "userId": 2       // 被撤销用户ID（必填）
     * }
     * 返回值：成功信息
     * 权限：只有星系创建者可以撤销管理员
     */
    @DeleteMapping("/revoke")
    public ResponseMessage revokeAdmin(@RequestBody Map<String, Integer> request) {
        try {
            Integer galaxyId = request.get("galaxyId");
            Integer revokeUserId = request.get("userId");

            if (galaxyId == null || revokeUserId == null) {
                return ResponseMessage.error("参数错误");
            }

            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer currentUserId = (Integer) userInfo.get("userId");

            boolean success = galaxyAdminService.revokeGalaxyAdmin(
                    galaxyId, revokeUserId, currentUserId
            );

            if (success) {
                return ResponseMessage.success("管理员撤销成功");
            }
            return ResponseMessage.error("撤销失败");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 获取星系管理员列表
     * 前端请求方式：GET
     * 请求URL：localhost:8081/galaxy/admin/list/{galaxyId}
     * 路径参数：galaxyId - 星系ID
     * 返回值：管理员列表
     */
    @GetMapping("/list/{galaxyId}")
    public ResponseMessage<List<GalaxyAdministrator>> getAdminList(
            @PathVariable @NotNull Integer galaxyId) {
        try {
            List<GalaxyAdministrator> admins = galaxyAdminService.getGalaxyAdmins(galaxyId);
            return ResponseMessage.success(admins);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 获取用户管理的星系列表
     * 前端请求方式：GET
     * 请求URL：localhost:8081/galaxy/admin/managed
     * 返回值：用户管理的星系列表
     */
    @GetMapping("/managed")
    public ResponseMessage<List<Integer>> getManagedGalaxies() {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");

            List<KnowledgeGalaxy> galaxies = galaxyAdminService.getUserManagedGalaxies(userId);
            return ResponseMessage.success(galaxies);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 删除违规星系评论接口
     * 前端请求方式：DELETE
     * 请求URL：localhost:8081/galaxy/admin/deleteComment/{commentId}
     * 返回值：成功返回成功信息，失败返回错误信息
     */
    @DeleteMapping("/deleteComment/{commentId}")
    public ResponseMessage deleteComment(@PathVariable @NotNull Integer commentId) {
        try {
            Map<String, Object> userInfo = ThreadLocalUtil.get();
            Integer userId = (Integer) userInfo.get("userId");

            // 验证用户是否为星系管理员
            if (!galaxyAdminService.isGalaxyAdmin(commentId, userId)) {
                return ResponseMessage.error("无权限删除评论");
            }

            galaxyAdminService.deleteComment(commentId);
            return ResponseMessage.success("评论删除成功");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 填写邀请码自动成为星系管理员
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/galaxy/admin/autoBecomeAdmin
     * 请求参数（JSON格式）：
     * {
     * "inviteCode": "ABC123" // 邀请码（必填）
     * "userId": 1 // 用户ID（必填）
     * * }
     * * 返回值：成功信息或错误信息
     */
    @PutMapping("/autoBecomeAdmin")
    public ResponseMessage autoBecomeAdmin(@RequestBody Map<String, Object> request) {
        try {
            String inviteCode = (String) request.get("inviteCode");
            Integer userId = (Integer) request.get("userId");

            if (userId == null ||inviteCode==null|| inviteCode.isEmpty()) {
                return ResponseMessage.error("参数错误");
            }

            boolean success = galaxyAdminService.autoBecomeAdmin(inviteCode, userId);
            if (success) {
                return ResponseMessage.success("自动成为管理员成功");
            }
            return ResponseMessage.error("自动成为管理员失败，可能是邀请码错误或已失效");
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
        }
    }
