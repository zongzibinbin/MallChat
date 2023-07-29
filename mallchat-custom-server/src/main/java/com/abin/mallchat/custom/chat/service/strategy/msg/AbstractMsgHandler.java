package com.abin.mallchat.custom.chat.service.strategy.msg;

import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageReq;

import javax.annotation.PostConstruct;

/**
 * Description: 消息处理器抽象类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
public abstract class AbstractMsgHandler {

    @PostConstruct
    private void init() {
        MsgHandlerFactory.register(getMsgTypeEnum().getType(), this);
    }

    /**
     * 消息类型
     */
    abstract MessageTypeEnum getMsgTypeEnum();

    /**
     * 校验消息——保存前校验
     */
    public abstract void checkMsg(ChatMessageReq req, Long uid);

    /**
     * 保存消息
     */
    public abstract void saveMsg(Message msg, ChatMessageReq req);

    /**
     * 展示消息
     */
    public abstract Object showMsg(Message msg);

    /**
     * 被回复时——展示的消息
     */
    public abstract Object showReplyMsg(Message msg);

    /**
     * 会话列表——展示的消息
     */
    public abstract String showContactMsg(Message msg);

}
