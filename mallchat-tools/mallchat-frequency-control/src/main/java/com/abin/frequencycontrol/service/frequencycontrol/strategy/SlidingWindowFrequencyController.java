package com.abin.frequencycontrol.service.frequencycontrol.strategy;

import com.abin.mallchat.common.FrequencyControlConstant;
import com.abin.mallchat.utils.RedisUtils;
import com.abin.frequencycontrol.domain.dto.SlidingWindowDTO;
import com.abin.frequencycontrol.service.frequencycontrol.AbstractFrequencyControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;



/**
 * 抽象类频控服务 -使用redis实现 滑动窗口是一种更加灵活的频率控制策略，它在一个滑动的时间窗口内限制操作的发生次数
 */
@Slf4j
@Service
public class SlidingWindowFrequencyController extends AbstractFrequencyControlService<SlidingWindowDTO> {
    @Override
    protected boolean reachRateLimit(Map<String, SlidingWindowDTO> frequencyControlMap) {
        // 批量获取redis统计的值
        List<String> frequencyKeys = new ArrayList<>(frequencyControlMap.keySet());
        for (int i = 0; i < frequencyKeys.size(); i++) {
            String key = frequencyKeys.get(i);
            SlidingWindowDTO controlDTO = frequencyControlMap.get(key);
            // 获取窗口时间内计数
            Long count = RedisUtils.ZSetGet(key);
            int frequencyControlCount = controlDTO.getCount();
            if (Objects.nonNull(count) && count >= frequencyControlCount) {
                //频率超过了
                log.warn("frequencyControl limit key:{},count:{}", key, count);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void addFrequencyControlStatisticsCount(Map<String, SlidingWindowDTO> frequencyControlMap) {
        List<String> frequencyKeys = new ArrayList<>(frequencyControlMap.keySet());
        for (int i = 0; i < frequencyKeys.size(); i++) {
            String key = frequencyKeys.get(i);
            SlidingWindowDTO controlDTO = frequencyControlMap.get(key);
            // 窗口最小周期转秒
            long period = controlDTO.getUnit().toMillis(controlDTO.getPeriod());
            long current = System.currentTimeMillis();
            // 窗口大小 单位 秒
            long length = period * controlDTO.getWindowSize();
            long start = current - length;
//            long expireTime = length + period;
            RedisUtils.ZSetAddAndExpire(key, start, length, current);
        }
    }

    @Override
    protected String getStrategyName() {
        return FrequencyControlConstant.SLIDING_WINDOW;

    }
}
