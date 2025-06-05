package com.example1.demo2.controller;


import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import com.example1.demo2.pojo.dto.UserDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.IPlanetAccessService;
import com.example1.demo2.service.IRewardService;
import com.example1.demo2.service.impl.RewardService;
import com.example1.demo2.util.ConvertUtil;
import com.example1.demo2.util.ThreadLocalUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planet/access")  //localhost:8081/planet/access/**
@Validated
public class PlanetAccessController {
    @Autowired
    private IPlanetAccessService planetAccessService;

    @Autowired
    private IRewardService rewardService;

    private static final Logger logger = LoggerFactory.getLogger(RewardService.class);
    /**
     * 定向飞行到指定星球
     * 前端请求方式：GET
     * 请求URL：localhost:8081/planet/access/search?planetName=星球名(模糊查询)
     * 返回值：成功返回星球列表，失败返回错误信息
     */
    @GetMapping("/search")
    @Transactional
    public ResponseMessage<List<KnowledgePlanetDto>> search(@RequestParam("planetName") String planetName) {
        // 获取当前用户ID
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer userId = (Integer) userInfo.get("userId");

        // 检查用户燃料值是否足够
        if (!rewardService.hasSufficientFuel(userId, 1)) {
            return ResponseMessage.error("燃料值不足，无法搜索星球");
        }

        // 查询星球
        List<KnowledgePlanet> planets = planetAccessService.findByTitle(planetName);
        if(planets.isEmpty()) {
            return ResponseMessage.error("没有找到相关星球");
        }

        // 扣除燃料值（只有找到星球才扣除）
        try {
            rewardService.consumeForPlanetAccess(userId);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }

        // 转化为dto列表
        List<KnowledgePlanetDto> dtoList = ConvertUtil.convertKnowledgePlanetListToDtoList(planets);
        return ResponseMessage.success(dtoList);
    }
    /**
     * 随机访问星球
     * 前端请求方式：GET
     * 请求URL：localhost:8081/planet/access/randomAccess
     * 返回值：成功返回星球信息，失败返回错误信息
     */
    @GetMapping("/randomAccess")
    @Transactional
    public ResponseMessage<KnowledgePlanetDto> randomAccess() {
        // 获取当前用户ID
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer userId = (Integer) userInfo.get("userId");

        // 检查用户燃料值是否足够
        if (!rewardService.hasSufficientFuel(userId, 1)) {
            return ResponseMessage.error("燃料值不足，无法访问星球");
        }

        // 扣除燃料值
        try {
            rewardService.consumeForPlanetAccess(userId);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }

        // 随机访问星球
        KnowledgePlanet p = planetAccessService.findRandomPlanet();
        if(p == null) {
            return ResponseMessage.error("没有可访问的星球");
        }
        if(p.getVisibility() != 1) {
            return ResponseMessage.error("星球不可见");
        }

        // 转化为dto
        KnowledgePlanetDto dto = ConvertUtil.convertKnowledgePlanetToDto(p);
        return ResponseMessage.success(dto);
    }

    /**
     * 展示访问量前10且visibility为1的星球
     * 前端请求方式：GET
     * 请求URL：localhost:8081/planet/access/loadinghotplanets
     * 返回值：成功返回星球列表，失败返回错误信息
     */

    @GetMapping("/loadinghotplanets")
    public ResponseMessage<List<KnowledgePlanetDto>> loadinghotplanets() {
        //获取访问量前10的星球
        List<KnowledgePlanet> planets = planetAccessService.getTop10Planets();
        if(planets.isEmpty()) {
            return ResponseMessage.error("没有可访问的星球");
        }
        //转化为dto列表
        List<KnowledgePlanetDto> dtoList = ConvertUtil.convertKnowledgePlanetListToDtoList(planets);
        return ResponseMessage.success(dtoList);
    }

}
