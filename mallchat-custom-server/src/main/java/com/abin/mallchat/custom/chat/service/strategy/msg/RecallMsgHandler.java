package com.abin.mallchat.custom.chat.service.strategy.msg;

import com.abin.mallchat.common.chat.dao.MessageDao;
import com.abin.mallchat.common.chat.domain.dto.ChatMsgRecallDTO;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.entity.msg.MessageExtra;
import com.abin.mallchat.common.chat.domain.entity.msg.MsgRecall;
import com.abin.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.abin.mallchat.common.chat.service.cache.MsgCache;
import com.abin.mallchat.common.common.event.MessageRecallEvent;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.service.cache.UserCache;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * Description: 撤回文本消息
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
@Component
public class RecallMsgHandler extends AbstractMsgHandler {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private UserCache userCache;
    @Autowired
    private MsgCache msgCache;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.RECALL;
    }

    @Override
    public void checkMsg(ChatMessageReq request, Long uid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveMsg(Message msg, ChatMessageReq request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object showMsg(Message msg) {//todo 后期让前端来做
        MsgRecall recall = msg.getExtra().getRecall();
        if (!Objects.equals(recall.getRecallUid(), msg.getFromUid())) {
            User userInfo = userCache.getUserInfo(recall.getRecallUid());
            return "管理员\"" + userInfo.getName() + "\"撤回了一条成员消息";
        }
        User userInfo = userCache.getUserInfo(msg.getFromUid());
        return "\"" + userInfo.getName() + "\"撤回了一条消息";
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "原消息已被撤回";
    }

    public void recall(Long recallUid, Message message) {//todo 消息覆盖问题用版本号解决
        MessageExtra extra = message.getExtra();
        extra.setRecall(new MsgRecall(recallUid, new Date()));
        Message update = new Message();
        update.setId(message.getId());
        update.setType(MessageTypeEnum.RECALL.getType());
        update.setExtra(extra);
        messageDao.updateById(update);
        applicationEventPublisher.publishEvent(new MessageRecallEvent(this, new ChatMsgRecallDTO(message.getId(), message.getRoomId(), recallUid)));

    }

    @Override
    public String showContactMsg(Message msg) {
        return "撤回了一条消息";
    }
}
