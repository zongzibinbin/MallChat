package com.abin.mallchat.common.chatai.service;

import com.abin.mallchat.common.chat.domain.entity.Message;

public interface IChatAIService {

    void chat(Message message);
}
