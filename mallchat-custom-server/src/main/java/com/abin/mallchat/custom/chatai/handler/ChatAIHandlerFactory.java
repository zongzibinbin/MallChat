package com.abin.mallchat.custom.chatai.handler;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatAIHandlerFactory {
    private static final Map<Long, AbstractChatAIHandler> CHATAI_ID_MAP = new ConcurrentHashMap<>();
    private static final Map<String, AbstractChatAIHandler> CHATAI_NAME_MAP = new ConcurrentHashMap<>();

    public static void register(Long aIUserId, String name, AbstractChatAIHandler chatAIHandler) {
        CHATAI_ID_MAP.put(aIUserId, chatAIHandler);
        CHATAI_NAME_MAP.put(name, chatAIHandler);
    }

    public static AbstractChatAIHandler getChatAIHandlerById(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return null;
        }
        for (Long userId : userIds) {
            AbstractChatAIHandler chatAIHandler = CHATAI_ID_MAP.get(userId);
            if (chatAIHandler != null) {
                return chatAIHandler;
            }
        }
        return null;
    }
    public static  AbstractChatAIHandler getChatAIHandlerByName(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        for (Map.Entry<String, AbstractChatAIHandler> entry : CHATAI_NAME_MAP.entrySet()) {
            if (text.contains("@"+entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
