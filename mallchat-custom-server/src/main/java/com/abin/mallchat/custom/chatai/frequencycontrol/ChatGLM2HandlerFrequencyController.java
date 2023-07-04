package com.abin.mallchat.custom.chatai.frequencycontrol;

import com.abin.mallchat.common.common.constant.RedisKey;
import com.abin.mallchat.common.common.service.frequecycontrol.AbstractFrequencyControlService;
import com.abin.mallchat.common.common.utils.RedisUtils;
import com.abin.mallchat.custom.chatai.dto.FrequencyControlWithUidDTO;
import com.abin.mallchat.custom.chatai.properties.ChatGLM2Properties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.abin.mallchat.common.common.constant.RedisKey.USER_GLM2_TIME_LAST;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Service
@Data
public class ChatGLM2HandlerFrequencyController extends AbstractFrequencyControlService<FrequencyControlWithUidDTO> {

    @Autowired
    private ChatGLM2Properties glm2Properties;


    @Override
    protected boolean reachRateLimit(Map<String, FrequencyControlWithUidDTO> frequencyControlMap) {
        // 理论上只会有一个
        FrequencyControlWithUidDTO frequencyControlWithUidDTO = new ArrayList<>(frequencyControlMap.values()).get(0);
        Long uid = frequencyControlWithUidDTO.getUid();
        // 获取用户最后聊天时间
        Date lastChatTime = RedisUtils.get(RedisKey.getKey(USER_GLM2_TIME_LAST, uid), Date.class);
        if (lastChatTime == null) {
            // 如果没有聊天记录，则可以立即聊天
            return false;
        }
        // 计算当前时间和上次聊天时间之间的时间差
        long now = System.currentTimeMillis();
        long lastChatTimeMillis = lastChatTime.getTime();
        long durationMillis = now - lastChatTimeMillis;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        // 计算剩余等待时间
        long remainingMinutes = glm2Properties.getMinute() - minutes;
        // 这里塞入 让外面可以拿到
        frequencyControlWithUidDTO.setRemainingMinutes(remainingMinutes);
        return remainingMinutes > 0;
    }

    @Override
    protected void addFrequencyControlStatisticsCount(Map<String, FrequencyControlWithUidDTO> frequencyControlMap) {

    }
}
