package com.abin.mallchat.common.common.service.frequencycontrol;

import com.abin.mallchat.common.common.domain.dto.FrequencyControlDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流策略工厂
 *
 * @author linzhihan
 * @date 2023/07/03
 */
public class FrequencyControlStrategyFactory {
    /**
     * 指定时间内总次数限流
     */
    public static final String TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER = "TotalCountWithInFixTime";
    /**
     * 限流策略集合
     */
    static Map<String, AbstractFrequencyControlService<?>> frequencyControlServiceStrategyMap = new ConcurrentHashMap<>(8);

    /**
     * 将策略类放入工厂
     *
     * @param strategyName                    策略名称
     * @param abstractFrequencyControlService 策略类
     */
    public static <K extends FrequencyControlDTO> void registerFrequencyController(String strategyName, AbstractFrequencyControlService<K> abstractFrequencyControlService) {
        frequencyControlServiceStrategyMap.put(strategyName, abstractFrequencyControlService);
    }

    /**
     * 根据名称获取策略类
     *
     * @param strategyName 策略名称
     * @return 对应的限流策略类
     */
    @SuppressWarnings("unchecked")
    public static <K extends FrequencyControlDTO> AbstractFrequencyControlService<K> getFrequencyControllerByName(String strategyName) {
        return (AbstractFrequencyControlService<K>) frequencyControlServiceStrategyMap.get(strategyName);
    }

    /**
     * 构造器私有
     */
    private FrequencyControlStrategyFactory() {

    }
}
