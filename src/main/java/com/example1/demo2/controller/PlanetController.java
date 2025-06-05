package com.example1.demo2.controller;

import com.example1.demo2.pojo.KnowledgePlanet;
import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.service.IPlanetService;
import com.example1.demo2.service.IRewardService;
import com.example1.demo2.service.IUserService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@RestController //接口返回对象，转化为json文本
@RequestMapping("/planet")  //localhost:8081/planet/**
@Validated
public class PlanetController {
    @Autowired
    private IPlanetService planetService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRewardService rewardService;

    private static final Logger logger = LoggerFactory.getLogger(RewardService.class);

    /**
     * 创建知识星球
     * 前端请求方式：POST
     * 请求URL：localhost:8081/planet/create
     * 请求参数（JSON格式）：
     * {
     *   "contentTitle": String,           // 星球标题（必填）
     *   "userId": Integer,        // 用户id
     *   "themeId": Integer,     // 类型(0-学习，1-游戏，2-生活)
     *   "description": String      // 星球描述
     *   "modelType":Integer       //文章类型(选填）
     * }
     * 返回值：成功返回星球id，失败返回错误信息
     */
    @PostMapping("/create")
    @Transactional
    public ResponseMessage<String> create(@Valid @RequestBody KnowledgePlanetDto planet) {
        if(planet.getContentTitle() == null || planet.getThemeId() == null
                || planet.getUserId() == null || planet.getDescription() == null) {
            return ResponseMessage.error("缺少必要参数！");
        }

        // 查询星球
        KnowledgePlanet p = planetService.findByTitle(planet.getContentTitle());
        if(p != null) {
            return ResponseMessage.error("星球名已被占用，请重新输入");
        } else {
            // 创建星球
            planetService.create(planet);

            // 奖励用户燃料值
            try {
                Integer newFuelValue = rewardService.rewardForPlanetCreation(planet.getUserId());
                // 可以在响应中返回新的燃料值
                Map<String, Object> result = new HashMap<>();
                result.put("planetId", planet.getPlanetId());
                result.put("newFuelValue", newFuelValue);
                return ResponseMessage.success(result);
            } catch (Exception e) {
                // 如果奖励失败，仍然返回成功（星球已创建），但记录错误
                logger.error("奖励燃料值失败: {}", e.getMessage());
                return ResponseMessage.success(ConvertUtil.convertKnowledgePlanetToDto(p));
            }
        }
    }

    /**
     * 发布知识星球
     * 前端请求方式：POST
     * 请求URL：localhost:8081/planet/publish
     * 请求参数（JSON格式）：
     * {
     *    "planetId":String      //星球Id（必填）
     * }
     * 返回值：成功返回星球id，失败返回错误信息
     */
    @PostMapping("/publish")
    public ResponseMessage<String> publish(@Valid @RequestBody KnowledgePlanetDto planet) {
        //查询星球
        KnowledgePlanet p = planetService.findByPlanetId(planet.getPlanetId());
        if(p==null) {
            //星球不存在
            return ResponseMessage.error("星球不存在或已被删除");
        }
        else{
            planetService.publish(planet);
            return ResponseMessage.success(planet.getPlanetId());
        }
    }


    /**
     * 访问星球（带可见性校验和访问量统计）
     * 前端请求方式：GET
     * 请求URL：localhost:8081/planet/visit/{}
     * 请求参数（P格式）：
     *      * {
     *      *   "planetId": String         // 星球ID（必填）
     * }
     * 返回值：成功返回星球信息，失败返回错误信息
     */
    @GetMapping("/visit/{planetId}")
    public ResponseMessage<KnowledgePlanetDto> visitPlanet(
            @PathVariable String planetId
    ) {
        // 获取当前用户ID
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId= (Integer) map.get("userId");
        User u=userService.findById(userId);
        if(u==null) {
            return ResponseMessage.error("用户不存在或登录状态已过期");
        }
        try {
            KnowledgePlanet planet = planetService.visitPlanet(planetId, userId);
            KnowledgePlanetDto dto = ConvertUtil.convertKnowledgePlanetToDto(planet);
            return ResponseMessage.success(dto);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
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
        if(dto.getCoverUrl()!=null) {
            if (Objects.equals(planet.getCoverUrl(), dto.getCoverUrl())) {
                return ResponseMessage.success("封面URL未变更");
            }
        }
        planetService.updateCoverUrl(dto.getPlanetId(), dto.getCoverUrl());
        return ResponseMessage.success("星球封面URL更新成功");
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
     * 更新星球内容
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
        if(dto.getContentTitle()!=null) {
            if (planet.getContentDetail().equals(dto.getContentDetail())) {
                return ResponseMessage.success("内容未变更");
            }
        }
        planetService.updatedetail(dto.getPlanetId(), dto.getContentDetail());
        return ResponseMessage.success("星球内容更新成功");
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
    @PutMapping("/updatevisibility")
    public ResponseMessage updatevisibility(@Valid @RequestBody KnowledgePlanetDto dto) {
        KnowledgePlanet planet = planetService.findByPlanetId(dto.getPlanetId());
        if (planet == null) {
            return ResponseMessage.error("星球不存在");
        }
        if(dto.getVisibility()!=null) {
            if (planet.getVisibility().equals(dto.getVisibility())) {
                return ResponseMessage.success("内容未变更");
            }
        }
        planetService.updatevisibility(dto.getPlanetId(), dto.getVisibility());
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
     *   "fuelValue": Integer          // 燃料值（必填）
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
     * 更新星球标题
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/planet/updateTitle
     * 请求参数（JSON格式）：
     * {
     *   "planetId": String,        // 星球ID（必填）
     *   "colorScheme": String        // 颜色方案（必填）
     * }
     //     * 返回值：成功或失败信息
     */
    @PutMapping("/updatecolorscheme")
    public ResponseMessage updatecolorscheme(@Valid @RequestBody KnowledgePlanetDto dto) {
        KnowledgePlanet planet = planetService.findByPlanetId(dto.getPlanetId());
        if (planet == null) {
            return ResponseMessage.error("星球不存在");
        }
        if(dto.getColorScheme()!=null) {
            if (planet.getColorScheme().equals(dto.getColorScheme())) {
                return ResponseMessage.success("颜色方案未变更");
            }
        }
        planetService.updatefuelvalue(dto.getPlanetId(), dto.getFuelValue());
        return ResponseMessage.success("颜色方案更新成功");
    }

    /**
     * 更新星球标题
     * 前端请求方式：PUT
     * 请求URL：localhost:8081/planet/updateTitle
     * 请求参数（JSON格式）：
     * {
     *   "planetId": String,        // 星球ID（必填）
     *   "modelType": Integer          // 展示模型（必填）
     * }
     //     * 返回值：成功或失败信息
     */
    @PutMapping("/updatemodeltype")
    public ResponseMessage updatemodeltype(@Valid @RequestBody KnowledgePlanetDto dto) {
        KnowledgePlanet planet = planetService.findByPlanetId(dto.getPlanetId());
        if (planet == null) {
            return ResponseMessage.error("星球不存在");
        }
        if (planet.getModelType().equals(dto.getModelType())) {
            return ResponseMessage.success("展示模型未变更");
        }
        planetService.updatefuelvalue(dto.getPlanetId(), dto.getFuelValue());
        return ResponseMessage.success("展示模型更新成功");
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
