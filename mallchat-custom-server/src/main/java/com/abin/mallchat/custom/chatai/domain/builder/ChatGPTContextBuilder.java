package com.abin.mallchat.custom.chatai.domain.builder;

import com.abin.mallchat.custom.chatai.domain.ChatGPTContext;

public class ChatGPTContextBuilder {

    public static ChatGPTContext initContext(Long uid, Long roomId) {
        ChatGPTContext chatGPTContext = new ChatGPTContext();
        chatGPTContext.setUid(uid);
        chatGPTContext.setRoomId(roomId);
        chatGPTContext.addMsg(ChatGPTMsgBuilder.systemPrompt());
        return chatGPTContext;
    }

}
