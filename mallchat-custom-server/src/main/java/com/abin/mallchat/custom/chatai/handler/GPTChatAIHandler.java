package com.abin.mallchat.custom.chatai.handler;

import cn.hutool.http.HttpResponse;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.common.constant.RedisKey;
import com.abin.mallchat.common.common.utils.DateUtils;
import com.abin.mallchat.common.common.utils.RedisUtils;
import com.abin.mallchat.custom.chatai.properties.ChatGPTProperties;
import com.abin.mallchat.custom.chatai.utils.ChatGPTUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GPTChatAIHandler extends AbstractChatAIHandler {

    @Autowired
    private ChatGPTProperties chatGPTProperties;

    @Override
    public Long getChatAIUserId() {
        return chatGPTProperties.getAIUserId();
    }

    @Override
    public String getChatAIName() {
        if (StringUtils.isNotBlank(chatGPTProperties.getAIUserName())) {
            return chatGPTProperties.getAIUserName();
        }
        String name = userService.getUserInfo(chatGPTProperties.getAIUserId()).getName();
        chatGPTProperties.setAIUserName(name);
        return name;
    }

    @Override
    protected String doChat(Message message) {
        String content = message.getContent().replace("@" +chatGPTProperties.getAIUserName(), "").trim();
        Long uid = message.getFromUid();
        Long chatNum;
        String text;
        if ((chatNum = userChatNumInrc(uid)) > chatGPTProperties.getLimit()) {
            text = "你今天已经和我聊了" + chatNum + "次了，我累了，明天再聊吧";
        } else {
            HttpResponse response = ChatGPTUtils.create(chatGPTProperties.getKey())
                    .proxyUrl(chatGPTProperties.getProxyUrl())
                    .model(chatGPTProperties.getModelName())
                    .prompt(content)
                    .send();
            text = ChatGPTUtils.parseText(response);
        }
        return text;
    }

    private Long userChatNumInrc(Long uid) {
        //todo:白名单
        return RedisUtils.inc(RedisKey.getKey(RedisKey.USER_CHAT_NUM, uid), DateUtils.getEndTimeByToday().intValue(), TimeUnit.MILLISECONDS);
    }


    @Override
    protected boolean supports(Message message) {
        if (!chatGPTProperties.isUse()) {
            return false;
        }
        /* 前端传@信息后取消注释 */

//        MessageExtra extra = message.getExtra();
//        if (extra == null) {
//            return false;
//        }
//        if (CollectionUtils.isEmpty(extra.getAtUidList())) {
//            return false;
//        }
//        if (!extra.getAtUidList().contains(ChatAIServiceImpl.AI_USER_ID)) {
//            return false;
//        }

        if (StringUtils.isBlank(message.getContent())) {
            return false;
        }
        return StringUtils.contains(message.getContent(), "@" + chatGPTProperties.getAIUserName())
                && StringUtils.isNotBlank(message.getContent().replace(chatGPTProperties.getAIUserName(), "").trim());
    }
}
