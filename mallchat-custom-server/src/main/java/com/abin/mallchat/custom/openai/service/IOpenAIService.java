package com.abin.mallchat.custom.openai.service;

import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageReq;

public interface IOpenAIService {


    void chat(ChatMessageReq chatMessageReq, Long uid);
    void chat(Message message);
}
