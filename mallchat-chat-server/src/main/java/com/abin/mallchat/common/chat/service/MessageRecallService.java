package com.abin.mallchat.common.chat.service;

import com.abin.mallchat.common.chat.domain.vo.request.ChatMessageBaseReq;

/**
 * Description: 消息撤回服务
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
public interface MessageRecallService {

    void recallMsg(Long uid, ChatMessageBaseReq request);
}
