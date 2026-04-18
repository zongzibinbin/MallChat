package com.abin.mallchat.common.chat.service;

import com.abin.mallchat.common.chat.domain.vo.request.ChatMessageMarkReq;

/**
 * Description: 消息标记服务
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
public interface MessageMarkService {

    void setMsgMark(Long uid, ChatMessageMarkReq request);
}
