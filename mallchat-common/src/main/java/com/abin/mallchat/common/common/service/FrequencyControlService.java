package com.abin.mallchat.common.common.service;

import com.abin.mallchat.common.common.domain.dto.FrequencyControlDTO;
import com.abin.mallchat.common.common.exception.BusinessException;
import com.abin.mallchat.common.common.exception.CommonErrorEnum;
import com.abin.mallchat.common.common.utils.RedisUtils;
import com.abin.mallchat.common.common.utils.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
/**
 * 频率控制注解的服务实现
 */
public class FrequencyControlService {
    /**
     * @param frequencyControlMap 定义的注解频控 Map中的Key-对应redis的单个频控的Key Map中的Value-对应redis的单个频控的Key限制的Value
     * @param supplier            函数式入参-代表每个频控方法执行的不同的业务逻辑
     * @return 业务方法执行的返回值
     * @throws Throwable
     */
    private <T> T executeWithFrequencyControl(Map<String, FrequencyControlDTO> frequencyControlMap, FrequencyControlService.SupplierThrow<T> supplier) throws Throwable {
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
                throw new BusinessException(CommonErrorEnum.FREQUENCY_LIMIT);
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
     * 编程式调用传入
     *
     * @param frequencyControlList 频控列表 包含每一个频率控制的定义以及顺序
     * @param supplier             函数式入参-代表每个频控方法执行的不同的业务逻辑
     * @param needRebuildKey       false-用于频控注解 表示Key外面由注解构建好了 完全不用生成Key true-用于手动式编程调用 需要根据序号生成Key 编程式注解只能传true 而且还要定义List<FrequencyControlDTO>
     * @return 业务方法执行的返回值
     * @throws Throwable
     */
    public <T> T executeWithFrequencyControl(List<FrequencyControlDTO> frequencyControlList, SupplierThrow<T> supplier, boolean needRebuildKey) throws Throwable {
        //如果为false 用于注解调用 外面已经构建好了Key的情况
        if (needRebuildKey) {
            /*
        编程式注解需要构建(List<FrequencyControlDTO> 包括每个频控的前缀prefix 一般如果同一个方法调用的话前缀相同
        频控的对象 target 如果为Key代表业务参数 则FrequencyControlDTO的Key字段一定要赋值 如果target为Ip或者UID的话会从上下文取值 Key字段无需传值
        time unit 和count代表 频控多长时间限制多少次
        order属性一定要传值 如果构建多个频率控制对象从1开始至递增
        举例子：编程式调用：
        见下面main方法
        */
            for (FrequencyControlDTO frequencyControl : frequencyControlList) {
                String prefix = frequencyControl.getPrefix() + ":index:" + frequencyControl.getOrder() + ":";
                String key = "";
                switch (frequencyControl.getTarget()) {
                    case UID:
                        key = RequestHolder.get().getUid().toString();
                        break;
                    case IP:
                        key = RequestHolder.get().getIp();
                        break;
                    case STRING:
                        key = frequencyControl.getKey();
                }
                frequencyControl.setKey(prefix + key);
            }
        }
        Map<String, FrequencyControlDTO> frequencyControlDTOMap = frequencyControlList.stream().collect(Collectors.groupingBy(FrequencyControlDTO::getKey, Collectors.collectingAndThen(Collectors.toList(), list -> list.get(0))));
        return executeWithFrequencyControl(frequencyControlDTOMap, supplier);
    }


    @FunctionalInterface
    public interface SupplierThrow<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }

    public static void main(String[] args) throws Throwable {
        // 举例子 我要定义两个频控注解如下
        List<FrequencyControlDTO> frequencyControlList = new ArrayList<>(4);
        String prefix = "TEST";
        // 编程式调用 定义两个频控注解从1到2
        frequencyControlList.add(new FrequencyControlDTO(prefix, null, FrequencyControlDTO.Target.UID, 60, TimeUnit.SECONDS, 50, 1));
        frequencyControlList.add(new FrequencyControlDTO(prefix, null, FrequencyControlDTO.Target.UID, 100, TimeUnit.SECONDS, 70, 2));
        FrequencyControlService frequencyControlService = new FrequencyControlService();
        // 表示手动调用频控注解
        frequencyControlService.executeWithFrequencyControl(frequencyControlList, () -> "return test", true);
    }


}
