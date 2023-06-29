package com.abin.mallchat.custom.openai.event.listener;

import com.abin.mallchat.common.chat.dao.MessageDao;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.custom.openai.event.OpenAIEvent;
import com.abin.mallchat.custom.openai.service.IOpenAIService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.abin.mallchat.custom.openai.service.impl.OpenAIServiceImpl.MALL_CHAT_AI_NAME;

/**
 * 是否AI回复监听器
 *
 * @author zhaoyuhang
 * @date 2023/06/29
 */
@Slf4j
@Component
public class OpenAIListener {
    @Autowired
    private IOpenAIService openAIService;
    @Autowired
    private MessageDao messageDao;

    @TransactionalEventListener(classes = OpenAIEvent.class, fallbackExecution = true)
    public void notifyAllOnline(@NotNull OpenAIEvent event) {
        Message message = messageDao.getById(event.getMsgId());
        if (ATedAI(message)) {
            openAIService.chat(message);
        }
    }

    /**
     * @return boolean
     * @了AI
     */
    private boolean ATedAI(Message message) {
        /* 前端传@信息后取消注释 */

//        MessageExtra extra = message.getExtra();
//        if (extra == null) {
//            return false;
//        }
//        if (CollectionUtils.isEmpty(extra.getAtUidList())) {
//            return false;
//        }
//        if (!extra.getAtUidList().contains(OpenAIServiceImpl.AI_USER_ID)) {
//            return false;
//        }

        if (StringUtils.isBlank(message.getContent())) {
            return false;
        }
        return StringUtils.contains(message.getContent(), "@" + MALL_CHAT_AI_NAME)
                && StringUtils.isNotBlank(message.getContent().replace(MALL_CHAT_AI_NAME, "").trim());
    }

}
