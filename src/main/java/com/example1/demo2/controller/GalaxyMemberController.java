package com.example1.demo2.controller;

import com.example1.demo2.pojo.GalaxyMember;
import com.example1.demo2.pojo.KnowledgeGalaxy;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.pojo.dto.GalaxyMemberDto;
import com.example1.demo2.service.GalaxyMemberService;
import com.example1.demo2.service.KnowledgeGalaxyService;
import com.example1.demo2.util.ThreadLocalUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/galaxy/member")
@Validated
public class GalaxyMemberController {

    @Autowired
    private GalaxyMemberService galaxyMemberService;

    @Autowired
    private KnowledgeGalaxyService knowledgeGalaxyService;

    // 获取星系成员列表
    @GetMapping("/list/{galaxyId}")
    public ResponseMessage<List<GalaxyMemberDto>> getMemberList(
            @PathVariable Integer galaxyId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {

        // 验证星系是否存在
        KnowledgeGalaxy galaxy = knowledgeGalaxyService.findById(galaxyId);
        if (galaxy == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 验证当前用户是否有权限查看成员列表
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        // 私有星系只有成员可以查看成员列表
        if (galaxy.getPermissionType() == 1 && !knowledgeGalaxyService.isMember(galaxyId, userId)) {
            return ResponseMessage.error("无权限查看该私有星系的成员列表");
        }

        List<GalaxyMemberDto> members = galaxyMemberService.getMemberList(galaxyId, page, pageSize);
        return ResponseMessage.success(members);
    }

    // 获取成员详细信息
    @GetMapping("/info/{memberId}")
    public ResponseMessage<GalaxyMemberDto> getMemberInfo(@PathVariable Integer memberId) {
        GalaxyMemberDto member = galaxyMemberService.getMemberInfo(memberId);
        if (member == null) {
            return ResponseMessage.error("成员信息不存在");
        }

        // 验证查看权限
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        KnowledgeGalaxy galaxy = knowledgeGalaxyService.findById(member.getGalaxyId());
        if (galaxy.getPermissionType() == 1 && !knowledgeGalaxyService.isMember(member.getGalaxyId(), userId)) {
            return ResponseMessage.error("无权限查看该成员信息");
        }

        return ResponseMessage.success(member);
    }

    // 更新成员角色
    @PutMapping("/role/update")
    public ResponseMessage<String> updateMemberRole(
            @RequestParam Integer galaxyId,
            @RequestParam Integer targetUserId,
            @RequestParam Integer newRoleType) {

        // 验证参数
        if (newRoleType < 0 || newRoleType > 2) {
            return ResponseMessage.error("无效的角色类型");
        }

        // 获取当前用户信息
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        // 验证星系存在
        KnowledgeGalaxy galaxy = knowledgeGalaxyService.findById(galaxyId);
        if (galaxy == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 验证操作权限：只有创建者和管理员可以修改角色
        GalaxyMember currentMember = galaxyMemberService.getMember(galaxyId, userId);
        if (currentMember == null) {
            return ResponseMessage.error("您不是该星系成员");
        }

        // 创建者权限最高
        boolean isCreator = galaxy.getCreator().equals(userId);
        boolean isAdmin = currentMember.getRoleType() == 1;

        if (!isCreator && !isAdmin) {
            return ResponseMessage.error("只有星系创建者和管理员可以修改成员角色");
        }

        // 不能修改创建者的角色
        if (galaxy.getCreator().equals(targetUserId)) {
            return ResponseMessage.error("不能修改创建者的角色");
        }

        // 管理员不能任命其他管理员，只有创建者可以
        if (newRoleType == 1 && !isCreator) {
            return ResponseMessage.error("只有创建者可以任命管理员");
        }

        // 不能将创建者角色赋予其他人
        if (newRoleType == 2) {
            return ResponseMessage.error("创建者角色不可转让");
        }

        galaxyMemberService.updateMemberRole(galaxyId, targetUserId, newRoleType);

        String roleName = newRoleType == 0 ? "普通成员" : "管理员";
        return ResponseMessage.success("成员角色已更新为：" + roleName);
    }

    // 更新成员权限
    @PutMapping("/permissions/update")
    public ResponseMessage<String> updateMemberPermissions(
            @RequestParam Integer galaxyId,
            @RequestParam Integer targetUserId,
            @RequestParam String permissions) {

        // 获取当前用户信息
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        // 验证星系和权限
        KnowledgeGalaxy galaxy = knowledgeGalaxyService.findById(galaxyId);
        if (galaxy == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 只有创建者可以修改权限配置
        if (!galaxy.getCreator().equals(userId)) {
            return ResponseMessage.error("只有创建者可以修改成员权限");
        }

        // 验证权限格式
        if (!permissions.matches("^\\[.*\\]$")) {
            return ResponseMessage.error("权限格式错误，必须是JSON数组格式");
        }

        galaxyMemberService.updateMemberPermissions(galaxyId, targetUserId, permissions);
        return ResponseMessage.success("成员权限已更新");
    }

    // 禁用/启用成员
    @PutMapping("/status/update")
    public ResponseMessage<String> updateMemberStatus(
            @RequestParam Integer galaxyId,
            @RequestParam Integer targetUserId,
            @RequestParam Integer status) {

        // 验证状态值
        if (status != 0 && status != 1) {
            return ResponseMessage.error("无效的状态值");
        }

        // 获取当前用户信息
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        // 验证权限
        KnowledgeGalaxy galaxy = knowledgeGalaxyService.findById(galaxyId);
        if (galaxy == null) {
            return ResponseMessage.error("星系不存在");
        }

        GalaxyMember currentMember = galaxyMemberService.getMember(galaxyId, userId);
        boolean isCreator = galaxy.getCreator().equals(userId);
        boolean isAdmin = currentMember != null && currentMember.getRoleType() == 1;

        if (!isCreator && !isAdmin) {
            return ResponseMessage.error("只有创建者和管理员可以管理成员状态");
        }

        // 不能禁用创建者
        if (galaxy.getCreator().equals(targetUserId)) {
            return ResponseMessage.error("不能禁用创建者");
        }

        // 管理员不能禁用其他管理员
        GalaxyMember targetMember = galaxyMemberService.getMember(galaxyId, targetUserId);
        if (targetMember.getRoleType() == 1 && !isCreator) {
            return ResponseMessage.error("只有创建者可以禁用管理员");
        }

        galaxyMemberService.updateMemberStatus(galaxyId, targetUserId, status);

        String statusText = status == 0 ? "启用" : "禁用";
        return ResponseMessage.success("成员已" + statusText);
    }

    // 批量邀请成员
    @PostMapping("/invite/batch")
    public ResponseMessage<String> batchInvite(
            @RequestParam Integer galaxyId,
            @RequestBody List<Integer> userIds) {

        if (userIds == null || userIds.isEmpty()) {
            return ResponseMessage.error("请选择要邀请的用户");
        }

        // 获取当前用户信息
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        // 验证权限
        KnowledgeGalaxy galaxy = knowledgeGalaxyService.findById(galaxyId);
        if (galaxy == null) {
            return ResponseMessage.error("星系不存在");
        }

        GalaxyMember currentMember = galaxyMemberService.getMember(galaxyId, userId);
        if (currentMember == null ||
                (currentMember.getRoleType() == 0 && !galaxy.getCreator().equals(userId))) {
            return ResponseMessage.error("只有创建者和管理员可以邀请新成员");
        }

        // 执行批量邀请
        int successCount = galaxyMemberService.batchInvite(galaxyId, userIds);
        return ResponseMessage.success("成功邀请 " + successCount + " 名新成员");
    }

    // 移除成员
    @DeleteMapping("/remove")
    public ResponseMessage<String> removeMember(
            @RequestParam Integer galaxyId,
            @RequestParam Integer targetUserId) {

        // 获取当前用户信息
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        // 验证权限
        KnowledgeGalaxy galaxy = knowledgeGalaxyService.findById(galaxyId);
        if (galaxy == null) {
            return ResponseMessage.error("星系不存在");
        }

        // 不能移除创建者
        if (galaxy.getCreator().equals(targetUserId)) {
            return ResponseMessage.error("不能移除星系创建者");
        }

        GalaxyMember currentMember = galaxyMemberService.getMember(galaxyId, userId);
        boolean isCreator = galaxy.getCreator().equals(userId);
        boolean isAdmin = currentMember != null && currentMember.getRoleType() == 1;

        if (!isCreator && !isAdmin) {
            return ResponseMessage.error("只有创建者和管理员可以移除成员");
        }

        // 管理员不能移除其他管理员
        GalaxyMember targetMember = galaxyMemberService.getMember(galaxyId, targetUserId);
        if (targetMember.getRoleType() == 1 && !isCreator) {
            return ResponseMessage.error("只有创建者可以移除管理员");
        }

        galaxyMemberService.removeMember(galaxyId, targetUserId);
        return ResponseMessage.success("成员已移除");
    }

    // 获取成员在所有星系中的角色统计
    @GetMapping("/stats/{userId}")
    public ResponseMessage<Map<String, Object>> getMemberStats(@PathVariable Integer userId) {
        Map<String, Object> stats = galaxyMemberService.getMemberStats(userId);
        return ResponseMessage.success(stats);
    }
}