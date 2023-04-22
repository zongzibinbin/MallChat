package com.abin.mallchat.custom.common.event.listener;

import com.abin.mallchat.common.chat.dao.MessageDao;
import com.abin.mallchat.common.chat.dao.MessageMarkDao;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.abin.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.abin.mallchat.common.common.domain.enums.IdempotentEnum;
import com.abin.mallchat.common.user.domain.enums.ItemEnum;
import com.abin.mallchat.common.user.service.IUserBackpackService;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageMarkReq;
import com.abin.mallchat.custom.common.event.MessageMarkEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 消息标记监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class MessageMarkListener {
    @Autowired
    private MessageMarkDao messageMarkDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private IUserBackpackService iUserBackpackService;

    @Async
    @EventListener(classes = MessageMarkEvent.class)
    public void changeMsgType(MessageMarkEvent event) {
        ChatMessageMarkReq req = event.getReq();
        Message msg = messageDao.getById(req.getMsgId());
        if (!Objects.equals(msg, MessageTypeEnum.NORMAL.getType())) {//普通消息才需要升级
            return;
        }
        //消息被标记次数
        Integer markCount = messageMarkDao.getMarkCount(req.getMsgId(), req.getMarkType());
        MessageMarkTypeEnum markTypeEnum = MessageMarkTypeEnum.of(req.getMarkType());
        if (markCount < markTypeEnum.getRiseNum()) {
            return;
        }
        boolean updateSuccess = messageDao.riseOptimistic(msg.getId(), msg.getType(), markTypeEnum.getRiseEnum().getType());
        if (MessageMarkTypeEnum.LIKE.getType().equals(req.getMarkType()) && updateSuccess) {//尝试给用户发送一张徽章
            iUserBackpackService.acquireItem(msg.getFromUid(), ItemEnum.LIKE_BADGE.getId(), IdempotentEnum.MSG_ID, msg.getId().toString());
        }
    }

}
