package com.abin.mallchat.common.common.service.frequecycontrol;

import com.abin.mallchat.common.common.domain.dto.FrequencyControlDTO;
import com.abin.mallchat.common.common.exception.BusinessException;
import com.abin.mallchat.common.common.exception.CommonErrorEnum;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/** 抽象类频控服务 其他类如果要实现限流服务 直接定义一个业务类继承这个类 然后通过那个业务类进行限流调用
 * @author linzhihan
 * @date 2023/07/03
 *
 */
@Slf4j
public abstract class AbstractFrequencyControlService {

    /**
     * @param frequencyControlMap 定义的注解频控 Map中的Key-对应redis的单个频控的Key Map中的Value-对应redis的单个频控的Key限制的Value
     * @param supplier            函数式入参-代表每个频控方法执行的不同的业务逻辑
     * @return 业务方法执行的返回值
     * @throws Throwable
     */
    private <T> T executeWithFrequencyControlMap(Map<String, FrequencyControlDTO> frequencyControlMap, SupplierThrow<T> supplier) throws Throwable {
        //批量获取redis统计的值
        List<String> frequencyKeys = new ArrayList<>(frequencyControlMap.keySet());
        List<Integer> countList = RedisUtils.mget(frequencyKeys, Integer.class);
        for (int i = 0; i < frequencyKeys.size(); i++) {
            String key = frequencyKeys.get(i);
            Integer count = countList.get(i);
            int frequencyControlCount = frequencyControlMap.get(key).getCount();
            if (Objects.nonNull(count) && count >= frequencyControlCount) {
                //频率超过了
                log.warn("frequencyControl limit key:{},count:{}", key, count);
                return errorHandleWhenLimited();
            }
        }
        try {
            return supplier.get();
        } finally {
            //不管成功还是失败，都增加次数
            frequencyControlMap.forEach((k, v) -> RedisUtils.inc(k, v.getTime(), v.getUnit()));
        }
    }

    /**
     * 多限流策略的编程式调用方法
     *
     * @param frequencyControlList 频控列表 包含每一个频率控制的定义以及顺序
     * @param supplier             函数式入参-代表每个频控方法执行的不同的业务逻辑
     * @return 业务方法执行的返回值
     * @throws Throwable 被限流或者限流策略定义错误
     */
    public <T> T executeWithFrequencyControlList(List<FrequencyControlDTO> frequencyControlList, SupplierThrow<T> supplier) throws Throwable {
        //如果为false 用于注解调用 外面已经构建好了Key的情况
        if (generateRedisKey()) {
            boolean existsFrequencyControlHasNullOrder = frequencyControlList.stream().anyMatch(frequencyControl -> ObjectUtils.isEmpty(frequencyControl.getOrder()));
            boolean existsFrequencyControlHasNullKey = frequencyControlList.stream().anyMatch(frequencyControl -> ObjectUtils.isEmpty(frequencyControl.getKey()));
            AssertUtil.isFalse(existsFrequencyControlHasNullOrder, "限流策略的Order字段不允许出现空值");
            AssertUtil.isFalse(existsFrequencyControlHasNullKey, "限流策略的Key字段不允许出现空值");
            /*
            编程式注解需要构建(List<FrequencyControlDTO> 包括每个频控的前缀prefix 一般如果同一个方法调用的话前缀相同
            频控的对象 target 如果为Key代表业务参数 则FrequencyControlDTO的Key字段一定要赋值 如果target为Ip或者UID的话会从上下文取值 Key字段无需传值
            time unit 和count代表 频控多长时间限制多少次
            order属性一定要传值 如果构建多个频率控制对象从1开始至递增
            */
            // Key的构建规则 前缀prefix:index:frequencyControl的order字段：frequencyControl的key字段
            frequencyControlList.forEach(frequencyControl -> frequencyControl.setKey(getKeyPrefix() + ":index:" + frequencyControl.getOrder() + ":" + frequencyControl.getKey()));
        }
        Map<String, FrequencyControlDTO> frequencyControlDTOMap = frequencyControlList.stream().collect(Collectors.groupingBy(FrequencyControlDTO::getKey, Collectors.collectingAndThen(Collectors.toList(), list -> list.get(0))));
        return executeWithFrequencyControlMap(frequencyControlDTOMap, supplier);
    }

    /**
     * 单限流策略的调用方法-编程式调用
     *
     * @param frequencyControl 单个频控对象
     * @param supplier         服务提供着
     * @return 业务方法执行结果
     * @throws Throwable
     */
    public <T> T executeWithFrequencyControl(FrequencyControlDTO frequencyControl, SupplierThrow<T> supplier) throws Throwable {
        return executeWithFrequencyControlList(Collections.singletonList(frequencyControl), supplier);
    }

    /**
     * 限流的异常处理 默认情况是抛出异常 子类可以覆盖返回自定义异常信息 比如GPT返回“我现在太忙了~~稍后再试哦~”
     *
     * @param <T> 限流的异常返回信息
     * @return
     */
    public <T> T errorHandleWhenLimited() {
        throw new BusinessException(CommonErrorEnum.FREQUENCY_LIMIT);
    }

    /**
     * @return false-直接使用外面的Key true-由内部根据prefix拼接 除了注解切面类 编程式调用都应该是true
     */
    public abstract boolean generateRedisKey();

    /**
     * 获取Key的前缀
     *
     * @return Key的前缀
     */
    public abstract String getKeyPrefix();


    @FunctionalInterface
    public interface SupplierThrow<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }

}
