package com.crypt.cryptguard.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @FileName CryptStrategyFactory
 * @Description
 * @Author yaoHui
 * @date 2025-01-09
 **/
public class CryptStrategyFactory {

    private static final Map<String, CryptStrategy> STRATEGIES = new HashMap<>();
    private static final String DEFAULT_STRATEGY = "default";

    // 静态块注册默认策略
    static {
        STRATEGIES.put(DEFAULT_STRATEGY, new DefaultCryptStrategy());
    }

    /**
     * 获取加密策略
     * @param name 策略名称
     * @return 加密策略
     */
    public static CryptStrategy getStrategy(String name) {
        return STRATEGIES.getOrDefault(name, STRATEGIES.get(DEFAULT_STRATEGY));
    }

    /**
     * 获取加密策略
     * @return 加密策略
     */
    public static CryptStrategy getStrategy() {
        return STRATEGIES.get(DEFAULT_STRATEGY);
    }

    /**
     * 注册自定义加密策略
     * @param name 策略名称
     * @param strategy 策略实例
     */
    public static void registerStrategy(String name, CryptStrategy strategy) {
        STRATEGIES.put(name, strategy);
    }
}
