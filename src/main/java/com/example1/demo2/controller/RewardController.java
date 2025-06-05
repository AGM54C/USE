package com.example1.demo2.controller;

import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.IRewardService;
import com.example1.demo2.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 奖励系统控制器
 * 提供查询用户奖励信息的接口
 */
@RestController
@RequestMapping("/reward")
public class RewardController {

    @Autowired
    private IRewardService rewardService;

    /**
     * 获取当前用户的奖励信息
     * 前端请求方式：GET
     * 请求URL：localhost:8081/reward/info
     * 返回值：包含fuelValue和knowledgeDust的对象
     */
    @GetMapping("/info")
    public ResponseMessage<Map<String, Integer>> getRewardInfo() {
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer userId = (Integer) userInfo.get("userId");

        try {
            Map<String, Integer> rewardInfo = rewardService.getUserRewardInfo(userId);
            return ResponseMessage.success(rewardInfo);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
}