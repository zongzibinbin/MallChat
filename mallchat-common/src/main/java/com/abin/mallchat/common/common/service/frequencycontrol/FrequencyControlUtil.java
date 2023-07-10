package com.abin.mallchat.common.common.service.frequencycontrol;

import com.abin.mallchat.common.common.domain.dto.FrequencyControlDTO;
import com.abin.mallchat.common.common.utils.AssertUtil;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

/**
 * 限流工具类 提供编程式的限流调用方法
 *
 * @author linzhihan
 * @date 2023/07/03
 */
public class FrequencyControlUtil {

    /**
     * 单限流策略的调用方法-编程式调用
     *
     * @param strategyName     策略名称
     * @param frequencyControl 单个频控对象
     * @param supplier         服务提供着
     * @return 业务方法执行结果
     * @throws Throwable
     */
    public static <T, K extends FrequencyControlDTO> T executeWithFrequencyControl(String strategyName, K frequencyControl, AbstractFrequencyControlService.SupplierThrowWithoutParam<T> supplier) throws Throwable {
        AbstractFrequencyControlService<K> frequencyController = FrequencyControlStrategyFactory.getFrequencyControllerByName(strategyName);
        return frequencyController.executeWithFrequencyControl(frequencyControl, supplier);
    }

    public static <K extends FrequencyControlDTO> void executeWithFrequencyControl(String strategyName, K frequencyControl, AbstractFrequencyControlService.Executor executor) throws Throwable {
        AbstractFrequencyControlService<K> frequencyController = FrequencyControlStrategyFactory.getFrequencyControllerByName(strategyName);
        frequencyController.executeWithFrequencyControl(frequencyControl, () -> {
            executor.execute();
            return null;
        });
    }


    /**
     * 多限流策略的编程式调用方法调用方法
     *
     * @param strategyName         策略名称
     * @param frequencyControlList 频控列表 包含每一个频率控制的定义以及顺序
     * @param supplier             函数式入参-代表每个频控方法执行的不同的业务逻辑
     * @return 业务方法执行的返回值
     * @throws Throwable 被限流或者限流策略定义错误
     */
    public static <T, K extends FrequencyControlDTO> T executeWithFrequencyControlList(String strategyName, List<K> frequencyControlList, AbstractFrequencyControlService.SupplierThrowWithoutParam<T> supplier) throws Throwable {
        boolean existsFrequencyControlHasNullKey = frequencyControlList.stream().anyMatch(frequencyControl -> ObjectUtils.isEmpty(frequencyControl.getKey()));
        AssertUtil.isFalse(existsFrequencyControlHasNullKey, "限流策略的Key字段不允许出现空值");
        AbstractFrequencyControlService<K> frequencyController = FrequencyControlStrategyFactory.getFrequencyControllerByName(strategyName);
        return frequencyController.executeWithFrequencyControlList(frequencyControlList, supplier);
    }

    /**
     * 构造器私有
     */
    private FrequencyControlUtil() {

    }

}
