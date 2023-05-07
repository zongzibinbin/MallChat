package com.abin.mallchat.custom.common.event.listener;

import com.abin.mallchat.common.chat.dao.MessageDao;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatMessageResp;
import com.abin.mallchat.common.common.event.MessageSendEvent;
import com.abin.mallchat.custom.chat.service.ChatService;
import com.abin.mallchat.custom.user.service.WebSocketService;
import com.abin.mallchat.custom.user.service.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 消息发送监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class MessageSendListener {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private MessageDao messageDao;

    @Async
    @EventListener(classes = MessageSendEvent.class)
    public void notifyAllOnline(MessageSendEvent event) {
        Message message = messageDao.getById(event.getMsgId());
        ChatMessageResp msgResp = chatService.getMsgResp(message, null);
        webSocketService.sendToAllOnline(WSAdapter.buildMsgSend(msgResp), message.getFromUid());
    }

}
