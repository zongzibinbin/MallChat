package com.abin.mallchat.custom.chatai.handler;

import cn.hutool.http.HttpResponse;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.entity.msg.MessageExtra;
import com.abin.mallchat.common.common.constant.RedisKey;
import com.abin.mallchat.common.common.utils.RedisUtils;
import com.abin.mallchat.custom.chatai.properties.ChatGLM2Properties;
import com.abin.mallchat.custom.chatai.utils.ChatGLM2Utils;
import com.abin.mallchat.custom.user.domain.vo.response.user.UserInfoResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.abin.mallchat.common.common.constant.RedisKey.USER_GLM2_TIME_LAST;

@Slf4j
@Component
public class ChatGLM2Handler extends AbstractChatAIHandler {

    private static final List<String> ERROR_MSG = Arrays.asList(
            "还摸鱼呢？你不下班我还要下班呢。。。。",
            "没给钱，矿工了。。。。",
            "服务器被你们玩儿坏了。。。。",
            "你们这群人，我都不想理你们了。。。。",
            "艾特我那是另外的价钱。。。。",
            "得加钱");


    private static final Random RANDOM = new Random();

    private static String AI_NAME;

    @Autowired
    private ChatGLM2Properties glm2Properties;

    @Override
    protected void init() {
        super.init();
        if (isUse()) {
            UserInfoResp userInfo = userService.getUserInfo(glm2Properties.getAIUserId());
            if (userInfo == null) {
                log.error("根据AIUserId:{} 找不到用户信息", glm2Properties.getAIUserId());
                throw new RuntimeException("根据AIUserId找不到用户信息");
            }
            if (StringUtils.isBlank(userInfo.getName())) {
                log.warn("根据AIUserId:{} 找到的用户信息没有name", glm2Properties.getAIUserId());
                throw new RuntimeException("根据AIUserId: " + glm2Properties.getAIUserId() + " 找到的用户没有名字");
            }
            AI_NAME = userInfo.getName();
        }
    }

    @Override
    protected boolean isUse() {
        return glm2Properties.isUse();
    }

    @Override
    public Long getChatAIUserId() {
        return glm2Properties.getAIUserId();
    }


    @Override
    protected String doChat(Message message) {
        String content = message.getContent().replace("@" + AI_NAME, "").trim();
        Long uid = message.getFromUid();
        Long minute;
        String text;
        if ((minute = userMinutesLater(uid)) > 0) {
            text = "你太快了 " + minute + "分钟后重试";
        } else {
            HttpResponse response = null;
            try {
                response = ChatGLM2Utils
                        .create()
                        .url(glm2Properties.getUrl())
                        .prompt(content)
                        .timeout(glm2Properties.getTimeout())
                        .send();
                text = ChatGLM2Utils.parseText(response);
            } catch (Exception e) {
                log.warn("glm2 doChat warn:", e);
                return getErrorText();
            }
            if (StringUtils.isNotBlank(text)) {
                RedisUtils.set(RedisKey.getKey(USER_GLM2_TIME_LAST, uid), new Date(), glm2Properties.getMinute(), TimeUnit.MINUTES);
            }
        }
        return text;
    }

    private static String getErrorText() {
        int index = RANDOM.nextInt(ERROR_MSG.size());
        return ERROR_MSG.get(index);
    }

    /**
     * 用户多少分钟后才能再次聊天
     *
     * @param uid
     * @return
     */
    private Long userMinutesLater(Long uid) {
        // 获取用户最后聊天时间
        Date lastChatTime = RedisUtils.get(RedisKey.getKey(USER_GLM2_TIME_LAST, uid), Date.class);
        if (lastChatTime == null) {
            // 如果没有聊天记录，则可以立即聊天
            return 0L;
        }
        // 计算当前时间和上次聊天时间之间的时间差
        long now = System.currentTimeMillis();
        long lastChatTimeMillis = lastChatTime.getTime();
        long durationMillis = now - lastChatTimeMillis;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        // 计算剩余等待时间
        long remainingMinutes = glm2Properties.getMinute() - minutes;
        return remainingMinutes > 0 ? remainingMinutes : 0L;
    }


    @Override
    protected boolean supports(Message message) {
        if (!glm2Properties.isUse()) {
            return false;
        }
        /* 前端传@信息后取消注释 */
        MessageExtra extra = message.getExtra();
        if (extra == null) {
            return false;
        }
        if (CollectionUtils.isEmpty(extra.getAtUidList())) {
            return false;
        }
        if (!extra.getAtUidList().contains(glm2Properties.getAIUserId())) {
            return false;
        }

        if (StringUtils.isBlank(message.getContent())) {
            return false;
        }
        return StringUtils.contains(message.getContent(), "@" + AI_NAME)
                && StringUtils.isNotBlank(message.getContent().replace(AI_NAME, "").trim());
    }
}
