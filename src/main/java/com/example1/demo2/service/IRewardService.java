package com.example1.demo2.service;

/**
 * 奖励系统服务接口
 * 管理用户的燃料值(fuelValue)和知识星云值(knowledgeDust)
 * 根据不同的用户行为进行奖励或扣除
 */
public interface IRewardService {

    /**
     * 创建星球时的奖励
     * 增加用户燃料值 +3
     * @param userId 用户ID
     * @return 更新后的燃料值
     */
    Integer rewardForPlanetCreation(Integer userId);

    /**
     * 访问星球时的消耗
     * 扣除用户燃料值 -1
     * @param userId 用户ID
     * @return 更新后的燃料值
     * @throws RuntimeException 如果燃料值不足
     */
    Integer consumeForPlanetAccess(Integer userId);

    /**
     * 发表评论时的奖励
     * 增加用户知识星云值 +3
     * @param userId 用户ID
     * @param isGalaxy true-星系评论, false-星球评论
     * @return 更新后的知识星云值
     */
    Integer rewardForComment(Integer userId, boolean isGalaxy);

    /**
     * 创建星系时的消耗
     * 扣除用户知识星云值 -1
     * @param userId 用户ID
     * @return 更新后的知识星云值
     * @throws RuntimeException 如果知识星云值不足
     */
    Integer consumeForGalaxyCreation(Integer userId);

    /**
     * 检查用户是否有足够的燃料值
     * @param userId 用户ID
     * @param requiredAmount 需要的燃料值
     * @return 是否有足够的燃料值
     */
    boolean hasSufficientFuel(Integer userId, Integer requiredAmount);

    /**
     * 检查用户是否有足够的知识星云值
     * @param userId 用户ID
     * @param requiredAmount 需要的知识星云值
     * @return 是否有足够的知识星云值
     */
    boolean hasSufficientKnowledgeDust(Integer userId, Integer requiredAmount);

    /**
     * 获取用户当前的奖励信息
     * @param userId 用户ID
     * @return 包含fuelValue和knowledgeDust的Map
     */
    java.util.Map<String, Integer> getUserRewardInfo(Integer userId);
}