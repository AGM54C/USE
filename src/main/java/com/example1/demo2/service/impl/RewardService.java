package com.example1.demo2.service.impl;

import com.example1.demo2.mapper.UserMapper;
import com.example1.demo2.pojo.User;
import com.example1.demo2.service.IRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 奖励系统服务实现类
 * 处理用户燃料值和知识星云值的增减逻辑
 */
@Service
public class RewardService implements IRewardService {

    private static final Logger logger = LoggerFactory.getLogger(RewardService.class);

    @Autowired
    private UserMapper userMapper;

    // 奖励和消耗的常量定义，便于后期调整
    private static final int PLANET_CREATION_FUEL_REWARD = 3;
    private static final int PLANET_ACCESS_FUEL_COST = 1;
    private static final int COMMENT_KNOWLEDGE_DUST_REWARD = 3;
    private static final int GALAXY_CREATION_KNOWLEDGE_DUST_COST = 1;

    @Override
    @Transactional
    public Integer rewardForPlanetCreation(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 增加燃料值
        Integer currentFuel = user.getFuelValue() != null ? user.getFuelValue() : 0;
        Integer newFuel = currentFuel + PLANET_CREATION_FUEL_REWARD;

        // 调用mapper更新燃料值
        userMapper.updateFuelValue(userId, newFuel);

        logger.info("用户 {} 创建星球，获得 {} 燃料值，当前燃料值: {}",
                userId, PLANET_CREATION_FUEL_REWARD, newFuel);

        return newFuel;
    }

    @Override
    @Transactional
    public Integer consumeForPlanetAccess(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查燃料值是否足够
        Integer currentFuel = user.getFuelValue() != null ? user.getFuelValue() : 0;
        if (currentFuel < PLANET_ACCESS_FUEL_COST) {
            throw new RuntimeException("燃料值不足，需要 " + PLANET_ACCESS_FUEL_COST +
                    " 点燃料值，当前只有 " + currentFuel + " 点");
        }

        // 扣除燃料值
        Integer newFuel = currentFuel - PLANET_ACCESS_FUEL_COST;
        userMapper.updateFuelValue(userId, newFuel);

        logger.info("用户 {} 访问星球，消耗 {} 燃料值，剩余燃料值: {}",
                userId, PLANET_ACCESS_FUEL_COST, newFuel);

        return newFuel;
    }

    @Override
    @Transactional
    public Integer rewardForComment(Integer userId, boolean isGalaxy) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 增加知识星云值
        Integer currentDust = user.getKnowledgeDust() != null ? user.getKnowledgeDust() : 0;
        Integer newDust = currentDust + COMMENT_KNOWLEDGE_DUST_REWARD;

        userMapper.updateKnowledgeDust(userId, newDust);

        String commentType = isGalaxy ? "星系" : "星球";
        logger.info("用户 {} 在{}发表评论，获得 {} 知识星云值，当前知识星云值: {}",
                userId, commentType, COMMENT_KNOWLEDGE_DUST_REWARD, newDust);

        return newDust;
    }

    @Override
    @Transactional
    public Integer consumeForGalaxyCreation(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查知识星云值是否足够
        Integer currentDust = user.getKnowledgeDust() != null ? user.getKnowledgeDust() : 0;
        if (currentDust < GALAXY_CREATION_KNOWLEDGE_DUST_COST) {
            throw new RuntimeException("知识星云值不足，需要 " + GALAXY_CREATION_KNOWLEDGE_DUST_COST +
                    " 点知识星云值，当前只有 " + currentDust + " 点");
        }

        // 扣除知识星云值
        Integer newDust = currentDust - GALAXY_CREATION_KNOWLEDGE_DUST_COST;
        userMapper.updateKnowledgeDust(userId, newDust);

        logger.info("用户 {} 创建星系，消耗 {} 知识星云值，剩余知识星云值: {}",
                userId, GALAXY_CREATION_KNOWLEDGE_DUST_COST, newDust);

        return newDust;
    }

    @Override
    public boolean hasSufficientFuel(Integer userId, Integer requiredAmount) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }

        Integer currentFuel = user.getFuelValue() != null ? user.getFuelValue() : 0;
        return currentFuel >= requiredAmount;
    }

    @Override
    public boolean hasSufficientKnowledgeDust(Integer userId, Integer requiredAmount) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }

        Integer currentDust = user.getKnowledgeDust() != null ? user.getKnowledgeDust() : 0;
        return currentDust >= requiredAmount;
    }

    @Override
    public Map<String, Integer> getUserRewardInfo(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Map<String, Integer> rewardInfo = new HashMap<>();
        rewardInfo.put("fuelValue", user.getFuelValue() != null ? user.getFuelValue() : 0);
        rewardInfo.put("knowledgeDust", user.getKnowledgeDust() != null ? user.getKnowledgeDust() : 0);

        return rewardInfo;
    }
}