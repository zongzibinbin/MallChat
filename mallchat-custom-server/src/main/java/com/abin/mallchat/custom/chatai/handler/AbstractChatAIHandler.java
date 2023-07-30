package com.abin.mallchat.custom.chatai.handler;

import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.abin.mallchat.common.common.config.ThreadPoolConfig;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageReq;
import com.abin.mallchat.custom.chat.domain.vo.request.msg.TextMsgReq;
import com.abin.mallchat.custom.chat.service.ChatService;
import com.abin.mallchat.custom.user.domain.vo.response.user.UserInfoResp;
import com.abin.mallchat.custom.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Slf4j
public abstract class AbstractChatAIHandler {
    @Autowired
    @Qualifier(ThreadPoolConfig.AICHAT_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    protected ChatService chatService;
    @Autowired
    protected UserService userService;

    @PostConstruct
    protected void init() {
        if (isUse()) {
            ChatAIHandlerFactory.register(getChatAIUserId(), this);
        }
    }

    /**
     * 是否启用
     *
     * @return boolean
     */
    protected abstract boolean isUse();

    // 获取机器人id
    public abstract Long getChatAIUserId();

    public void chat(Message message) {
        if (!supports(message)) {
            return;
        }
        threadPoolTaskExecutor.execute(() -> {
            String text = doChat(message);
            if (StringUtils.isNotBlank(text)) {
                answerMsg(text, message);
            }
        });
    }

    /**
     * 支持
     *
     * @param message 消息
     * @return boolean true 支持 false 不支持
     */
    protected abstract boolean supports(Message message);

    /**
     * 执行聊天
     *
     * @param message 消息
     * @return {@link String} AI回答的内容
     */
    protected abstract String doChat(Message message);


    protected void answerMsg(String text, Message replyMessage) {
        UserInfoResp userInfo = userService.getUserInfo(replyMessage.getFromUid());
        text = "@" + userInfo.getName() + " " + text;
        if (text.length() < 800) {
            save(text, replyMessage);
        } else {
            int maxLen = 800;
            int len = text.length();
            int count = (len + maxLen - 1) / maxLen;

            for (int i = 0; i < count; i++) {
                int start = i * maxLen;
                int end = Math.min(start + maxLen, len);
                save(text.substring(start, end), replyMessage);
            }
        }
    }

    private void save(String text, Message replyMessage) {
        Long roomId = replyMessage.getRoomId();
        Long uid = replyMessage.getFromUid();
        Long id = replyMessage.getId();
        ChatMessageReq answerReq = new ChatMessageReq();
        answerReq.setRoomId(roomId);
        answerReq.setMsgType(MessageTypeEnum.TEXT.getType());
        TextMsgReq textMsgReq = new TextMsgReq();
        textMsgReq.setContent(text);
        textMsgReq.setReplyMsgId(replyMessage.getId());
        textMsgReq.setAtUidList(Collections.singletonList(uid));
        answerReq.setBody(textMsgReq);
        chatService.sendMsg(answerReq, getChatAIUserId());
    }

}
