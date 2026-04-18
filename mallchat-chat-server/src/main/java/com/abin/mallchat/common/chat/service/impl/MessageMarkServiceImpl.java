package com.abin.mallchat.common.chat.service.impl;

import com.abin.mallchat.common.chat.domain.enums.MessageMarkActTypeEnum;
import com.abin.mallchat.common.chat.domain.vo.request.ChatMessageMarkReq;
import com.abin.mallchat.common.chat.service.MessageMarkService;
import com.abin.mallchat.common.chat.service.strategy.mark.AbstractMsgMarkStrategy;
import com.abin.mallchat.common.chat.service.strategy.mark.MsgMarkFactory;
import com.abin.mallchat.common.common.annotation.RedissonLock;
import org.springframework.stereotype.Service;

/**
 * Description: 消息标记服务实现
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
@Service
public class MessageMarkServiceImpl implements MessageMarkService {

    @Override
    @RedissonLock(key = "#uid")
    public void setMsgMark(Long uid, ChatMessageMarkReq request) {
        AbstractMsgMarkStrategy strategy = MsgMarkFactory.getStrategyNoNull(request.getMarkType());
        switch (MessageMarkActTypeEnum.of(request.getActType())) {
            case MARK:
                strategy.mark(uid, request.getMsgId());
                break;
            case UN_MARK:
                strategy.unMark(uid, request.getMsgId());
                break;
        }
    }
}
