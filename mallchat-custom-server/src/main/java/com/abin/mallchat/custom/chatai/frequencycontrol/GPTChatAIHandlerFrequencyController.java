package com.abin.mallchat.custom.chatai.frequencycontrol;

import com.abin.mallchat.common.common.constant.RedisKey;
import com.abin.mallchat.common.common.service.frequecycontrol.AbstractFrequencyControlService;
import com.abin.mallchat.common.common.utils.DateUtils;
import com.abin.mallchat.common.common.utils.RedisUtils;
import com.abin.mallchat.custom.chatai.dto.FrequencyControlWithUidDTO;
import com.abin.mallchat.custom.chatai.properties.ChatGPTProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Service
@Data
public class GPTChatAIHandlerFrequencyController extends AbstractFrequencyControlService<FrequencyControlWithUidDTO> {


    @Autowired
    private ChatGPTProperties chatGPTProperties;

    @Override
    protected boolean reachRateLimit(Map<String, FrequencyControlWithUidDTO> frequencyControlMap) {
        // 理论上只会有一个
        FrequencyControlWithUidDTO frequencyControlWithUidDTO = new ArrayList<>(frequencyControlMap.values()).get(0);
        Long uid = frequencyControlWithUidDTO.getUid();
        Long num = RedisUtils.get(RedisKey.getKey(RedisKey.USER_CHAT_NUM, uid), Long.class);
        long chatNum = (num == null ? 0 : num);
        frequencyControlWithUidDTO.setChatNum(chatNum);
        return chatNum > chatGPTProperties.getLimit();
    }

    @Override
    protected void addFrequencyControlStatisticsCount(Map<String, FrequencyControlWithUidDTO> frequencyControlMap) {
        // 理论上只会有一个
        FrequencyControlWithUidDTO frequencyControlWithUidDTO = new ArrayList<>(frequencyControlMap.values()).get(0);
        Long uid = frequencyControlWithUidDTO.getUid();
        // 增加用户聊天次数
        RedisUtils.inc(RedisKey.getKey(RedisKey.USER_CHAT_NUM, uid), DateUtils.getEndTimeByToday().intValue(), TimeUnit.MILLISECONDS);
    }
}
