package com.abin.mallchat.custom.common.event.listener;

import com.abin.mallchat.common.chat.dao.MessageDao;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.common.event.MessageSendEvent;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatMessageResp;
import com.abin.mallchat.custom.chat.service.ChatService;
import com.abin.mallchat.custom.chat.service.WeChatMsgOperationService;
import com.abin.mallchat.custom.chat.service.impl.WeChatMsgOperationServiceImpl;
import com.abin.mallchat.custom.chatai.service.IChatAIService;
import com.abin.mallchat.custom.user.service.WebSocketService;
import com.abin.mallchat.custom.user.service.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

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
    @Autowired
    private IChatAIService openAIService;

    @Autowired
    WeChatMsgOperationService weChatMsgOperationService;

    @Async
    @TransactionalEventListener(classes = MessageSendEvent.class, fallbackExecution = true)
    public void notifyAllOnline(MessageSendEvent event) {
        Message message = messageDao.getById(event.getMsgId());
        ChatMessageResp msgResp = chatService.getMsgResp(message, null);
        webSocketService.sendToAllOnline(WSAdapter.buildMsgSend(msgResp), message.getFromUid());
    }

    @TransactionalEventListener(classes = MessageSendEvent.class, fallbackExecution = true)
    public void handlerMsg(@NotNull MessageSendEvent event) {
        Message message = messageDao.getById(event.getMsgId());
        openAIService.chat(message);
    }

    @TransactionalEventListener(classes = MessageSendEvent.class, fallbackExecution = true)
    public void publishChatToWechat(@NotNull MessageSendEvent event) {
        Message message = messageDao.getById(event.getMsgId());
        if (Objects.nonNull(message.getExtra().getAtUidList())) {
            weChatMsgOperationService.publishChatMsgToWeChatUser(message.getFromUid(), message.getExtra().getAtUidList(),
                    message.getContent());
        }
    }
}
